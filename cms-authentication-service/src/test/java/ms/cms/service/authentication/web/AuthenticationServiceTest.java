package ms.cms.service.authentication.web;

import ms.cms.data.CmsRoleRepository;
import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsUser;
import ms.cms.service.authentication.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class AuthenticationServiceTest {
    RestTemplate template = new TestRestTemplate();
    @Autowired
    private CmsUserRepository userRepository;
    @Autowired
    private CmsRoleRepository roleRepository;

    @Before
    public void setUp() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        ArrayList<CmsRole> cmsRoles = new ArrayList<CmsRole>();
        cmsRoles.add(createCmsRole("ROLE_USER"));
        cmsRoles.add(createCmsRole("ROLE_ADMIN"));
        CmsUser user = new CmsUser("John Doe", "jdoe@email.com", "jdoe", "jdoe", cmsRoles);
        userRepository.save(user);
    }

    @Test
    public void testWhoAmI() throws Exception {
        ResponseEntity<CmsUser> responseEntity = template.getForEntity("http://jdoe:jdoe@localhost:9000/auth/whoami", CmsUser.class);
        CmsUser user = responseEntity.getBody();
        assertEquals("jdoe", user.getUsername());
        assertEquals("jdoe@email.com", user.getEmail());
        assertEquals("John Doe", user.getName());
        assertEquals("", user.getPassword());
    }

    private CmsRole createCmsRole(String roleName) {
        CmsRole cmsRole = new CmsRole(roleName);
        roleRepository.save(cmsRole);
        return cmsRole;
    }
}