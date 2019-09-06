package dk.gilakathula.webservice.web.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import dk.gilakathula.webservice.web.products.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrdersHelperMockTest {

    @Mock
    private OrdersHelper ordersHelper;
    private final ObjectMapper mapper = new ObjectMapper();
    private String payload;
    private OrderStatus orderStatusOk;


    private static final Product product = new Product.Builder()
            .id(BigInteger.TEN)
            .name("Product10")
            .price(BigDecimal.valueOf(1000))
            .build();


    private static Order order = new Order.Builder()
            .id("1234509878")
            .email("fake@wipe.com")
            .products(Lists.newArrayList(product))
            .total(Lists.newArrayList(product))
            .build();


    @Before
    public void setUp() throws IOException {
        orderStatusOk = new OrderStatus(true, "okay", HttpStatus.OK);
        payload = mapper.writeValueAsString(order);
    }

    @Test
    public void placeOrder() throws IOException {
        ordersHelper.placeOrder(payload);
        order = mapper.readValue(payload, Order.class);
        verify(ordersHelper).placeOrder(payload);
        verifyZeroInteractions(ordersHelper);
    }
}