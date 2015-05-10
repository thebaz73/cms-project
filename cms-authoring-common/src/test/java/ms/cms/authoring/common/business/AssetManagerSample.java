package ms.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.UUID;

/**
 * AssetManagerSample
 * Created by bazzoni on 06/05/2015.
 */
@Configuration
@ComponentScan({"ms.cms"})
@EnableAutoConfiguration
@Import(value = {MongoConfig.class})
public class AssetManagerSample extends WebMvcConfigurerAdapter implements CommandLineRunner {

    @Autowired
    private AssetManager assetManager;

    public static void main(String[] args) {
        SpringApplication.run(AssetManagerSample.class, args);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        String randomUID = UUID.randomUUID().toString();

        assetManager.createSiteRepository(randomUID);
    }

}
