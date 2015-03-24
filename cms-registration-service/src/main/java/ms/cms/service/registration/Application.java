package ms.cms.service.registration;

import ms.cms.service.registration.config.MongoConfig;
import ms.cms.service.registration.config.WebConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Application
 * Created by thebaz on 21/03/15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import(value = {MongoConfig.class, WebConfig.class})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
