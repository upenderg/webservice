package dk.gilakathula.webservice.web.products;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import dk.gilakathula.webservice.web.TestHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerIntegrationTest {
    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    CustomJsonValidator customJsonValidator;
    @Autowired
    ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port);
        mongoTemplate.dropCollection("products");
    }

    @After
    public void exit() throws Exception {
        mongoTemplate.dropCollection("products");
    }

    @Test
    public void getAll() throws IOException {
        ResponseEntity<String> getResponse = template.getForEntity(base.toString() + "/api/v1/products/", String.class);
        assertThat(getResponse.getBody(), equalTo("Unable to find any products"));

        final List<Product> productsJson = createNewProducts();

        getResponse = template.getForEntity(base.toString() + "/api/v1/products/", String.class);
        List<Product> products = TestHelper.getObjectMapper().readValue(getResponse.getBody(), new TypeReference<List<Product>>() {
        });
        assertThat(productsJson, equalTo(products));
    }

    private List<Product> createNewProducts() throws IOException {
        final List<Product> productsJson = getProductsJson("products.json");
        for (Product product : productsJson) {
            createNewProduct(product);
        }
        return productsJson;
    }

    @Test
    public void getOne() throws IOException {
        final Product product = getProductJson("product.json");
        ResponseEntity<String> responseCreate = createNewProduct(product);
        final String body = responseCreate.getBody();
        ResponseEntity<String> responseGet = template.getForEntity(base.toString() + "/api/v1/products/" + body, String.class);
        final Product received = TestHelper.getObjectMapper().readValue(responseGet.getBody(), Product.class);
        assertThat(received, equalTo(product));
        responseGet = template.getForEntity(base.toString() + "/api/v1/products/unknown", String.class);
        assertThat(responseGet.getStatusCode().value(), equalTo(HttpStatus.BAD_REQUEST.value()));
        responseGet = template.getForEntity(base.toString() + "/api/v1/products/2", String.class);
        assertThat(responseGet.getStatusCode().value(), equalTo(HttpStatus.NOT_FOUND.value()));
    }

    @Test
    public void save() throws IOException {
        ResponseEntity<String> response = createNewProduct(getProductJson("product.json"));
        final BigInteger bigInteger = TestHelper.getObjectMapper().readValue(response.getBody(), BigInteger.class);
        assertNotNull(bigInteger);
    }

    @Test
    public void update() throws IOException {
        ResponseEntity<String> response = createNewProduct(getProductJson("product.json"));
        final BigInteger id = TestHelper.getObjectMapper().readValue(response.getBody(), BigInteger.class);

        final Product updateProductNameJson = getProductJson("update_product_name.json");
        final ResponseEntity<Product> responseUpdate = template.postForEntity(base.toString() + "/api/v1/products/" + id + "/update", updateProductNameJson, Product.class);

        assertThat(Objects.requireNonNull(responseUpdate.getBody()).getId(), equalTo(id));
        assertThat(responseUpdate.getBody().getName(), equalTo("Updated Product Name"));
    }

    @Test
    public void delete() throws IOException {
        final Product productJson = getProductJson("product.json");
        createNewProduct(productJson);
        template.delete(base.toString() + "/api/v1/products/" + productJson.getId());
        final ResponseEntity<String> forEntity = template.getForEntity(base.toString() + "/api/v1/products/" + productJson.getId(), String.class);
        assertThat(forEntity.getStatusCodeValue(), equalTo(HttpStatus.NOT_FOUND.value()));
    }


    private ResponseEntity<String> createNewProduct(Product newProduct) {
        return template.postForEntity(base.toString() + "/api/v1/products/create:new", newProduct, String.class);
    }

    private Product getProductJson(String jsonFileName) throws IOException {
        return TestHelper.create(jsonFileName, Product.class);
    }

    private List<Product> getProductsJson(String jsonFileName) throws IOException {
        return TestHelper.createListOf(jsonFileName, Product.class);
    }


    @Test
    public void health() throws IOException {
        ResponseEntity<String> response = template.getForEntity(base.toString() + "/actuator/health", String.class);
        final JsonNode jsonNode = mapper.readTree(response.getBody());
        final String status = jsonNode.get("status").asText();
        assertThat(status, equalTo("UP"));
    }


}
