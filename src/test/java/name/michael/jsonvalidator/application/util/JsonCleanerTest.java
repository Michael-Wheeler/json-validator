package name.michael.jsonvalidator.application.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static name.michael.jsonvalidator.application.util.JsonCleaner.removeNullValues;
import static org.testng.AssertJUnit.assertEquals;

public class JsonCleanerTest {

    @Test
    public void testremoveNullValuesShouldCleanJson() throws JsonProcessingException {
        String testJson = "{\"a\":-5,\"b\":null}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testObject = mapper.readTree(testJson).deepCopy();

        JsonNode cleanedJson = removeNullValues(testObject);
        assertEquals("{\"a\":-5}", cleanedJson.toString());
    }

    @Test
    public void testremoveNullValuesShouldCleanInNestedObject() throws JsonProcessingException {
        String testJson = "{\"source\":\"/home/alice/image.iso\",\"destination\":\"/mnt/storage\",\"timeout\":null,\"chunks\":{\"size\":1024,\"number\":null}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testObject = mapper.readTree(testJson).deepCopy();

        JsonNode cleanedJson = removeNullValues(testObject);
        assertEquals("{\"source\":\"/home/alice/image.iso\",\"destination\":\"/mnt/storage\",\"chunks\":{\"size\":1024}}", cleanedJson.toString());
    }

    @Test
    public void testremoveNullValuesShouldCleanInArray() throws JsonProcessingException {
        String testJson = "[{\"rectangle\":{\"a\":-5,\"b\":null}}]";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode testObject = mapper.readTree(testJson).deepCopy();

        JsonNode cleanedJson = removeNullValues(testObject);
        assertEquals("[{\"rectangle\":{\"a\":-5}}]", cleanedJson.toString());
    }
}
