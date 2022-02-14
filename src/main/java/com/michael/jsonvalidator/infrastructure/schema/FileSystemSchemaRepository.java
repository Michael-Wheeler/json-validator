package com.michael.jsonvalidator.infrastructure.schema;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.RawValue;
import com.michael.jsonvalidator.domain.schema.SchemaDeserializer;
import com.michael.jsonvalidator.infrastructure.exception.DatabaseConnectionException;
import com.michael.jsonvalidator.domain.schema.Schema;
import com.michael.jsonvalidator.infrastructure.exception.AlreadyExistsException;
import com.michael.jsonvalidator.infrastructure.exception.EntryNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileSystemSchemaRepository implements SchemaRepositoryInterface {
    private final ObjectMapper mapper;
    private final String jsonFilePath;

    public FileSystemSchemaRepository() {
        this.mapper = new ObjectMapper();
        registerCustomDeserializer();
        jsonFilePath = createDbFile();
    }

    public void createSchema(Schema schema) throws DatabaseConnectionException, JsonProcessingException, AlreadyExistsException {
        Schema fetched = null;

        try {
            fetched = getSchemaById(schema.getId());
        } catch (EntryNotFoundException e) {
            // No existing schema
        }

        if (fetched != null) {
            throw new AlreadyExistsException();
        }

        ArrayNode schemaJsonArray = readSchemaFile();

        String schemaJson = mapper.writeValueAsString(schema);
        schemaJsonArray.addRawValue(new RawValue(schemaJson));

        saveSchemaFile(schemaJsonArray);
    }

    public List<Schema> getAllSchemas() throws DatabaseConnectionException {
        try {
            InputStream jsonStream = new DataInputStream(new FileInputStream(jsonFilePath));

            String json = new BufferedReader(new InputStreamReader(jsonStream, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            return mapper.readValue(json, new TypeReference<List<Schema>>() {
            });
        } catch (IOException e) {
            throw new DatabaseConnectionException();
        }
    }

    public Schema getSchemaById(String id) throws DatabaseConnectionException, EntryNotFoundException {
        List<Schema> schemas = getAllSchemas();

        return schemas.stream()
                .filter(i -> id.equals(i.getId()))
                .findAny()
                .orElseThrow(EntryNotFoundException::new);
    }

    public void deleteSchemaById(String id) throws EntryNotFoundException, DatabaseConnectionException {
        List<Schema> schemas = getAllSchemas();

        Schema schema = schemas.stream()
                .filter(i -> id.equals(i.getId()))
                .findAny()
                .orElseThrow(EntryNotFoundException::new);

        schemas.remove(schema);

        try {
            PrintWriter writer = new PrintWriter(jsonFilePath);
            writer.print(mapper.writeValueAsString(schemas));
            writer.close();
        } catch (IOException e) {
            throw new DatabaseConnectionException();
        }
    }

    private ArrayNode readSchemaFile() throws DatabaseConnectionException {
        try {
            InputStream jsonStream = new DataInputStream(new FileInputStream(jsonFilePath));

            String json = new BufferedReader(
                    new InputStreamReader(jsonStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            return (ArrayNode) mapper.readTree(json);
        } catch (IOException e) {
            throw new DatabaseConnectionException();
        }
    }

    private void saveSchemaFile(ArrayNode schemaJsonArray) throws DatabaseConnectionException {
        try {
            PrintWriter writer = new PrintWriter(jsonFilePath);
            writer.print(schemaJsonArray);
            writer.close();
        } catch (IOException e) {
            throw new DatabaseConnectionException();
        }
    }

    private void registerCustomDeserializer() {
        SimpleModule module = new SimpleModule("CustomSchemaDeserializer");
        module.addDeserializer(Schema.class, new SchemaDeserializer());
        mapper.registerModule(module);
    }

    private String createDbFile() {
        final String jsonFilePath;
        File classFile = new File(FileSystemSchemaRepository.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        // Handle test vs dev env
        jsonFilePath = classFile.getPath().equals("/json-schema-validator-with-dependencies.jar")
                ? "/database/db.json"
                : Paths.get(classFile.getPath()) + "/../db.json";

        File f = new File(jsonFilePath);
        if (!f.exists()) {
            try {
                PrintWriter writer = new PrintWriter(jsonFilePath, "UTF-8");
                writer.println("[]");
                writer.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return jsonFilePath;
    }
}
