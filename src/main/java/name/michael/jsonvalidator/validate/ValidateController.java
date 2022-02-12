package name.michael.jsonvalidator.validate;

import io.javalin.http.Context;

public class ValidateController {
    public static void validateJson(Context ctx) {
        ctx.result("Validated");
    }
}
