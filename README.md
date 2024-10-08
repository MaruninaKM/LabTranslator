-------------------------------------------------------------- 
---------- LabTranslator - веб-приложение на языке Java для перевода набора слов на другой язык с использованием стороннего сервиса перевода Яндекс. ----------
-------------------------------------------------------------- 

---------- Функции ---------- 

Перевод текста между различными языками.
Обработка и сохранение запросов и ответов в базу данных.
Обработка ошибок при недоступности внешнего сервиса.
Настройка и запуск

---------- Требования ---------- 

- Java 22
- Maven
- Spring Boot

---------- База данных ---------- 

В проекте использовалась база данных MySQL.
Файл со структурой и данными представлен в data.sql
ER-диаграмма представлена в файле diagrom.mvb
Подключение к базе данных прописано в файле \src\main\resourcesapplication.properties

-------------------------------------------------------------- 
Файловая структура
-------------------------------------------------------------- 

├───.idea
├───.mvn
│   └───wrapper
├───src
│   ├───main
│   │   ├───java
│   │   │   └───com
│   │   │       └───example
│   │   │           └───labtranslator
│   │   │               ├───controller
│   │   │               ├───model
│   │   │               ├───repository
│   │   │               └───service
│   │   └───resources
│   │       ├───static
│   │       └───templates
│   └───test
│       └───java
│           └───com
│               └───example
│                   └───labtranslator
└───target
    ├───classes
    │   └───com
    │       └───example
    │           └───labtranslator
    │               ├───controller
    │               ├───model
    │               ├───repository
    │               └───service
    ├───generated-sources
    │   └───annotations
    ├───generated-test-sources
    │   └───test-annotations
    ├───maven-status
    │   └───maven-compiler-plugin
    │       ├───compile
    │       │   └───default-compile
    │       └───testCompile
    │           └───default-testCompile
    ├───surefire-reports
    └───test-classes
        └───com
            └───example
                └───labtranslator
                
--------------------------------------------------------------                 
---------- Установка ---------- 
-------------------------------------------------------------- 

https://github.com/MaruninaKM/labtranslator.git
cd labtranslator
mvn clean install
mvn spring-boot:run

---------- Переводчик ---------- 

В данной работе использовался Яндекс переводчик
https://translate.api.cloud.yandex.net/translate/v2/translate


---------- API ---------- 

POST /translator

--- Параметры запроса ---

text (обязательный) — текст для перевода.
sourceLang (обязательный) — исходный язык текста.
targetLang (обязательный) — целевой язык перевода.

--- Пример запроса через командную строку --- 

C:\Users\sumarn>curl -X POST "http://localhost:8080/translator?text=Hello%20world,%20this%20is%20my%20first%20program&sourceLang=en&targetLang=ru"

Ответ: Здравствуйте мир, этот является мой первый программа

--- Пример запроса через терминал --- 

PS C:\Users\yourname\IdeaProjects\LabTranslator> Invoke-RestMethod -Uri "http://localhost:8080/translator" `
>>     -Body @{ text = "Hello world, this is my first program"; sourceLang = "en"; targetLang = "ru" } `
>>     -ContentType "application/x-www-form-urlencoded"

Ответ: http 200 Здравствуйте мир, этот является мой первый программа

---------- Тестирование ---------- 

Функиональные тесты прописаны в \src\test\java\com\example\labtranslator
Тесты охватывают:
- Верная работа программы
- Отсутствие строки для перевода
- Отсутствие исходного языка
- Отсутствие целевого языка
- Неверный порт
- Ошибка подключения к ресурсу перевода
Помимо этого в коде LabService обработаны все необходимые ошибки и исключения
Запустить тесты:
mvn test
