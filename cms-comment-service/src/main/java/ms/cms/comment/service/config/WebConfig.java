package ms.cms.comment.service.config;

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
@ComponentScan({"ms.cms.comment.service.web"})
public class WebConfig extends WebMvcConfigurerAdapter {
}
