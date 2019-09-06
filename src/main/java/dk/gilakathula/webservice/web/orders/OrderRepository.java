package dk.gilakathula.webservice.web.orders;

import java.util.List;

public interface OrderRepository {

    List<Order> getAll(final int days);

    String save(Order order);
}
