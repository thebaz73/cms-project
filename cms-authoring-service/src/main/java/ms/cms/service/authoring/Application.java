package ms.cms.service.authoring;

import ms.cms.service.authoring.business.AuthoringManager;
import ms.cms.service.authoring.config.MongoConfig;
import ms.cms.service.authoring.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Application
 * Created by thebaz on 27/03/15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import(value = {MongoConfig.class, WebConfig.class})
public class Application implements CommandLineRunner {

    @Autowired
    private AuthoringManager registrationManager;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        registrationManager.initialize();
    }

}
