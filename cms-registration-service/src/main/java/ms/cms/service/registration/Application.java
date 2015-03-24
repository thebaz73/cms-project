package ms.cms.service.registration;

import ms.cms.data.CmsRoleRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.Role;
import ms.cms.service.registration.business.RegistrationManager;
import ms.cms.service.registration.config.MongoConfig;
import ms.cms.service.registration.config.WebConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * Application
 * Created by thebaz on 21/03/15.
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
@Import(value = {MongoConfig.class, WebConfig.class})
public class Application implements CommandLineRunner {

    @Autowired
    private CmsRoleRepository cmsRoleRepository;

    @Autowired
    private RegistrationManager registrationManager;

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
        for (Role role : Role.ALL) {
            List<CmsRole> byRole = cmsRoleRepository.findByRole(role.getName());
            if (byRole.isEmpty()) {
                CmsRole cmsRole = new CmsRole(role.getName());
                cmsRoleRepository.save(cmsRole);
            }
        }
        registrationManager.initialize();
    }
}
