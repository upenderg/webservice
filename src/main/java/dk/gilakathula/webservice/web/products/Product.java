package dk.gilakathula.webservice.web.products;


import dk.gilakathula.webservice.web.BaseObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.BigInteger;

@Document(collection = "products")
public class Product extends BaseObject {
    @Id
    private BigInteger id;
    String name;
    BigDecimal price;

    private Product(Builder builder) {
        id = builder.id;
        name = builder.name;
        price = builder.price;
    }


    public static final class Builder {

        private BigInteger id;
        private String name;
        private BigDecimal price;

        public Builder() {
        }

        public Builder id(BigInteger id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }


        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }


    public Product() {
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigInteger getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
