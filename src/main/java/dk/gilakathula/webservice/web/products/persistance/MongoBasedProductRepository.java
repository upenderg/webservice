package dk.gilakathula.webservice.web.products.persistance;

import dk.gilakathula.webservice.web.products.Product;
import dk.gilakathula.webservice.web.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class MongoBasedProductRepository implements ProductRepository {

    private MongoTemplate mongoTemplate;

    @Autowired
    public MongoBasedProductRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Product> getAll() {
        return mongoTemplate.findAll(Product.class);
    }

    @Override
    public Optional<Product> get(BigInteger id) {
        final Query queryFind = getIdQuery(id);
        final Product found = mongoTemplate.findOne(queryFind, Product.class);
        return Optional.ofNullable(found);
    }

    @Override
    public BigInteger save(Product product) {
        final Product save = mongoTemplate.save(product);
        return save.getId();
    }

    @Override
    public Optional<Product> update(BigInteger id, Product update) {
        final Query queryFind = getIdQuery(id);
        final Product found = mongoTemplate.findOne(queryFind, Product.class);

        if (found != null) {
            if (update.getName() != null) {
                found.setName(update.getName());
            }

            if (update.getPrice() != null) {
                found.setPrice(update.getPrice());
            }
            return Optional.of(mongoTemplate.save(found));
        }
        return Optional.empty();
    }

    private Query getIdQuery(BigInteger id) {
        return new Query().addCriteria(Criteria.where("_id").is(id));
    }

    @Override
    public Optional<Product> delete(BigInteger id) {
        final Product andRemove = mongoTemplate.findAndRemove(getIdQuery(id), Product.class);
        return Optional.ofNullable(andRemove);
    }
}
