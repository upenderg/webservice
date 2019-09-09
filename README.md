# Simple web service

## Introduction

Simple web service provides a way to create a product and place an order using the REST APIS.
At the moment its not protected by any security mechanism.

Here are the REST APIS:

### Products API
 - Basic CRUD Operations
### Orders API
 - Get Orders by days
 - Create an Order
    

## How to deploy?

1. Make sure that you have Docker installed on your local machine
2. If you want to run Integration tests you need to install mongodb locally on ubuntu(https://hevodata.com/blog/install-mongodb-on-ubuntu/). 
3. Else if you want to simple run the application on your local machine please follow these steps

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
- Pagination
- Java Melody
- Product Attributes
- Customer Info
- OAuth security
- Metrices
- Alerts








    
    
    