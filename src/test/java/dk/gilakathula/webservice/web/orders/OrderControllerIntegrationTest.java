package dk.gilakathula.webservice.web.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import dk.gilakathula.webservice.web.TestHelper;
import dk.gilakathula.webservice.web.products.Product;
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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTest {
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
        mongoTemplate.dropCollection("orders");
        mongoTemplate.dropCollection("products");
    }

    @After
    public void exit() throws Exception {
        mongoTemplate.dropCollection("orders");
        mongoTemplate.dropCollection("products");
    }

    @Test
    public void getAllOrders() throws IOException {
        ResponseEntity<String> getResponse = template.getForEntity(base.toString() + "/api/v1/orders/time-frame:days/7", String.class);
        assertThat(getResponse.getBody(), equalTo("Unable to find any Orders"));
    }

    @Test
    public void verifyBadProductRequest() throws IOException {
        createNewProducts();
        final List<Order> ordersJson = getOrdersJson("orders_bad.json");
        for (Order order : ordersJson) {
            final ResponseEntity<String> newOrder = createNewOrder(order);
            assertThat(newOrder.getStatusCode().value(), equalTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Test
    public void verifyDuplicateProductRequest() throws IOException {
        createNewProducts();
        final List<Order> ordersJson = getOrdersJson("orders_duplicate.json");
        for (Order order : ordersJson) {
            final ResponseEntity<String> newOrder = createNewOrder(order);
            assertThat(newOrder.getStatusCode().value(), equalTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

    private void createNewProducts() throws IOException {
        final List<Product> productsJson = getProductsJson("products.json");
        for (Product product : productsJson) {
            createNewProduct(product);
        }
    }

    @Test
    public void createNewOrders() throws IOException {
        createNewProducts();
        final List<Order> ordersJson = getOrdersJson("orders.json");
        for (Order newOrder : ordersJson) {
            final ResponseEntity<String> newOrder1 = createNewOrder(newOrder);
            assertThat(newOrder1.getStatusCode().value(), equalTo(HttpStatus.OK.value()));
        }
    }


    @Test
    public void save() throws IOException {
        ResponseEntity<String> response = createNewProduct(getProductJson("product.json"));
        final BigInteger bigInteger = TestHelper.getObjectMapper().readValue(response.getBody(), BigInteger.class);
        assertNotNull(bigInteger);
    }


    private ResponseEntity<String> createNewProduct(Product newProduct) {
        return template.postForEntity(base.toString() + "/api/v1/products/create:new", newProduct, String.class);
    }

    private ResponseEntity<String> createNewOrder(Order order) {
        return template.postForEntity(base.toString() + "/api/v1/orders/create:new", order, String.class);
    }

    private Product getProductJson(String jsonFileName) throws IOException {
        return TestHelper.create(jsonFileName, Product.class);
    }

    private List<Product> getProductsJson(String jsonFileName) throws IOException {
        return TestHelper.createListOf(jsonFileName, Product.class);
    }

    private List<Order> getOrdersJson(String jsonFileName) throws IOException {
        return TestHelper.createListOf(jsonFileName, Order.class);
    }


}
