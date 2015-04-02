package ms.cms.registration.service.web;

import ms.cms.data.CmsRoleRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.Role;
import ms.cms.registration.common.business.RegistrationManager;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * AbstractServiceTest
 * Created by bazzoni on 01/04/2015.
 */
public class AbstractServiceTest {
    public static final String USERNAME = "lvoldemort";
    public static final String PASSWORD = "avada!kedavra";
    protected HttpClient client;
    protected String userId;
    @Autowired
    protected RegistrationManager registrationManager;
    @Autowired
    private CmsUserRepository cmsUserRepository;
    @Autowired
    private CmsRoleRepository cmsRoleRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    protected void prepareEnvironment(boolean authenticated) throws Exception {
        cmsRoleRepository.deleteAll();
        cmsUserRepository.deleteAll();
        cmsSiteRepository.deleteAll();
        for (Role role : Role.ALL) {
            List<CmsRole> byRole = cmsRoleRepository.findByRole(role.getName());
            if (byRole.isEmpty()) {
                CmsRole cmsRole = new CmsRole(role.getName());
                cmsRoleRepository.save(cmsRole);
            }
        }
        registrationManager.initialize();
        if (authenticated) {
            registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                    USERNAME,
                    PASSWORD,
                    "voldemort@evil.com",
                    "Tom Riddle");
            userId = registrationManager.findUser("voldemort@evil.com").getId();
        }
    }

    protected void prepareHttpClient(boolean authenticated) {
        int timeout = 5;

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .build();

        if (authenticated) {
            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            AuthScope authScope = new AuthScope("localhost", 9000, AuthScope.ANY_REALM);
            Credentials credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
            credentialsProvider.setCredentials(authScope, credentials);

            client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config)
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build();
        } else {
            client = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config)
                    .build();
        }
    }
}
