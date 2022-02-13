package name.michael.jsonvalidator.application.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.http.Context;

public class ResponseUtil {
    public static Context createErrorJsonResponse(Context ctx, String action, String id, String message) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("action", action);
        rootNode.put("id", id);
        rootNode.put("status", "error");
        rootNode.put("message", message);

        return ctx.json(rootNode);
    }

    public static Context createSuccessJsonResponse(Context ctx, String action, String id) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("action", action);
        rootNode.put("id", id);
        rootNode.put("status", "success");

        return ctx.json(rootNode);
    }
}
