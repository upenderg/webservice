package dk.gilakathula.webservice.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import static java.lang.String.format;

public class CustomJsonValidator {

    public static final String ORDER_TYPE = "order";
    public static final String PRODUCT_TYPE = "product";

    private static final Logger logger = LoggerFactory.getLogger(CustomJsonValidator.class);

    Multimap<String, JsonSchema> STRING_JSON_SCHEMA_MAP = ArrayListMultimap.create();
    Multimap<String, JsonNode> STRING_JSON_NODE_MAP = ArrayListMultimap.create();


    private static final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
    private String payloadValidationSchemaPath;

    public CustomJsonValidator(final String payloadValidationSchemaPath) throws IOException, ProcessingException {
        this.payloadValidationSchemaPath = payloadValidationSchemaPath;
        loadSchemas(payloadValidationSchemaPath);
    }

    private void loadSchemas(String payloadValidationSchemaPath) throws IOException, ProcessingException {
        logger.debug("Load Schemas load from resources directory");
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources;
        try {
            resources = resolver.getResources(payloadValidationSchemaPath + "/**.json");
            for (Resource resource : resources) {
                final InputStream inputStream = resource.getInputStream();
                final String fileName = resource.getFilename();
                String[] split = StringUtils.split(fileName, ".");
                JsonSchema jsonSchema = getJsonSchema(inputStream, fileName);
                STRING_JSON_SCHEMA_MAP.put(split[0], jsonSchema);
            }
        } catch (IOException | ProcessingException e) {
            logger.error("Unable to load schemas", e);
            throw e;
        }
    }


    /***
     * Returns true or false based on the validation payload with respect to type and the json schema
     *
     * @param type - type
     * @param payload   - Payload to be saved
     * @return - verifies the payload based on type json schema
     */
    public JsonValidationStatus validate(String type, String payload) throws IOException, ProcessingException {
        logger.debug("Type {} and payload {}", type, payload);
        Collection<JsonSchema> jsonSchemas = STRING_JSON_SCHEMA_MAP.get(type.toLowerCase());
        final JsonNode data = JsonLoader.fromString(payload);
        if (jsonSchemas == null) {
            logger.debug("Not in the cache, load from resources directory");
            loadSchemas(payloadValidationSchemaPath);
            jsonSchemas = STRING_JSON_SCHEMA_MAP.get(type.toLowerCase());
            if (jsonSchemas == null) {
                return unableToGetJSONSchemaValidationStatusMessage(type);
            }
        }

        JsonValidationStatus jsonValidationStatus = unableToGetJSONSchemaValidationStatusMessage(type);
        for (JsonSchema jsonSchema : jsonSchemas) {
            try {
                ListProcessingReport validate = (ListProcessingReport) jsonSchema.validate(data);
                jsonValidationStatus = jsonValidationStatus(validate);
                if (jsonValidationStatus != null && jsonValidationStatus.isValid())
                    return jsonValidationStatus;
            } catch (ProcessingException e) {
                logger.error("Unable to validate", e);
                throw e;
            }
        }

        return jsonValidationStatus;

    }

    private JsonValidationStatus unableToGetJSONSchemaValidationStatusMessage(String type) {
        return JsonValidationStatus.newJsonValidationStatus().status(false).message(format("Unable to get JSON Schemas for type %s", type.toLowerCase())).build();
    }

    private JsonValidationStatus jsonValidationStatus(ListProcessingReport validate) {
        return validate.isSuccess() ? getCorrectPayloadValidationStatus(validate) : getIncorrectPayloadJsonValidationStatus(validate);
    }

    private JsonValidationStatus getCorrectPayloadValidationStatus(ListProcessingReport validate) {
        return JsonValidationStatus.newJsonValidationStatus().status(validate.isSuccess()).message("Success").build();
    }

    private JsonValidationStatus getIncorrectPayloadJsonValidationStatus(ListProcessingReport validate) {
        final StringBuilder builder = new StringBuilder();
        if (validate != null) {
            for (ProcessingMessage aValidate : validate) {
                final JsonNode jsonNode = aValidate.asJson();
                final JsonNode missing = jsonNode.get("missing");
                builder.append(missing != null ? missing.toString().replace("\"", "") : aValidate.getMessage());
            }
        }

        final String format = format("Incorrect payload, missing %s", builder.toString());
        return JsonValidationStatus.newJsonValidationStatus().status(validate != null && validate.isSuccess()).message(format).build();
    }


    private JsonSchema getJsonSchema(InputStream inputStream, final String fileName) throws IOException, ProcessingException {
        try (Reader targetReader = new InputStreamReader(inputStream)) {
            JsonNode schemaNode = JsonLoader.fromReader(targetReader);
            STRING_JSON_NODE_MAP.put(fileName, schemaNode);
            return factory.getJsonSchema(schemaNode);
        }
    }
}
