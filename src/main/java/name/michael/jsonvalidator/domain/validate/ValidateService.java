package name.michael.jsonvalidator.domain.validate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import name.michael.jsonvalidator.domain.schema.Schema;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import name.michael.jsonvalidator.infrastructure.schema.SchemaRepositoryInterface;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;

import java.util.List;

public class ValidateService {
    private final SchemaRepositoryInterface repo;

    public ValidateService(SchemaRepositoryInterface repo) {
        this.repo = repo;
    }

    public String validateJson(ObjectNode inputJson, String schemaId) throws DatabaseConnectionException, EntryNotFoundException {
//        Schema schema = new FileSystemSchemaRepository().getSchemaById(schemaId);
        Schema schema = repo.getSchemaById(schemaId);


        try {
            JSONObject schemaObject = objectNodeToJsonObject(schema.getSchema());
            JSONObject inputJsonObject = objectNodeToJsonObject(inputJson);

            org.everit.json.schema.Schema validator = SchemaLoader.load(schemaObject);
            validator.validate(inputJsonObject);
        } catch (JsonProcessingException e) {
            // Never thrown as casting ObjectNode to JsonObject, and so we have already checked it is valid JSON
        } catch (ValidationException e) {
            return e.getCausingExceptions().size() == 0 ? e.getMessage() : buildValidationError(e.getCausingExceptions());
        }

        return null;
    }

    private JSONObject objectNodeToJsonObject(ObjectNode node) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return new JSONObject(mapper.writeValueAsString(node));
    }

    private String buildValidationError(List<ValidationException> errors) {
        StringBuilder builder = new StringBuilder();

        for (ValidationException e : errors) {
            builder.append(e.getMessage()).append(", ");
        }

        String errorMessage = builder.toString();
        return errorMessage.substring(0, errorMessage.length() - 2);
    }
}

