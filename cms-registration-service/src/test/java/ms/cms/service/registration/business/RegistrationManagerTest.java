package ms.cms.service.registration.business;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import ms.cms.data.CmsRoleRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.data.CmsUserRepository;
import ms.cms.domain.CmsRole;
import ms.cms.domain.Role;
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

import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = "ms.cms")
@ContextConfiguration(classes = {RegistrationManagerTest.class})
@ComponentScan
public class RegistrationManagerTest extends AbstractMongoConfiguration {
    @Autowired
    private RegistrationManager registrationManager;
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
        registrationManager.createUser(RegistrationManager.UserType.AUTHOR,
                "lvoldemort",
                "avada!kedavra",
                "voldemort@evil.com",
                "Tom",
                "Riddle");
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
        assertEquals(Role.ROLE_AUTHOR, cmsUserRepository.findAll().get(0).getRoles().get(1).getRole());

        try {
            registrationManager.createUser(RegistrationManager.UserType.AUTHOR,
                    "lvoldemort",
                    "avada!kedavra",
                    "voldemort@evil.com",
                    "Tom",
                    "Riddle");
        } catch (RegistrationException e) {
            assertEquals(RegistrationException.class, e.getClass());
        }

        try {
            registrationManager.createUser(RegistrationManager.UserType.AUTHOR,
                    "tomriddle",
                    "avada!kedavra",
                    "voldemort@evil.com",
                    "Tom",
                    "Riddle");
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
        assertEquals("avada!kedavra", cmsUserRepository.findAll().get(0).getPassword());
        assertEquals("Tom Riddle", cmsUserRepository.findAll().get(0).getName());

        registrationManager.editUser(id, "password", "FirstName", "LastName");
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

    }

    @Test
    public void testFindSite() throws Exception {

    }

    @Test
    public void testEditSite() throws Exception {

    }

    @Test
    public void testDeleteSite() throws Exception {

    }
}