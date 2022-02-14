package name.michael.jsonvalidator.domain.validate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import name.michael.jsonvalidator.domain.schema.Schema;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import name.michael.jsonvalidator.infrastructure.schema.SchemaRepositoryInterface;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;

public class ValidateServiceTest {
    private SchemaRepositoryInterface repo = mock(SchemaRepositoryInterface.class);
    private ValidateService service = new ValidateService(repo);

    @Test
    public void testValidateJsonShouldReturnNullOnSuccess() throws JsonProcessingException, DatabaseConnectionException, EntryNotFoundException {
        String schemaJson = "{\"type\":\"object\",\"properties\":{\"rectangle\":{\"$ref\":\"#/definitions/Rectangle\"}},\"definitions\":{\"size\":{\"type\":\"number\",\"minimum\":0},\"Rectangle\":{\"type\":\"object\",\"properties\":{\"a\":{\"$ref\":\"#/definitions/size\"},\"b\":{\"$ref\":\"#/definitions/size\"}}}}}";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schemaObject = mapper.readTree(schemaJson).deepCopy();

        when(repo.getSchemaById("test")).thenReturn(new Schema("to-delete", schemaObject));

        String testJson = "{\"rectangle\":{\"a\":5,\"b\":2}}";
        ObjectNode testObject = mapper.readTree(testJson).deepCopy();

        String out = service.validateJson(testObject, "test");

        assertNull(out);
    }

    @Test
    public void testValidateJsonShouldReturnSingleErrorMessage() throws JsonProcessingException, DatabaseConnectionException, EntryNotFoundException {
        String schemaJson = "{\"type\":\"object\",\"properties\":{\"rectangle\":{\"$ref\":\"#/definitions/Rectangle\"}},\"definitions\":{\"size\":{\"type\":\"number\",\"minimum\":0},\"Rectangle\":{\"type\":\"object\",\"properties\":{\"a\":{\"$ref\":\"#/definitions/size\"},\"b\":{\"$ref\":\"#/definitions/size\"}}}}}";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schemaObject = mapper.readTree(schemaJson).deepCopy();

        when(repo.getSchemaById("test")).thenReturn(new Schema("to-delete", schemaObject));

        String testJson = "{\"rectangle\":{\"a\":5,\"b\":\"invalid\"}}";
        ObjectNode testObject = mapper.readTree(testJson).deepCopy();

        String out = service.validateJson(testObject, "test");

        assertEquals("#/rectangle/b: expected type: Number, found: String", out);
    }

    @Test
    public void testValidateJsonShouldReturnMultiplErrorMessages() throws JsonProcessingException, DatabaseConnectionException, EntryNotFoundException {
        String schemaJson = "{\"type\":\"object\",\"properties\":{\"rectangle\":{\"$ref\":\"#/definitions/Rectangle\"}},\"definitions\":{\"size\":{\"type\":\"number\",\"minimum\":0},\"Rectangle\":{\"type\":\"object\",\"properties\":{\"a\":{\"$ref\":\"#/definitions/size\"},\"b\":{\"$ref\":\"#/definitions/size\"}}}}}";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode schemaObject = mapper.readTree(schemaJson).deepCopy();

        when(repo.getSchemaById("test")).thenReturn(new Schema("to-delete", schemaObject));

        String testJson = "{\"rectangle\":{\"a\":-5,\"b\":\"invalid\"}}";
        ObjectNode testObject = mapper.readTree(testJson).deepCopy();

        String out = service.validateJson(testObject, "test");

        assertEquals(
                "#/rectangle/a: -5 is not greater or equal to 0, #/rectangle/b: expected type: Number, found: String",
                out
        );
    }
}

