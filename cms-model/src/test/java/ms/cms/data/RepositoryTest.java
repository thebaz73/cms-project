package ms.cms.data;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import ms.cms.domain.CmsRole;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import ms.cms.domain.Role;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = "ms.cms")
@ComponentScan
@ContextConfiguration(classes = {RepositoryTest.class})
public class RepositoryTest extends AbstractMongoConfiguration {
    @Autowired
    private CmsUserRepository userRepository;
    @Autowired
    private CmsRoleRepository roleRepository;
    @Autowired
    private CmsSiteRepository siteRepository;

    public String getDatabaseName() {
        return "cms-test";
    }

    @Bean
    public Mongo mongo() throws UnknownHostException {
        MongoClient client = new MongoClient();
        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(mongo(), getDatabaseName());
    }

    @Before
    public void clean() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        siteRepository.deleteAll();
    }

    @Test
    public void testCmsRoleRepository() {
        for (Role role : Role.ALL) {
            createCmsRole(role.getName());
        }

        List<CmsRole> all = roleRepository.findAll();
        assertEquals(Role.ALL.length, all.size());

        for (Role role : Role.ALL) {
            List<CmsRole> byRole = roleRepository.findByRole(role.getName());
            if (!byRole.isEmpty()) {
                assertEquals(role.getName(), byRole.get(0).getRole().getName());
            }
        }
    }

    @Test
    public void testCmsUserRepository() {
        List<CmsRole> cmsRoles = new ArrayList<>();
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
        List<CmsRole> cmsRoles = new ArrayList<>();
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