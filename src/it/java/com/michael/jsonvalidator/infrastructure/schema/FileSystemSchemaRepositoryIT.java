package com.michael.jsonvalidator.infrastructure.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.michael.jsonvalidator.infrastructure.exception.AlreadyExistsException;
import com.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import com.michael.jsonvalidator.domain.schema.Schema;
import com.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class FileSystemSchemaRepositoryIT {
    FileSystemSchemaRepository repo = new FileSystemSchemaRepository();

    @Test
    public void testCreateSchemaShouldAppendToFile() throws DatabaseConnectionException, JsonProcessingException, EntryNotFoundException, AlreadyExistsException {
        int initialCount = repo.getAllSchemas().size();

        String json = "{ \"id\" : 123, \"description\" : \"test\" }";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.readTree(json).deepCopy();
        repo.createSchema(new Schema("new-id", objectNode));

        assertEquals(initialCount + 1, repo.getAllSchemas().size());
        repo.deleteSchemaById("new-id");
    }

    @Test
    public void testDeleteSchemaByIdShouldRemoveSchemaFromFile() throws DatabaseConnectionException, EntryNotFoundException, JsonProcessingException, AlreadyExistsException {
        String json = "{ \"id\" : 123, \"description\" : \"test\" }";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.readTree(json).deepCopy();
        repo.createSchema(new Schema("to-delete", objectNode));

        assertNotNull(repo.getSchemaById("to-delete"));

        repo.deleteSchemaById("to-delete");

        assertThrows(EntryNotFoundException.class, () -> {
            repo.getSchemaById("to-delete");
        });
    }

    @Test
    public void testGetSchemaByIdShouldReturnCorrectSchema() throws DatabaseConnectionException, EntryNotFoundException, JsonProcessingException, AlreadyExistsException {
        String json = "{ \"id\" : 123, \"schema\" : \"test\" }";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode = mapper.readTree(json).deepCopy();
        repo.createSchema(new Schema("unique", objectNode));

        Schema schema = repo.getSchemaById("unique");
        assertEquals("unique", schema.getId());
        assertEquals("{\"id\":123,\"schema\":\"test\"}", schema.getSchema().toString());
    }

    @BeforeEach
    public void cleanDatabase() throws DatabaseConnectionException {
        List<Schema> schemas = repo.getAllSchemas();

        schemas.forEach(schema -> {
            try {
                repo.deleteSchemaById(schema.getId());
            } catch (EntryNotFoundException | DatabaseConnectionException e) {
                //
            }
        });
    }
}
