package ms.cms.authoring.common.business;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import ms.cms.data.*;
import ms.cms.domain.*;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@Configuration
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = "ms.cms")
@ContextConfiguration(classes = {AuthoringManagerTest.class})
@ComponentScan("ms.cms")
public class AuthoringManagerTest extends AbstractMongoConfiguration {
    @Autowired
    private AuthoringManager authoringManager;
    @Autowired
    private CmsUserRepository cmsUserRepository;
    @Autowired
    private CmsRoleRepository cmsRoleRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsPostRepository cmsPostRepository;

    private String siteId;

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
        cmsPageRepository.deleteAll();
        cmsPostRepository.deleteAll();
        for (Role role : Role.ALL) {
            List<CmsRole> byRole = cmsRoleRepository.findByRole(role.getName());
            if (byRole.isEmpty()) {
                CmsRole cmsRole = new CmsRole(role.getName());
                cmsRoleRepository.save(cmsRole);
            }
        }
        authoringManager.initialize(100);
        CmsUser webMaster = new CmsUser("Tom Riddle",
                "voldemort@evil.com",
                "lvoldemort",
                "avada!kedavra",
                new Date(),
                Arrays.asList(cmsRoleRepository.findByRole("ROLE_USER").get(0),
                        cmsRoleRepository.findByRole("ROLE_MANAGER").get(0)));
        cmsUserRepository.save(webMaster);
        CmsSite cmsSite = new CmsSite("Half Blood Blog",
                new Date(),
                "www.half-blood.com",
                WorkflowType.SELF_APPROVAL_WF,
                webMaster);
        cmsSiteRepository.save(cmsSite);
        siteId = cmsSite.getId();
    }

    @Test
    public void testCreatePage() throws Exception {
        authoringManager.createPage(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsPageRepository.findAll().size());
        assertNotNull(cmsPageRepository.findAll().get(0).getId());
        assertNotNull(cmsPageRepository.findAll().get(0).getModificationDate());
        assertEquals("PAGE", cmsPageRepository.findAll().get(0).getType());
        assertEquals(siteId, cmsPageRepository.findAll().get(0).getSiteId());
        assertEquals("Advanced Potions 2", cmsPageRepository.findAll().get(0).getName());
        assertEquals("Advanced Potions 2", cmsPageRepository.findAll().get(0).getTitle());
        assertEquals("advanced_potions_2", cmsPageRepository.findAll().get(0).getUri());
        assertNotNull(cmsPageRepository.findAll().get(0).getSummary());
        assertNotNull(cmsPageRepository.findAll().get(0).getContent());

        try {
            authoringManager.createPage(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testFindPage() throws Exception {
        authoringManager.createPage(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        String pageId = cmsPageRepository.findAll().get(0).getId();

        assertNotNull(authoringManager.findPage(pageId));
        assertEquals(pageId, authoringManager.findPage(pageId).getId());

        try {
            assertNotNull(authoringManager.findPage("error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }

        assertNotNull(authoringManager.findPageByUri(siteId, "advanced_potions_2"));
        assertEquals(pageId, authoringManager.findPageByUri(siteId, "advanced_potions_2").getId());

        try {
            assertNotNull(authoringManager.findPageByUri("error", "advanced_potions_2"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
        try {
            assertNotNull(authoringManager.findPageByUri(siteId, "error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
        try {
            assertNotNull(authoringManager.findPageByUri("error", "error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testEditPage() throws Exception {
        authoringManager.createPage(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsPageRepository.findAll().size());
        String pageId = cmsPageRepository.findAll().get(0).getId();
        assertNotNull(pageId);
        Date modificationDate = cmsPageRepository.findAll().get(0).getModificationDate();
        assertNotNull(modificationDate);

        authoringManager.editPage(pageId, "a", "a", "a", "a", "a");
        assertEquals("a", cmsPageRepository.findAll().get(0).getName());
        assertEquals("a", cmsPageRepository.findAll().get(0).getTitle());
        assertEquals("a", cmsPageRepository.findAll().get(0).getUri());
        assertEquals("a", cmsPageRepository.findAll().get(0).getSummary());
        assertEquals("a", cmsPageRepository.findAll().get(0).getContent());
        assertTrue(modificationDate.before(cmsPageRepository.findAll().get(0).getModificationDate()));

        try {
            authoringManager.createPage("error", "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testDeletePage() throws Exception {
        authoringManager.createPage(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsPageRepository.findAll().size());
        String pageId = cmsPageRepository.findAll().get(0).getId();

        authoringManager.deletePage(pageId);
        assertEquals(0, cmsPageRepository.findAll().size());

        try {
            authoringManager.deletePage("error");
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testCreatePost() throws Exception {
        authoringManager.createPost(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsPostRepository.findAll().size());
        assertNotNull(cmsPostRepository.findAll().get(0).getId());
        assertNotNull(cmsPostRepository.findAll().get(0).getModificationDate());
        assertEquals("POST", cmsPostRepository.findAll().get(0).getType());
        assertEquals(siteId, cmsPostRepository.findAll().get(0).getSiteId());
        assertEquals("Advanced Potions 2", cmsPostRepository.findAll().get(0).getName());
        assertEquals("Advanced Potions 2", cmsPostRepository.findAll().get(0).getTitle());
        assertEquals("advanced_potions_2", cmsPostRepository.findAll().get(0).getUri());
        assertNotNull(cmsPostRepository.findAll().get(0).getSummary());
        assertNotNull(cmsPostRepository.findAll().get(0).getContent());

        try {
            authoringManager.createPost(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testFindPost() throws Exception {
        authoringManager.createPost(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        String postId = cmsPostRepository.findAll().get(0).getId();

        assertNotNull(authoringManager.findPost(postId));
        assertEquals(postId, authoringManager.findPost(postId).getId());

        try {
            assertNotNull(authoringManager.findPost("error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }

        assertNotNull(authoringManager.findPostByUri(siteId, "advanced_potions_2"));
        assertEquals(postId, authoringManager.findPostByUri(siteId, "advanced_potions_2").getId());

        try {
            assertNotNull(authoringManager.findPostByUri("error", "advanced_potions_2"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
        try {
            assertNotNull(authoringManager.findPostByUri(siteId, "error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
        try {
            assertNotNull(authoringManager.findPostByUri("error", "error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testEditPost() throws Exception {
        authoringManager.createPost(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsPostRepository.findAll().size());
        String postId = cmsPostRepository.findAll().get(0).getId();
        assertNotNull(postId);
        Date modificationDate = cmsPostRepository.findAll().get(0).getModificationDate();
        assertNotNull(modificationDate);

        authoringManager.editPost(postId, "a", "a", "a", "a", "a");
        assertEquals("a", cmsPostRepository.findAll().get(0).getName());
        assertEquals("a", cmsPostRepository.findAll().get(0).getTitle());
        assertEquals("a", cmsPostRepository.findAll().get(0).getUri());
        assertEquals("a", cmsPostRepository.findAll().get(0).getSummary());
        assertEquals("a", cmsPostRepository.findAll().get(0).getContent());
        assertTrue(modificationDate.before(cmsPostRepository.findAll().get(0).getModificationDate()));

        try {
            authoringManager.createPost("error", "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testDeletePost() throws Exception {
        authoringManager.createPost(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsPostRepository.findAll().size());
        String postId = cmsPostRepository.findAll().get(0).getId();

        authoringManager.deletePost(postId);
        assertEquals(0, cmsPostRepository.findAll().size());

        try {
            authoringManager.deletePost("error");
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }
}