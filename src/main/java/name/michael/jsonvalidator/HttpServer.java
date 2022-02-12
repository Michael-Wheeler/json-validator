package name.michael.jsonvalidator;

import io.javalin.Javalin;
import name.michael.jsonvalidator.schema.SchemaController;
import name.michael.jsonvalidator.validate.ValidateController;

public class HttpServer {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.enableDevLogging();
        }).start(7000);

        app.get("/hello", ctx -> ctx.result("Hello World"));
        app.get("/api/v1/schema/<schema-id>", SchemaController::getSchemaById);
        app.post("/api/v1/schema/<schema-id>", SchemaController::createSchema);
        app.post("/api/v1/validate/<schema-id>", ValidateController::validateJson);
    }
}