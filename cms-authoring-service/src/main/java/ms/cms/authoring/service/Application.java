package ms.cms.authoring.service;

import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.authoring.service.config.MongoConfig;
import ms.cms.authoring.service.config.WebConfig;
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
@ComponentScan("ms.cms")
@EnableAutoConfiguration
@Import(value = {MongoConfig.class, WebConfig.class})
public class Application implements CommandLineRunner {

    @Autowired
    private AuthoringManager authoringManager;

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
        authoringManager.initialize();
    }

}
