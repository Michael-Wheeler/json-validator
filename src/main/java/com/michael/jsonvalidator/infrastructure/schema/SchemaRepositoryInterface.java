package com.michael.jsonvalidator.infrastructure.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.michael.jsonvalidator.infrastructure.exception.AlreadyExistsException;
import com.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import com.michael.jsonvalidator.domain.schema.Schema;
import com.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;

import java.util.List;

public interface SchemaRepositoryInterface {
    void createSchema(Schema schema) throws DatabaseConnectionException, JsonProcessingException, AlreadyExistsException;

    List<Schema> getAllSchemas() throws DatabaseConnectionException;

    Schema getSchemaById(String id) throws DatabaseConnectionException, EntryNotFoundException;

    void deleteSchemaById(String id) throws DatabaseConnectionException, EntryNotFoundException;
}
