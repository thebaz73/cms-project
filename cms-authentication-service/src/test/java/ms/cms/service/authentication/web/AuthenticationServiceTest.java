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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

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
//        String url = String.format("http://%s:%s@localhost:9000/auth/whoami", cmsUser.getUsername(), cmsUser.getPassword());
//        ResponseEntity<CmsUser> responseEntity = template.getForEntity(url, CmsUser.class);
//        CmsUser user = responseEntity.getBody();
//        assertEquals(cmsUser.getUsername(), user.getUsername());
//        assertEquals(cmsUser.getEmail(), user.getEmail());
//        assertEquals(cmsUser.getName(), user.getName());
//        assertEquals("", user.getPassword());
    }

    private CmsRole createCmsRole(String roleName) {
        CmsRole cmsRole = new CmsRole(roleName);
        roleRepository.save(cmsRole);
        return cmsRole;
    }
}