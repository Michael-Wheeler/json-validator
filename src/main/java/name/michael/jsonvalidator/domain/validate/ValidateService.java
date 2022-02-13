package name.michael.jsonvalidator.domain.validate;

import com.fasterxml.jackson.databind.node.ObjectNode;
import name.michael.jsonvalidator.domain.schema.Schema;
import name.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import name.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;
import name.michael.jsonvalidator.infrastructure.schema.FileSystemSchemaRepository;
import name.michael.jsonvalidator.infrastructure.schema.SchemaRepositoryInterface;

public class ValidateService {
    private SchemaRepositoryInterface repo;

    public ValidateService(SchemaRepositoryInterface repo) {
        this.repo = repo;
    }

    public String validateJson(ObjectNode json, String schemaId) throws DatabaseConnectionException, EntryNotFoundException {
        Schema schema = new FileSystemSchemaRepository().getSchemaById(schemaId);

    }
}
