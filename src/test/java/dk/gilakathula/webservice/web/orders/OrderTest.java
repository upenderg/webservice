package dk.gilakathula.webservice.web.orders;

import dk.gilakathula.webservice.web.TestHelper;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class OrderTest {

    @Test
    public void json() throws IOException {
        Order order = TestHelper.create("order.json", Order.class);
        assertNotNull(order);

        final String orderAsString = TestHelper.getObjectMapper().writeValueAsString(order);
        final Order read = TestHelper.getObjectMapper().readValue(orderAsString, Order.class);
        assertEquals(order, read);
    }

    @Test
    public void jsonList() throws IOException {
        final List<Order> listOf = TestHelper.createListOf("orders.json", Order.class);
        assertNotNull(listOf);

    }

}
