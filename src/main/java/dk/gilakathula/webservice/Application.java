package dk.gilakathula.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;

import static com.google.common.base.Predicates.not;

@SpringBootApplication
@EnableSwagger2
public class Application {

    @Value("${payload.validation.schemas.path}")
    private String payloadValidationSchemaPath;


    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .run(args);
    }

    @Bean
    public Docket swagger() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }


    @Bean
    public WebMvcConfigurer webMvcConfigAdapter() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*").allowedOrigins("*");
            }
        };

    }

    @Bean
    public CustomJsonValidator jsonValidator() throws IOException, ProcessingException {
        return new CustomJsonValidator(payloadValidationSchemaPath);
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }


}

