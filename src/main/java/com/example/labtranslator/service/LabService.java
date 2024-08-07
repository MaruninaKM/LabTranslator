package com.example.labtranslator.service;

import com.example.labtranslator.model.LabRequest;
import com.example.labtranslator.repository.LabRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

//---------- Класс LabService - это логика переводчика а основе яндекс переводчика ----------
@Service
public class LabService {
    //----- Поля -----
    @Autowired
    private LabRepository repository; // Для работы с БД
    private RestTemplate restTemplate = new RestTemplate(); // Для выполнения HTTP-запросов
    private final String url = "https://translate.api.cloud.yandex.net/translate/v2/translate"; // URL яндекс переводчика
    private final String key = "AQVNygC1uFvo_jtYgS-m4CYYNPo5B9AGGkEDrbJr";  // API-ключ для авторизации в яндекс cloud
    private final String catalog = "b1g47p3oa9i874cvc61q"; // id каталога в яндекс cloud для доступа к переводчику
    private final int MAX_THREADS = 10; // Число одновременно работающих потоков не должно превышать 10
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS); // Потоки для параллельных задач

    //----- Методы -----
    //--- Метод для перевода текста ---
    public ResponseEntity<String> translateText(String ip, String inputText, String sourceLang, String targetLang) {
        String[] words = inputText.split(" ");// Разбивает строку на слова
        final boolean[] flag = {false};  // Флаг для ошибок

        List<Future<String>> parallel = List.of(words).stream() // Параллельный перевод по словам
                .map(word -> executor.submit(() -> {
                    // Выполнение и отлов ошибки (установка флага)
                    try {
                        return translateWord(word, sourceLang, targetLang);
                    } catch (Exception e) {
                        flag[0] = true;
                        return null;
                    }
                }))
                .collect(Collectors.toList());

        List<String> listWords = parallel.stream() // Список переведенных слов
                .map(future -> {
                    try {
                        return future.get();
                    } catch (Exception e) {
                        flag[0] = true;
                        return null;
                    }
                }).collect(Collectors.toList());
        // Проверка состояния флага
        if (flag[0]) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Обработка ошибки
                    .body("http 400 Ошибка доступа к ресурсу перевода");
        }
        for (int i = 0; i < listWords.size(); i++) {
            if (listWords.get(i) == null) {
                listWords.set(i, words[i]);
            }
        }
        String translatedText = String.join(" ", listWords); // Склейка слов в строку

        // Объект для БД
        LabRequest request = new LabRequest();
        request.setIp(ip);
        request.setInputText(inputText);
        request.setOutputText(translatedText);

        try {
            repository.save(request); // Сохранение запроса в БД
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("http 500 Ошибка при сохранении в базу данных: " + e.getMessage());
        }
        // Случай успеха
        return ResponseEntity.ok("http 200 " + translatedText);
    }

    //--- Метод для перевода одного слова ---
    private String translateWord(String word, String sourceLang, String targetLang) throws Exception {
        // JSON для запроса
        String requestJson = String.format("{\"folderId\": \"%s\", \"texts\": [\"%s\"], \"targetLanguageCode\": \"%s\"}", catalog, word, targetLang);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, createHttpEntity(requestJson), String.class); // POST-запрос
            HttpStatus statusCode = HttpStatus.valueOf(responseEntity.getStatusCode().value());
            if (statusCode == HttpStatus.OK) {
                String response = responseEntity.getBody(); // Переведенный текст
                return extractTranslatedText(response);
            } else {
                throw new Exception("http " + statusCode.value() + " " + ErrorMessage(statusCode));
            }
        } catch (HttpClientErrorException e) {
            // Обработка ошибок клиента
            HttpStatus statusCode = HttpStatus.valueOf(e.getStatusCode().value());
            throw new Exception("http " + statusCode.value() + " " + ErrorMessage(statusCode));
        } catch (HttpServerErrorException e) {
            // Обработка ошибок сервера
            HttpStatus statusCode = HttpStatus.valueOf(e.getStatusCode().value());
            throw new Exception("http " + statusCode.value() + " " + ErrorMessage(statusCode));
        } catch (Exception e) {
            // и тд
            throw new Exception("http 500 Ошибка при получении перевода: " + e.getMessage());
        }
    }

    //--- Метод для сообщений об ошибках ---
    private String ErrorMessage(HttpStatus statusCode) {
        switch (statusCode) {
            case BAD_REQUEST:
                return "Не найден язык исходного сообщения";
            case UNAUTHORIZED:
                return "Ошибка доступа к ресурсу перевода";
            case NOT_FOUND:
                return "Ресурс не найден";
            default:
                return "Неизвестная ошибка: " + statusCode.value();
        }
    }

    //--- Метод для создания HttpEntity ---
    public HttpEntity<String> createHttpEntity(String requestJson) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json"); // Заголовок говорит серверу, что тело запроса содержит данные в формате JSON
        headers.set("Authorization", "Api-Key " + key); // "Authorization" с API-ключом для авторизации (так было написано в документации к яндекс переводчику)
        return new HttpEntity<>(requestJson, headers); // requestJson содержит тело запроса в формате JSON
    }

    //--- Метод для получения переведенного текста из JSON-ответа ---
    private String extractTranslatedText(String response) throws Exception {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(response);
            return jsonObject.getJSONArray("translations").getJSONObject(0).getString("text");
        } catch (org.json.JSONException e) {
            throw new Exception("Ошибка при извлечении перевода: " + e.getMessage());
        }
    }
}