package ms.cms.service.authentication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * WebConfig
 * Created by thebaz on 23/03/15.
 */
@Configuration
@Import(value = {ApplicationSecurity.class})
public class WebConfig extends WebMvcConfigurerAdapter {
}
