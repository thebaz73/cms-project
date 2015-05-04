package ms.cms.authoring.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WebConfig
 * Created by thebaz on 27/03/15.
 */
@Configuration
@EnableWebMvc
@ComponentScan({"ms.cms.authoring.service.web"})
public class WebConfig extends WebMvcConfigurerAdapter {
}
