package com.example.labtranslator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class TestNoFound {

    //---------- Тест на неверный http адрес (левый порт) ----------

    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost:3322"; // недоступный порт
        RestAssured.basePath = "/translator";
    }

    @Test
    public void testNotFound() {
        String requestBody = "text=Hello&sourceLang=en&targetLang=ru";

        Response response = RestAssured.given()
                .contentType(ContentType.URLENC)
                .body(requestBody)
                .when()
                .post();
        response.then()
                .statusCode(404)
                .body(equalTo("Невозможно соединиться с удаленным сервером"));
    }
}