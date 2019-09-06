package dk.gilakathula.webservice.web.orders;

import com.google.common.collect.Lists;
import dk.gilakathula.webservice.web.products.Product;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class OrderRepositoryStub implements OrderRepository {

    private static final Product product = new Product.Builder()
            .id(BigInteger.TEN)
            .name("Product10")
            .price(BigDecimal.valueOf(1000))
            .build();


    private static final Order order = new Order.Builder()
            .id("1234509878")
            .email("fake@wipe.com")
            .products(Lists.newArrayList(product))
            .created()
            .total(Lists.newArrayList(product))
            .build();

    @Override
    public List<Order> getAll(int days) {
        return Lists.newArrayList(order);
    }

    @Override
    public String save(Order order) {
        return order.getId();
    }
}
