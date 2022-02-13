package name.michael.jsonvalidator.application.schema;

import io.javalin.http.Context;
import name.michael.jsonvalidator.infrastructure.schema.SchemaRepositoryInterface;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class SchemaControllerTest {
    private Context ctx = mock(Context.class);
    private SchemaRepositoryInterface repo = mock(SchemaRepositoryInterface.class);

    @Test
    public void testCreateSchemaShouldCreateSchemaAndReturn201() {
        when(ctx.queryParam("schema-id")).thenReturn("id");
//        when(repo.createSchema();)
        SchemaController.createSchema(ctx);
        verify(ctx).status(201);
    }
}