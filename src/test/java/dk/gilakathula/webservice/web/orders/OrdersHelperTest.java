package dk.gilakathula.webservice.web.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import dk.gilakathula.webservice.web.TestHelper;
import dk.gilakathula.webservice.web.products.Product;
import dk.gilakathula.webservice.web.products.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OrdersHelperTest {

    private OrdersHelper ordersHelper;
    final ObjectMapper mapper = new ObjectMapper();
    String payload;



    @Before
    public void setUp() throws Exception {
        payload = mapper.writeValueAsString(TestHelper.create("order.json", Order.class));
        final CustomJsonValidator customJsonValidator = new CustomJsonValidator("/schemas");
        final ProductRepository productRepository = new ProductRepositoryStub();
        final OrderRepository orderRepository = new OrderRepositoryStub();
        ordersHelper = new OrdersHelper(customJsonValidator, mapper, productRepository, orderRepository);

    }

    @Test
    public void placeOrder() {
        final OrderStatus orderStatus = ordersHelper.placeOrder(payload);
        assertNotNull(orderStatus);
        assertTrue(orderStatus.getSuccess());
    }
}