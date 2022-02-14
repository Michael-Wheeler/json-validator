# JSON Validator

This API which enables the uploading of JSON schemas to be used for the validation of JSON documents.

### Libraries and tools:
* Javalin web framework
* Jackson JSON parser
* JUnit and Mockito for testing
* Docker
* Java 8
* Custom filesystem storage repository

### System requirements
* Maven
* Docker

### To run
* mvn clean package
* docker-compose up

The server will now be running on [`localhost:7000`](http://localhost:7000).

### Not included
As it has been a while since I have developed with Java, there were a few features which I have left out which I would ideally have added but that do not prevent the application from meeting the defined requirements. The main shortcut was the use of a filesystem repository. I would have used a NoSQL database (MongoDB) in this scenario as we do not require relationships between tables and would easily scale horizontally to manage the large number of schemas we may need to store.

The full list of omissions is:
* Caching
* Database (filesystem used for POC)
* Dependency injection
* Logging
* Rate limits
* Full testing suite. Tests have been used to aid development but some classes have not been fully tested and error paths are untested

### Example useage
Get a schema by its ID
```
curl --location --request GET 'localhost:7000/api/v1/schema/{schema-id}'
```

Create a new schema
```
curl --location --request POST 'localhost:7000/api/v1/schema/{schema-id}' \
--header 'Content-Type: application/json' \
--data-raw '{
  "$schema": "http://json-schema.org/draft-04/schema#",
  "type": "object",
  "properties": {
    "source": {
      "type": "string"
    },
    "destination": {
      "type": "string"
    },
    "timeout": {
      "type": "integer",
      "minimum": 0,
      "maximum": 32767
    },
    "chunks": {
      "type": "object",
      "properties": {
        "size": {
          "type": "integer"
        },
        "number": {
          "type": "integer"
        }
      },
      "required": ["size"]
    }
  },
  "required": ["source", "destination"]
}'
```

Validate JSON against a schema
```
curl --location --request POST 'localhost:7000/api/v1/validate/{schema-id}' \
--header 'Content-Type: application/json' \
--data-raw '{
  "source": "/home/alice/image.iso",
  "destination": "/mnt/storage",
  "timeout": null,
  "chunks": {
    "size": 1024,
    "number": null
  }
}'
```
