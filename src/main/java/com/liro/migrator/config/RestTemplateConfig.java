package com.liro.migrator.config;

import com.liro.migrator.dtos.MigratorRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

@Configuration
@EnableSwagger2
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public String CLIENT_ID = "android-app";

    public String CLIENT_SECRET = "12345";

    public String AUTH_SERVER = "https://api.liro.pet/api/security";


    @Bean
    public Queue<MigratorRequest> migratorQueue(){
        return new LinkedBlockingDeque<MigratorRequest>();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Docket api() {

        List<ResponseMessage> list = new ArrayList<>();
        list.add(new ResponseMessageBuilder().code(500).message("500 message")
                .responseModel(new ModelRef("Result")).build());
        list.add(new ResponseMessageBuilder().code(401).message("Unauthorized")
                .responseModel(new ModelRef("Result")).build());
        list.add(new ResponseMessageBuilder().code(406).message("Not Acceptable")
                .responseModel(new ModelRef("Result")).build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.liro.migrator"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(securitySchema()))
                .securityContexts(Collections.singletonList(securityContext()))
                .pathMapping("/")
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .globalResponseMessage(RequestMethod.GET, list)
                .globalResponseMessage(RequestMethod.POST, list)
            .directModelSubstitute(Pageable.class, SwaggerPageable.class);
    }

    private static class SwaggerPageable {
        private Integer page;
        private Integer size;
        private List<String> sort;

        // Getters and setters
    }


    private OAuth securitySchema() {

        List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        authorizationScopeList.add(new AuthorizationScope("read", "read all"));
        authorizationScopeList.add(new AuthorizationScope("write", "access all"));

        List<GrantType> grantTypes = new ArrayList<>();
        GrantType creGrant = new ResourceOwnerPasswordCredentialsGrant(AUTH_SERVER + "/oauth/token");

        grantTypes.add(creGrant);

        return new OAuth("oauth2schema", authorizationScopeList, grantTypes);

    }

    private List<SecurityReference> defaultAuth() {

        final AuthorizationScope[] authorizationScopes = new AuthorizationScope[2];
        authorizationScopes[0] = new AuthorizationScope("read", "read all");
        authorizationScopes[1] = new AuthorizationScope("write", "write all");

        return Collections.singletonList(new SecurityReference("oauth2schema", authorizationScopes));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).
                forPaths(PathSelectors.any())
                .build();
    }


    @Bean
    public SecurityConfiguration securityInfo() {
        return new SecurityConfiguration(CLIENT_ID, CLIENT_SECRET, "", "", "", ApiKeyVehicle.HEADER, "", " ");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Migrator API").description("")
                .termsOfServiceUrl("https://www.example.com/api")
                .contact(new Contact("Francisco Busleiman", "http://www.example.com", "franbusleiman@gmail.com"))
                .license("Open Source").licenseUrl("https://www.example.com").version("1.0.0").build();
    }

}

