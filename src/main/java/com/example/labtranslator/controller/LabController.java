package com.example.labtranslator.controller;

import com.example.labtranslator.service.LabService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

// Класс является REST-контроллером
@RestController
// Базовый путь для всех методов контроллера
@RequestMapping("/translator")

public class LabController {
    @Autowired
    private LabService translationService;
    // Обрабатывает POST-запросы
    @PostMapping
    public ResponseEntity<String> translate(
            // Для получения параметров запроса из URL или тела запроса.
            @RequestParam(required = false) String text,
            @RequestParam(required = false) String sourceLang,
            @RequestParam(required = false) String targetLang,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr(); // Получение IP-адреса из запроса
        // IPv6 в IPv4 адрес (чтобы выглядел например так 127.0.0.1 а не так 0:0:0:0:0:0:0:1)
        if (ip != null && ip.contains(":")) {
            ip = ip.substring(ip.lastIndexOf(":") + 1);
        }
        // Возвращение сообщений если были ошибки
        if (text == null || text.isEmpty()) {
            return ResponseEntity.badRequest().body("http 400 Текст не найден");
        }
        if (sourceLang == null || sourceLang.isEmpty()) {
            return ResponseEntity.badRequest().body("http 400 Не найден язык исходного сообщения");
        }
        if (targetLang == null || targetLang.isEmpty()) {
            return ResponseEntity.badRequest().body("http 400 Не указан целевой язык перевода");
        }
        try {
            // Вызов метода перевода из LabService
            return translationService.translateText(ip, text, sourceLang, targetLang);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("http 500 Ошибка при обработке запроса: " + e.getMessage());
        }
    }
}