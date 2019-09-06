package dk.gilakathula.webservice.web.products;


import dk.gilakathula.webservice.web.TestHelper;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ProductTest {

    @Test
    public void json() throws IOException {
        Product product = TestHelper.create("product.json",Product.class);
        assertNotNull(product);

        final String productAsString = TestHelper.getObjectMapper().writeValueAsString(product);
        final Product read = TestHelper.getObjectMapper().readValue(productAsString, Product.class);
        assertEquals(product,read);
    }


}
