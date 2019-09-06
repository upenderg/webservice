package dk.gilakathula.webservice.web.products;


import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


public interface ProductRepository {
    List<Product> getAll();
    Optional<Product> get(BigInteger id);
    BigInteger save(Product product);

    Optional<Product> update(BigInteger id,Product product);
    Optional<Product> delete(BigInteger id);
}
