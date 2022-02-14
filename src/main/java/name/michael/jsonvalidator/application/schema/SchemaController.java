package name.michael.jsonvalidator.application.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import name.michael.jsonvalidator.domain.schema.Schema;
import name.michael.jsonvalidator.infrastructure.exception.AlreadyExistsException;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import name.michael.jsonvalidator.infrastructure.schema.FileSystemSchemaRepository;
import name.michael.jsonvalidator.infrastructure.schema.SchemaRepositoryInterface;
import org.everit.json.schema.SchemaException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.util.List;

import static name.michael.jsonvalidator.application.util.ResponseUtil.createErrorJsonResponse;
import static name.michael.jsonvalidator.application.util.ResponseUtil.createSuccessJsonResponse;

public class SchemaController {
    SchemaRepositoryInterface repo;

    public SchemaController(SchemaRepositoryInterface repo) {
        this.repo = repo;
    }

    public static void createSchema(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schemaJson;

        try {
            schemaJson = (ObjectNode) mapper.readTree(ctx.body());
            JSONObject inputJsonObject = new JSONObject(mapper.writeValueAsString(schemaJson));
            SchemaLoader.load(inputJsonObject);
        } catch (JsonProcessingException e) {
            ctx.status(HttpCode.BAD_REQUEST);
            createErrorJsonResponse(ctx, "uploadSchema", schemaId, "Schema is invalid JSON");
            return;
        } catch (ClassCastException | SchemaException $e) {
            ctx.status(HttpCode.UNPROCESSABLE_ENTITY);
            createErrorJsonResponse(ctx, "uploadSchema", schemaId, "Invalid JSON schema provided");
            return;
        }

        Schema schema = new Schema(schemaId, schemaJson);

        try {
            new FileSystemSchemaRepository().createSchema(schema);
        } catch (DatabaseConnectionException | JsonProcessingException e) {
            ctx.status(HttpCode.INTERNAL_SERVER_ERROR);
            createErrorJsonResponse(ctx, "uploadSchema", schemaId, "Internal server error");
            return;
        } catch (AlreadyExistsException e) {
            ctx.status(HttpCode.CONFLICT);
            createErrorJsonResponse(ctx, "uploadSchema", schemaId, "Schema with given ID already exists");
            return;
        }

        ctx.status(HttpCode.CREATED);
        createSuccessJsonResponse(ctx, "uploadSchema", schemaId);
    }

    public static void getSchemaById(Context ctx) {
        String schemaId = ctx.pathParam("schema-id");
        try {
            Schema schema = new FileSystemSchemaRepository().getSchemaById(schemaId);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("action", "downloadSchema");
            rootNode.put("id", schemaId);
            rootNode.put("status", "success");
            rootNode.set("schema", schema.getSchema());

            ctx.json(rootNode);
        } catch (DatabaseConnectionException e) {
            ctx.status(HttpCode.INTERNAL_SERVER_ERROR);
            createErrorJsonResponse(ctx, "downloadSchema", schemaId, "Internal server error");
        } catch (EntryNotFoundException e) {
            ctx.status(HttpCode.NOT_FOUND);
            createErrorJsonResponse(ctx, "downloadSchema", schemaId, "Schema not found");
        }
    }

    public static void getAllSchemas(Context ctx) {
        try {
            List<Schema> schemas = new FileSystemSchemaRepository().getAllSchemas();
            ctx.json(new ObjectMapper().writeValueAsString(schemas));
        } catch (DatabaseConnectionException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
