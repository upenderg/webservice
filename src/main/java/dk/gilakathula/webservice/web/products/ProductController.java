package dk.gilakathula.webservice.web.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import dk.gilakathula.webservice.util.JsonValidationStatus;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/v1/products")
@Api(tags = "External", description = " API which \n" +
        " Supports basic products CRUD: " +
        "● Create a new product " +
        "● Get a list of all products " +
        "● Update a product ")
public class ProductController {
    private ProductRepository productRepository;
    private CustomJsonValidator customJsonValidator;
    private ObjectMapper mapper;

    @Autowired
    public ProductController(ProductRepository productRepository,
                             CustomJsonValidator customJsonValidator,
                             ObjectMapper mapper) {
        this.productRepository = productRepository;
        this.customJsonValidator = customJsonValidator;
        this.mapper = mapper;
    }

    @ApiOperation(
            value = "List of Products Data"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Products Data", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not found"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> products() {
        final List<Product> products = productRepository.getAll();
        if (!products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("Unable to find any products");
        }
    }

    @ApiOperation(
            value = "Get Products Data by Id"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of Products Data", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not found"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.GET, path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> get(@PathVariable(value = "id") BigInteger id) {
        final Optional<Product> product = productRepository.get(id);
        return product.<ResponseEntity<Object>>map(product1 -> ResponseEntity.status(HttpStatus.OK).body(product1)).orElseGet(() -> getNotFoundResponse(id));
    }

    @ApiOperation(
            value = "Save product data"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product Data", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not found"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/create:new", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> save(@RequestBody String payload) {
        final JsonValidationStatus validate;
        try {
            validate = customJsonValidator.validate(CustomJsonValidator.PRODUCT_TYPE, payload);
            if (validate.isValid()) {
                final Product product = mapper.readValue(payload, Product.class);
                final BigInteger save = productRepository.save(product);
                return ResponseEntity.status(HttpStatus.OK).body(save);
            }
            final String formattedMessage = String.format("Unable to create the product with the payload '%s' due to '%s'", payload, validate.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(formattedMessage);
        } catch (IOException | ProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format("Unable to create the product with the payload '%s' due to '%s'", payload, e.getMessage()));
        }
    }

    @ApiOperation(
            value = "Update product data"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Product Data", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not found"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/{id}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable(value = "id") BigInteger id,
                                         @RequestBody Product product) {
        try {
            final Optional<Product> updated = productRepository.update(id, product);
            return updated.<ResponseEntity<Object>>map(product1 -> ResponseEntity.status(HttpStatus.OK).body(product1)).orElseGet(() -> getNotFoundResponse(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(String.format("Unable to update '%s' with Payload '%s'", id, product.getName()));
        }

    }

    @ApiOperation(
            value = "Delete product data by Id"
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Delete Product Data", response = ResponseEntity.class),
            @ApiResponse(code = 400, message = "Bad request exception"),
            @ApiResponse(code = 401, message = "Authentication Error"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Resource Not found"),
            @ApiResponse(code = 503, message = "Service not available"),
            @ApiResponse(code = 500, message = "Internal Server error")
    })
    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> delete(@PathVariable(value = "id") BigInteger id) {
        final Optional<Product> deleted = productRepository.delete(id);
        return deleted.isPresent() ? ResponseEntity.status(HttpStatus.OK).body(String.format("Successfully deleted product by id '%s' ", id)) : getNotFoundResponse(id);
    }

    private ResponseEntity<Object> getNotFoundResponse(@PathVariable("id") BigInteger id) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("Unable to find product by id '%s'", id));
    }
}
