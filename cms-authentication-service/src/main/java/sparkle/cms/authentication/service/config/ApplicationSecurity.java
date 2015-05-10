package sparkle.cms.authentication.service.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * ApplicationSecurity
 * Created by bazzoni on 24/03/2015.
 */
@Configuration
@ImportResource({"classpath:webSecurityConfig.xml"})
@ComponentScan({"sparkle.cms.security"})
public class ApplicationSecurity {
}
