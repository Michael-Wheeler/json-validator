package name.michael.jsonvalidator.domain.validate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public String validateJson(JsonNode inputJson, String schemaId) throws DatabaseConnectionException, EntryNotFoundException {
        Schema schema = repo.getSchemaById(schemaId);

        try {
            JSONObject schemaObject = jsonNodeToJsonObject(schema.getSchema());
            JSONObject inputJsonObject = jsonNodeToJsonObject(inputJson);

            org.everit.json.schema.Schema validator = SchemaLoader.load(schemaObject);
            validator.validate(inputJsonObject);
        } catch (JsonProcessingException e) {
            // Never thrown as casting ObjectNode to JsonObject, and so we have already checked it is valid JSON
        } catch (ValidationException e) {
            return e.getCausingExceptions().size() == 0 ? e.getMessage() : buildValidationError(e.getCausingExceptions());
        }

        return null;
    }

    private JSONObject jsonNodeToJsonObject(JsonNode node) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return new JSONObject(mapper.writeValueAsString(node));
    }

    private String buildValidationError(List<ValidationException> errors) {
        StringBuilder builder = new StringBuilder();

        for (ValidationException e : errors) {
//            Pattern p = Pattern.compile("\\W(found: Null)\\W");
//            Matcher m = p.matcher(e.getMessage());
//
//            if (m.find()) {
//                continue;
//            }

            builder.append(e.getMessage()).append(", ");
        }

        String errorMessage = builder.toString();
        return errorMessage.substring(0, errorMessage.length() - 2);
    }
}

