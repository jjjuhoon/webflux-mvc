package Helloworld.helloworld_webflux.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//http://localhost:8082/webjars/swagger-ui/index.html -> 여기로 접속
//https://medium.com/@kamomillte/tutorial-adding-swagger-ui-to-a-spring-webflux-application-3ff7aebb63b8 -> 참고


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI helloWorldWebfluxAPI() {
        Info info = new Info()
                .title("Hello World Server Webflux API")
                .description("Hello World Server Webflux API 명세서")
                .version("1.0.0");

        String jwtSchemeName = "JWT TOKEN";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
