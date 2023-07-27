## Rate Limiter Spring Boot Starter

This is a redis-based rate limiter for spring boot project.

## Motivation

Spring Cloud supports Redis rate limiter. I hope that it can also be conveniently used in Spring Boot. Therefore, this project has ported the limiter from Spring Cloud Gateway to Spring Boot and added some encapsulation. Please refer to the features section for more details.

## Features

* Support global rate limiting
* Support API prefix rate limiting
* Support method-level rate limiting
* Support custom rate limiting objects, such as user ID and user IP.
