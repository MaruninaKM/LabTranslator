package com.example.labtranslator;

import com.example.labtranslator.service.LabService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;


public class TestConnectionError {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private LabService labService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
//---------- Тест на недоступный ресерс перевода ----------
    @Test
    public void testTranslateText_ServiceUnavailable() {
        String ip = "127.0.0.1";
        String inputText = "Hello world";
        String sourceLang = "en";
        String targetLang = "ru";

        doThrow(new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE))
                .when(restTemplate).exchange(
                        "https://translate.api.cloud.yandex.net/translate/v2/bybybybybybybyb",
                        HttpMethod.POST,
                        labService.createHttpEntity(
                                "{\"folderId\": \"b1g47p3oa9i874cvc61q\", \"texts\": [\"Hello\"], \"targetLanguageCode\": \"ru\"}"
                        ),
                        String.class
                );

        ResponseEntity<String> response = labService.translateText(ip, inputText, sourceLang, targetLang);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("http 400 Ошибка доступа к ресурсу перевода", response.getBody());
    }
}