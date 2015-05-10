package sparkle.cms.registration.service.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.service.Application;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class UserServiceTest extends AbstractServiceTest {

    @Before
    public void setUp() throws Exception {
        prepareEnvironment(false);
        prepareHttpClient(false);
    }

    @Test
    public void testCreateUser() throws Exception {
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        CmsUser cmsUser = new CmsUser();
        cmsUser.setName("Severus Snape");
        cmsUser.setEmail("prince@halfblood.com");
        cmsUser.setUsername("ssnape");
        cmsUser.setPassword("legilimens");
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsUser> requestEntity = new HttpEntity<>(cmsUser, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/public/user/{type}", HttpMethod.POST, requestEntity, Void.class, "manager");

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testFindUser() throws Exception {
        registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                "tomriddle",
                "avada!kedavra",
                "voldemort@evil.com",
                "Tom Riddle");
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsUser> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsUser> entity = template.exchange("http://localhost:9000/public/user?param={param}", HttpMethod.GET, requestEntity, CmsUser.class, "tomriddle");

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testEditUser() throws Exception {
        registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                "tomriddle",
                "avada!kedavra",
                "voldemort@evil.com",
                "Tom Riddle");
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        CmsUser cmsUser = new CmsUser();
        cmsUser.setName("Severus Snape");
        cmsUser.setEmail("prince@halfblood.com");
        cmsUser.setUsername("ssnape");
        cmsUser.setPassword("legilimens");
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsUser> requestEntity = new HttpEntity<>(cmsUser, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/public/user/{id}", HttpMethod.PUT, requestEntity, Void.class, registrationManager.findUser("tomriddle").getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testDeleteUser() throws Exception {
        registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                "tomriddle",
                "avada!kedavra",
                "voldemort@evil.com",
                "Tom Riddle");
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsUser> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/public/user/{id}", HttpMethod.DELETE, requestEntity, Void.class, registrationManager.findUser("tomriddle").getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}