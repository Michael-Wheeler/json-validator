package name.michael.jsonvalidator.application.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Iterator;

public class JsonCleaner {
    public static JsonNode removeNullValues(JsonNode json) {
        Iterator<JsonNode> nodes = json.elements();
        while (nodes.hasNext()) {
            JsonNode item = nodes.next();
            if (item.isArray() || item.isObject()) {
                removeNullValues(item);
            }

            if (item.isNull()) {
                nodes.remove();
            }
        }

        return json;
    }
}
