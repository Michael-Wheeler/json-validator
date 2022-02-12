package name.michael.jsonvalidator.schema;

import io.javalin.http.Context;

public class SchemaController {
    private SchemaService service;

    public SchemaController(SchemaService service) {
        this.service = service;
    }

    public static void createSchema(Context ctx) {
        ctx.result("created " + ctx.pathParam("schema-id"));
        ctx.status(201);
    }

    public static void getSchemaById(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");
        ctx.result(schemaId);
    }
}
