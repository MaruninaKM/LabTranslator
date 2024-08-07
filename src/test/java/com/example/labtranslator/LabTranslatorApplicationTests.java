package com.example.labtranslator;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class LabTranslatorApplicationTests {

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/translator";
    }

    //---------- Тест на корректную работу ----------
    @Test
    public void testStatusOK() {
        String requestBody = "text=Hello world, this is my first program&sourceLang=en&targetLang=ru";
        RestAssured.given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(200) // Ожидаемый статус-код
                .body(equalTo("http 200 Здравствуйте мир, этот является мой первый программа")); // Ожидаемый текст ответа
    }

    //---------- Тест с отсутствием входной строки ----------
    @Test
    public void testNoText() {
        String requestBody = "sourceLang=en&targetLang=ru";
        RestAssured.given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400) // Ожидаемый статус-код для отсутствующего текста
                .body(equalTo("http 400 Текст не найден"));
    }

    //---------- Тест с отсутствием языка исходной строки ----------
    @Test
    public void testNoSourceLang() {
        String requestBody = "text=Hello world, this is my first program&targetLang=ru";
        RestAssured.given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400) // Ожидаемый статус-код для отсутствующего исходного языка
                .body(equalTo("http 400 Не найден язык исходного сообщения"));
    }

    //---------- Тест с отсутствием языка перевода ----------
    @Test
    public void testNoTargetLang() {
        String requestBody = "text=Hello world, this is my first program&sourceLang=en";
        RestAssured.given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                .when()
                .post()
                .then()
                .statusCode(400) // Ожидаемый статус-код для отсутствующего целевого языка
                .body(equalTo("http 400 Не указан целевой язык перевода"));
    }

}