package com.example.labtranslator.model;

//---------- Класс LabRequest - это структура данных
//  для хранения в БД. Запрос на перевод. ----------
public class LabRequest {
    //----- Поля -----
    private Long id; // ID запроса
    private String ip; // IP пользователя
    private String inputText; // Входная строка для перевода
    private String outputText; // Результат перевода

    //----- Методы -----
    //--- Методы для получения значений полей объекта (геттеры) ---
    // Для получения id
    public Long getId() {
        return id;
    }
    // Для получения ip
    public String getIp() {
        return ip;
    }
    // Для получения inputText
    public String getInputText() {
        return inputText;
    }
    // Для получения translatedText
    public String getOutputText() {
        return outputText;
    }

    //--- Методы для для установки значений полей объекта (сеттеры) ---
    // Для установки id
    public void setId(Long id) {
        this.id = id;
    }
    // Для установки ip
    public void setIp(String ip) {
        this.ip = ip;
    }
    // Для установки inputText
    public void setInputText(String inputText) {
        this.inputText = inputText;
    }
    // Для установки outputText
    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }
}