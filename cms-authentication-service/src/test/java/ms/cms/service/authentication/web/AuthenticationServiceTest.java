package ms.cms.service.authentication.web;

import ms.cms.data.CmsRoleRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsUser;
import ms.cms.service.authentication.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@ContextConfiguration(classes={MongoConfig.class, Application.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AuthenticationServiceTest {

    @Autowired
    private CmsUserRepository userRepository;


    @Before
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    public void testWhoAmI() throws Exception {
        CmsUser user = null;

        assertEquals("jdoe", user.getUsername());
        assertEquals("jdoe@email.com", user.getEmail());
        assertEquals("John Soe", user.getName());
    }
}