package sparkle.cms.registration.common.business;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sparkle.cms.data.CmsRoleRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.CmsRole;
import sparkle.cms.domain.Role;
import sparkle.cms.domain.WorkflowType;

import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = "sparkle.cms")
@ContextConfiguration(classes = {RegistrationManagerTest.class})
@ComponentScan("sparkle.cms")
public class RegistrationManagerTest extends AbstractMongoConfiguration {
    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private SiteManager siteManager;
    @Autowired
    private CmsUserRepository cmsUserRepository;
    @Autowired
    private CmsRoleRepository cmsRoleRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

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
    public void setUp() throws Exception {
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
        registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                "lvoldemort",
                "avada!kedavra",
                "voldemort@evil.com",
                "Tom Riddle");
    }

    @Test
    public void testCreateUser() throws Exception {
        assertEquals(1, cmsUserRepository.findAll().size());
        assertNotNull(cmsUserRepository.findAll().get(0).getId());
        assertNotNull(cmsUserRepository.findAll().get(0).getCreationDate());
        assertEquals("lvoldemort", cmsUserRepository.findAll().get(0).getUsername());
        assertEquals("avada!kedavra", cmsUserRepository.findAll().get(0).getPassword());
        assertEquals("voldemort@evil.com", cmsUserRepository.findAll().get(0).getEmail());
        assertEquals("Tom Riddle", cmsUserRepository.findAll().get(0).getName());
        assertEquals(Role.ROLE_USER, cmsUserRepository.findAll().get(0).getRoles().get(0).getRole());
        assertEquals(Role.ROLE_MANAGER, cmsUserRepository.findAll().get(0).getRoles().get(1).getRole());

        try {
            registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                    "lvoldemort",
                    "avada!kedavra",
                    "voldemort@evil.com",
                    "Tom Riddle");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }

        try {
            registrationManager.createUser(RegistrationManager.UserType.MANAGER,
                    "tomriddle",
                    "avada!kedavra",
                    "voldemort@evil.com",
                    "Tom Riddle");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testFindUser() throws Exception {
        String id = cmsUserRepository.findAll().get(0).getId();
        assertEquals(id, registrationManager.findUser("lvoldemort").getId());
        assertEquals(id, registrationManager.findUser("voldemort@evil.com").getId());
        try {
            registrationManager.findUser("error");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testEditUser() throws Exception {
        String id = cmsUserRepository.findAll().get(0).getId();

        registrationManager.editUser(id, "", "", "");
        assertEquals("lvoldemort", cmsUserRepository.findAll().get(0).getUsername());
        assertEquals("avada!kedavra", cmsUserRepository.findAll().get(0).getPassword());
        assertEquals("Tom Riddle", cmsUserRepository.findAll().get(0).getName());

        registrationManager.editUser(id, "username", "password", "FirstName LastName");
        assertEquals("username", cmsUserRepository.findAll().get(0).getUsername());
        assertEquals("password", cmsUserRepository.findAll().get(0).getPassword());
        assertEquals("FirstName LastName", cmsUserRepository.findAll().get(0).getName());
        try {
            registrationManager.editUser("error", "", "", "");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testDeleteUser() throws Exception {
        assertEquals(1, cmsUserRepository.findAll().size());
        String id = cmsUserRepository.findAll().get(0).getId();
        try {
            registrationManager.deleteUser("error");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
        registrationManager.deleteUser(id);
        assertEquals(0, cmsUserRepository.findAll().size());
    }

    @Test
    public void testCreateSite() throws Exception {
        String userId = createSite();
        assertEquals(1, cmsSiteRepository.findAll().size());
        assertNotNull(cmsSiteRepository.findAll().get(0).getId());
        assertNotNull(cmsSiteRepository.findAll().get(0).getCreationDate());
        assertEquals("Half Blood Blog", cmsSiteRepository.findAll().get(0).getName());
        assertEquals("www.half-blood.com", cmsSiteRepository.findAll().get(0).getAddress());
        assertEquals(WorkflowType.SELF_APPROVAL_WF, cmsSiteRepository.findAll().get(0).getWorkflowType());
        assertEquals(0, cmsSiteRepository.findAll().get(0).getAuthors().size());

        try {
            registrationManager.createSite("error", "Half Blood Blog", "www.half-blood.com", WorkflowType.SELF_APPROVAL_WF);
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
        try {
            registrationManager.createSite(userId, "Half Blood Blog", "www.half-blood.com", WorkflowType.SELF_APPROVAL_WF);
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testFindSite() throws Exception {
        String userId = createSite();
        assertEquals(registrationManager.findSites(userId).get(0).getId(), cmsSiteRepository.findAll().get(0).getId());
        assertEquals(registrationManager.findSites("lvoldemort").get(0).getId(), cmsSiteRepository.findAll().get(0).getId());
        assertEquals(registrationManager.findSite("www.half-blood.com").getId(), cmsSiteRepository.findAll().get(0).getId());
        try {
            registrationManager.findSites("error");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testEditSite() throws Exception {
        createSite();
        String siteId = cmsSiteRepository.findAll().get(0).getId();
        registrationManager.editSite(siteId, "Half Blood site");
        assertEquals("Half Blood site", cmsSiteRepository.findAll().get(0).getName());
        registrationManager.editSite(siteId, "");
        assertEquals("Half Blood site", cmsSiteRepository.findAll().get(0).getName());
        try {
            registrationManager.editSite("error", "Half Blood site");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testDeleteSite() throws Exception {
        createSite();
        String siteId = cmsSiteRepository.findAll().get(0).getId();
        try {
            registrationManager.deleteSite("error");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
        assertEquals(1, cmsSiteRepository.findAll().size());

        registrationManager.deleteSite(siteId);
        assertEquals(0, cmsSiteRepository.findAll().size());
    }

    @Test
    public void testAddSiteAuthor() throws Exception {
        String userId = createSiteAndAuthor();
        String siteId = cmsSiteRepository.findAll().get(0).getId();
        String princeId = cmsUserRepository.findByUsername("prince").get(0).getId();
        registrationManager.addSiteAuthor(siteId, princeId);

        assertEquals(1, cmsSiteRepository.findAll().get(0).getAuthors().size());
        assertNotNull(cmsSiteRepository.findAll().get(0).getAuthors().get(0).getId());
        assertNotNull(cmsSiteRepository.findAll().get(0).getAuthors().get(0).getCreationDate());
        assertEquals("prince", cmsSiteRepository.findAll().get(0).getAuthors().get(0).getUsername());
        assertEquals("legilimens", cmsSiteRepository.findAll().get(0).getAuthors().get(0).getPassword());
        assertEquals("ssnape@evil.com", cmsSiteRepository.findAll().get(0).getAuthors().get(0).getEmail());
        assertEquals("Severus Snape", cmsSiteRepository.findAll().get(0).getAuthors().get(0).getName());
        assertEquals(Role.ROLE_USER, cmsSiteRepository.findAll().get(0).getAuthors().get(0).getRoles().get(0).getRole());
        assertEquals(Role.ROLE_AUTHOR, cmsSiteRepository.findAll().get(0).getAuthors().get(0).getRoles().get(1).getRole());

        try {
            registrationManager.addSiteAuthor("error", princeId);
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }

        try {
            registrationManager.addSiteAuthor(siteId, "error");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }

        try {
            registrationManager.addSiteAuthor(siteId, userId);
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }
    }

    @Test
    public void testRemoveSiteAuthor() throws Exception {
        createSiteAndAuthor();
        String siteId = cmsSiteRepository.findAll().get(0).getId();
        String princeId = cmsUserRepository.findByUsername("prince").get(0).getId();
        registrationManager.addSiteAuthor(siteId, princeId);

        try {
            registrationManager.removeSiteAuthor("error", princeId);
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }

        try {
            registrationManager.removeSiteAuthor(siteId, "error");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }

        registrationManager.removeSiteAuthor(siteId, princeId);
        assertEquals(0, cmsSiteRepository.findAll().get(0).getAuthors().size());
    }

    private String createSite() throws RegistrationException {
        String userId = cmsUserRepository.findAll().get(0).getId();
        registrationManager.createSite(userId, "Half Blood Blog", "www.half-blood.com", WorkflowType.SELF_APPROVAL_WF);
        return userId;
    }

    private String createSiteAndAuthor() throws RegistrationException {
        String userId = createSite();
        registrationManager.createUser(RegistrationManager.UserType.AUTHOR,
                "prince",
                "legilimens",
                "ssnape@evil.com",
                "Severus Snape");
        return userId;
    }
}