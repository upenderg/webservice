package dk.gilakathula.webservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import dk.gilakathula.webservice.util.CustomJsonValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.IOException;

import static com.google.common.base.Predicates.not;

@SpringBootApplication
public class Application {

    @Value("${payload.validation.schemas.path}")
    private String payloadValidationSchemaPath;


    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .run(args);
    }

    @Bean
    public Docket swaggerForCustomers() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("External")
                .apiInfo(
                        new ApiInfoBuilder()
                                .title("Simple web-service")
                                .description("Simple web-service")
                                .version("0.1")
                                .build()
                )
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("dk.gilakathula.webservice"))
                .paths(not(PathSelectors.regex("/error*")))
                .build();
    }


    @Bean
    public WebMvcConfigurer webMvcConfigAdapter() {
        return new WebMvcConfigurer() {
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("redirect:/swagger-ui.html");
            }

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

