package com.michael.jsonvalidator.application;

import io.javalin.Javalin;
import com.michael.jsonvalidator.application.schema.SchemaController;
import com.michael.jsonvalidator.application.validate.ValidateController;

public class HttpServer {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);

        app.get("/api/v1/schema/<schema-id>", SchemaController::getSchemaById);
        app.post("/api/v1/schema/<schema-id>", SchemaController::createSchema);
        app.post("/api/v1/validate/<schema-id>", ValidateController::validateJson);
    }
}