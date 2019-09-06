package dk.gilakathula.webservice.web.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dk.gilakathula.webservice.util.CustomDateTimeSerializer;
import dk.gilakathula.webservice.web.BaseObject;
import dk.gilakathula.webservice.web.products.Product;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "orders")
public class Order extends BaseObject {

    @Id
    private String id;
    @JsonProperty("orderLines") private List<Product> products;
    private String email; // The email of the person who placed the order

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private DateTime created; // The timestamp at which the order have been created.

    private BigDecimal total;

    public Order() {
    }

    private Order(Builder builder) {
        id = builder.id;
        products = builder.products;
        email = builder.email;
        created = builder.created;
        total = builder.total;
    }


    public static final class Builder {

        private String id;
        private List<Product> products;
        private String email; // The email of the person who placed the order
        private DateTime created; // The timestamp at which the order have been created.
        private BigDecimal total;

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder products(List<Product> products) {
            this.products = products;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder total(List<Product> products) {
            BigDecimal total = BigDecimal.ZERO;
            for (Product product : products) {
                total = total.add(product.getPrice());
            }
            this.total = total;
            return this;
        }

        public Builder created() {
            this.created = DateTime.now();
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    public String getId() {
        return id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public String getEmail() {
        return email;
    }

    public DateTime getCreated() {
        return created;
    }

    public BigDecimal getTotal() {
        return total;
    }
}
