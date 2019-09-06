package dk.gilakathula.webservice.web.orders;

import com.google.common.collect.Lists;
import dk.gilakathula.webservice.web.products.Product;
import dk.gilakathula.webservice.web.products.ProductRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryStub implements ProductRepository {

    private static final Product product = new Product.Builder()
            .id(BigInteger.TEN)
            .name("Product10")
            .price(BigDecimal.valueOf(1000))
            .build();

    private static final Product updatedProduct = new Product.Builder()
            .id(BigInteger.TEN)
            .name("Product10")
            .price(BigDecimal.valueOf(2000))
            .build();


    @Override
    public List<Product> getAll() {
        return Lists.newArrayList(product);
    }

    @Override
    public Optional<Product> get(BigInteger id) {
        return Optional.of(product);
    }

    @Override
    public BigInteger save(Product product) {
        return product.getId();
    }

    @Override
    public Optional<Product> update(BigInteger id, Product product) {
        return Optional.of(updatedProduct);
    }

    @Override
    public Optional<Product> delete(BigInteger id) {
        return Optional.of(product);
    }
}
