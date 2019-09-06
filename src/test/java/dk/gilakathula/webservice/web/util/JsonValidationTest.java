package dk.gilakathula.webservice.web.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import dk.gilakathula.webservice.util.JsonValidationStatus;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import dk.gilakathula.webservice.web.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

public class JsonValidationTest {
    CustomJsonValidator customJsonValidator;

    @Before
    public void setUp() throws IOException, ProcessingException {
        customJsonValidator = new CustomJsonValidator("/schemas");
    }

    @Test
    public void product() throws IOException, ProcessingException {
        final JsonValidationStatus product = customJsonValidator.validate("product", TestHelper.create("product.json", JsonNode.class).toString());
        assertTrue(product.isValid());
    }

    @Test
    public void order() throws IOException, ProcessingException {
        final JsonValidationStatus orderCreated = customJsonValidator.validate("order", TestHelper.create("order.json", JsonNode.class).toString());
        assertTrue(orderCreated.isValid());
    }

}
