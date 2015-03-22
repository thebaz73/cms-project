package ms.cms.data;

import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;


@ContextConfiguration(classes={MongoConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class RepositoryTest {

    @Autowired
    private CmsUserRepository userRepository;
    @Autowired
    private CmsRoleRepository roleRepository;
    @Autowired
    private CmsSiteRepository siteRepository;

    @Before
    public void clean() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    public void testCmsRoleRepository() {
        List<CmsRole> cmsRoles = new ArrayList<CmsRole>();
        cmsRoles.add(createCmsRole("ROLE_USER"));
        cmsRoles.add(createCmsRole("ROLE_ADMIN"));

        List<CmsRole> all = roleRepository.findAll();
        assertEquals(cmsRoles.size(), all.size());
    }

    @Test
    public void testCmsUserRepository() {
        ArrayList<CmsRole> cmsRoles = new ArrayList<CmsRole>();
        cmsRoles.add(createCmsRole("ROLE_USER"));
        cmsRoles.add(createCmsRole("ROLE_ADMIN"));
        CmsUser user = new CmsUser("John Doe", "john.doe@email.com", "jdoe", "jdoe", cmsRoles);
        userRepository.save(user);

        List<CmsUser> all = userRepository.findAll();
        assertEquals(1, all.size());

        assertEquals(0, userRepository.findByUsername("").size());
        assertEquals(1, userRepository.findByUsername("jdoe").size());
    }

    @Test
    public void testCmsSiteRepository() {
        ArrayList<CmsRole> cmsRoles = new ArrayList<CmsRole>();
        cmsRoles.add(createCmsRole("ROLE_USER"));
        cmsRoles.add(createCmsRole("ROLE_MANAGER"));
        CmsUser user = new CmsUser("John Doe", "john.doe@email.com", "jdoe", "jdoe", cmsRoles);
        userRepository.save(user);

        CmsSite site = new CmsSite();
        site.setName("John Doe's Blog");
        site.setAddress("www.jdoe.com");
        site.setWebMaster(user);
        siteRepository.save(site);

        List<CmsSite> all = siteRepository.findAll();
        assertEquals(1, all.size());

        assertEquals(0, siteRepository.findByAddress("").size());
        assertEquals(1, siteRepository.findByAddress("www.jdoe.com").size());

        assertEquals(0, siteRepository.findByWebMaster(null).size());
        assertEquals(1, siteRepository.findByWebMaster(userRepository.findByUsername("jdoe").get(0)).size());
    }

    private CmsRole createCmsRole(String roleName) {
        CmsRole cmsRole = new CmsRole(roleName);
        roleRepository.save(cmsRole);
        return cmsRole;
    }
}