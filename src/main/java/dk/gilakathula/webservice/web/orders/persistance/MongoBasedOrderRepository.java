package dk.gilakathula.webservice.web.orders.persistance;

import dk.gilakathula.webservice.web.orders.Order;
import dk.gilakathula.webservice.web.orders.OrderRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class MongoBasedOrderRepository implements OrderRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoBasedOrderRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Order> getAll(int days) {
        final DateTime dateTimeMinusDays = DateTime.now().minusDays(days);
        final Query created = new Query().addCriteria(Criteria.where("created").gte(dateTimeMinusDays));
        return mongoTemplate.find(created, Order.class);
    }

    @Override
    public String save(Order order) {
        Order orderCreate = new Order.Builder().created().total(order.getProducts()).email(order.getEmail())
                .products(order.getProducts())
                .build();
        final Order save = mongoTemplate.save(orderCreate);
        return save.getId();
    }
}
