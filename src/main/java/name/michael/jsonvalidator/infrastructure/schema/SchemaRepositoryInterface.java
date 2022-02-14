package name.michael.jsonvalidator.infrastructure.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import name.michael.jsonvalidator.domain.schema.Schema;
import name.michael.jsonvalidator.infrastructure.exception.AlreadyExistsException;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;

import java.util.List;

public interface SchemaRepositoryInterface {
    public void createSchema(Schema schema) throws DatabaseConnectionException, JsonProcessingException, AlreadyExistsException;

    public List<Schema> getAllSchemas() throws DatabaseConnectionException;

    public Schema getSchemaById(String id) throws DatabaseConnectionException, EntryNotFoundException;

    public void deleteSchemaById(String id) throws DatabaseConnectionException, EntryNotFoundException;
}
