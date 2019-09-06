# Simple web service

## Introduction

REST APIs to retrieve all products
    Pagination
    Product by name
REST APIs to retrieve all Orders
    Pagination
    Orders by name

## How to deploy?

1. Make sure that you have Docker installed on your local machine
2. Once you checkout the project do a mvn clean install
3. If you want to run Integration tests you need to install mongodb locally on ubuntu(https://hevodata.com/blog/install-mongodb-on-ubuntu/). 

```
mvn clean install -DskipTests
docker run -d -p 27017:27017 --name mongo mongo
docker build -t webservice:0.1.0 .
docker run -p 8080:8080 --name webservice --link mongo webservice:0.1.0
```

## URLs for health check

``` http://localhost:8080/actuator/health ```


## Swagger
http://localhost:8080/v2/api-docs


## Things could be added or improved
Metrices
Alerts
Java Melody
Product Attributes
OAuth security







    
    
    