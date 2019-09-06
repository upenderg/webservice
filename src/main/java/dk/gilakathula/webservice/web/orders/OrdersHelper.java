package dk.gilakathula.webservice.web.orders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import dk.gilakathula.webservice.util.JsonValidationStatus;
import dk.gilakathula.webservice.web.products.Product;
import dk.gilakathula.webservice.web.products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrdersHelper {

    private CustomJsonValidator customJsonValidator;
    private ObjectMapper mapper;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;

    @Autowired
    public OrdersHelper(CustomJsonValidator customJsonValidator,
                        ObjectMapper mapper,
                        ProductRepository productRepository,
                        OrderRepository orderRepository) {
        this.customJsonValidator = customJsonValidator;
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    OrderStatus placeOrder(String payload) {
        try {
            final JsonValidationStatus validate = customJsonValidator.validate(CustomJsonValidator.ORDER_TYPE, payload);
            if (validate.isValid()) {
                final Order order = mapper.readValue(payload, Order.class);
                final OrderStatus orderStatus = areAllProductsFoundById(order.getProducts());
                if (!orderStatus.getSuccess()) {
                    return orderStatus;
                }

                final boolean areAllUnique = areAllUnique(order.getProducts());
                if (!areAllUnique) {
                    return new OrderStatus(false, "Found duplicate products so will not create an order", HttpStatus.BAD_REQUEST);
                }
                return new OrderStatus(true, saveOrder(order), HttpStatus.OK);
            }
            final String formattedMessage = String.format("Unable to create the order with the payload '%s' due to '%s'", payload, validate.getMessage());

            return new OrderStatus(false, formattedMessage, HttpStatus.BAD_REQUEST);
        } catch (IOException | ProcessingException e) {
            final String format = String.format("Unable to create the order with the payload '%s' due to '%s'", payload, e.getMessage());
            return new OrderStatus(false, format, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    String saveOrder(Order order) {
        return orderRepository.save(order);
    }

    <T> boolean areAllUnique(List<T> list) {
        final Set<T> set = new HashSet<>();
        for (T t : list) {
            if (!set.add(t))
                return false;
        }
        return true;
    }


    OrderStatus areAllProductsFoundById(List<Product> products) {
        for (Product product : products) {
            final Optional<Product> productFound = getProduct(product);
            if (!productFound.isPresent()) {
                final String formattedMessage = String.format("Unable to find the product with id '%s' so will not create an order", product.getId());
                return new OrderStatus(false, formattedMessage, HttpStatus.BAD_REQUEST);
            }
        }
        return new OrderStatus(true, "okay", HttpStatus.OK);
    }

    Optional<Product> getProduct(Product product) {
        return productRepository.get(product.getId());
    }

}
