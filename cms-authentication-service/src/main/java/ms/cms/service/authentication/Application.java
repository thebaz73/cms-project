package ms.cms.service.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Application
 * Created by thebaz on 21/03/15.
 */
@Configuration
@ComponentScan({"ms.cms"})
@EnableAutoConfiguration
//@Import(value = {MongoConfig.class, WebConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
