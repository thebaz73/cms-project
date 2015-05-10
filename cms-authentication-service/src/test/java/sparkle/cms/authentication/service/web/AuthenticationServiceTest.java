package sparkle.cms.authentication.service.web;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import sparkle.cms.authentication.service.Application;
import sparkle.cms.data.CmsRoleRepository;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.CmsRole;
import sparkle.cms.domain.CmsUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class AuthenticationServiceTest {
    @Autowired
    private CmsUserRepository userRepository;
    @Autowired
    private CmsRoleRepository roleRepository;
    private CmsUser cmsUser;

    @Before
    public void setUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        List<CmsRole> cmsRoles = new ArrayList<>();
        cmsRoles.add(createCmsRole("ROLE_USER"));
        cmsRoles.add(createCmsRole("ROLE_ADMIN"));
        cmsUser = new CmsUser("John Doe", "jdoe@email.com", randomAlphanumeric(8), randomAlphanumeric(8), new Date(), cmsRoles);
        userRepository.save(cmsUser);
    }

    @Test
    public void testWhoAmI() throws Exception {
        int timeout = 5;

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .build();

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthScope authScope = new AuthScope("localhost", 9000, AuthScope.ANY_REALM);
        Credentials credentials = new UsernamePasswordCredentials(cmsUser.getUsername(), cmsUser.getPassword());
        credentialsProvider.setCredentials(authScope, credentials);

        HttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<CmsUser> responseEntity;
        responseEntity = template.exchange("http://localhost:9000/api/whoami", HttpMethod.GET, null, CmsUser.class);

        CmsUser user = responseEntity.getBody();

        assertEquals(cmsUser.getUsername(), user.getUsername());
        assertEquals(cmsUser.getEmail(), user.getEmail());
        assertEquals(cmsUser.getName(), user.getName());
        assertEquals("", user.getPassword());
    }

    private CmsRole createCmsRole(String roleName) {
        CmsRole cmsRole = new CmsRole(roleName);
        roleRepository.save(cmsRole);
        return cmsRole;
    }
}