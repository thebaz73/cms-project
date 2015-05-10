package sparkle.cms.registration.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WebConfig
 * Created by thebaz on 23/03/15.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"sparkle.cms.registration.service.web"})
public class WebConfig extends WebMvcConfigurerAdapter {
}
