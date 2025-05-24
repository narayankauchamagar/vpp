package np.com.thapanarayan.vpp.config;

import java.util.List;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenApi() {
        Server server = new Server();
        server.setUrl("http://207.180.203.169:8080");
        server.setDescription("Prod");

        Contact myContact = new Contact();
        myContact.setName("Narayan Thapa");
        myContact.setEmail("narayankauchamagar@gmail.com");

        Info information = new Info()
                .title("VPP")
                .version("1.0")
                .description("This API exposes ")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server));
    }


}
