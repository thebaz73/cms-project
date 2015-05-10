package sparkle.cms.authoring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import sparkle.cms.authoring.common.business.AuthoringManager;
import sparkle.cms.authoring.service.config.MongoConfig;
import sparkle.cms.authoring.service.config.WebConfig;

/**
 * Application
 * Created by thebaz on 27/03/15.
 */
@Configuration
@ComponentScan({"sparkle.cms"})
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
        //TODO initialize values using Cloud ConfigService
        authoringManager.initialize(100);
    }

}
