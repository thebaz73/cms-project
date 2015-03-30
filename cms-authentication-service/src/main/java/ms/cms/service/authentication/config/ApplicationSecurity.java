package ms.cms.service.authentication.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * ApplicationSecurity
 * Created by bazzoni on 24/03/2015.
 */
@Configuration
@ImportResource({"classpath:webSecurityConfig.xml"})
@ComponentScan({"ms.cms.service.authentication.security"})
public class ApplicationSecurity {
}
