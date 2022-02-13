package name.michael.jsonvalidator.application.validate;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import name.michael.jsonvalidator.domain.schema.Schema;
import name.michael.jsonvalidator.domain.validate.ValidateService;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import name.michael.jsonvalidator.infrastructure.schema.FileSystemSchemaRepository;

import static name.michael.jsonvalidator.application.util.ResponseUtil.createErrorJsonResponse;

public class ValidateController {
    private ValidateService service;

    public ValidateController(ValidateService service) {
        this.service = service;
    }

    public static void validateJson(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");
        try {
            Schema schema = new FileSystemSchemaRepository().getSchemaById(schemaId);
        } catch (DatabaseConnectionException e) {
            ctx.status(HttpCode.INTERNAL_SERVER_ERROR);
            createErrorJsonResponse(ctx, "downloadSchema", schemaId, "Internal server error");
        } catch (EntryNotFoundException e) {
            ctx.status(HttpCode.NOT_FOUND);
            createErrorJsonResponse(ctx, "downloadSchema", schemaId, "Schema not found");
        }
    }
}
