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

@SuppressWarnings("deprecation")
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
    private CmsContentRepository cmsContentRepository;
    @Autowired
    private CmsTagRepository cmsTagRepository;

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
        cmsContentRepository.deleteAll();
        cmsTagRepository.deleteAll();
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
    public void testCreateContent() throws Exception {
        authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsContentRepository.findAll().size());
        assertNotNull(cmsContentRepository.findAll().get(0).getId());
        assertNotNull(cmsContentRepository.findAll().get(0).getModificationDate());
        assertEquals("CONTENT", cmsContentRepository.findAll().get(0).getType());
        assertEquals(siteId, cmsContentRepository.findAll().get(0).getSiteId());
        assertEquals("Advanced Potions 2", cmsContentRepository.findAll().get(0).getName());
        assertEquals("Advanced Potions 2", cmsContentRepository.findAll().get(0).getTitle());
        assertEquals("advanced_potions_2", cmsContentRepository.findAll().get(0).getUri());
        assertNotNull(cmsContentRepository.findAll().get(0).getSummary());
        assertNotNull(cmsContentRepository.findAll().get(0).getContent());

        try {
            authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testFindContent() throws Exception {
        authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        String contentId = cmsContentRepository.findAll().get(0).getId();

        assertNotNull(authoringManager.findContent(contentId));
        assertEquals(contentId, authoringManager.findContent(contentId).getId());

        try {
            assertNotNull(authoringManager.findContent("error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }

        assertNotNull(authoringManager.findContentByUri(siteId, "advanced_potions_2"));
        assertEquals(contentId, authoringManager.findContentByUri(siteId, "advanced_potions_2").getId());

        try {
            assertNotNull(authoringManager.findContentByUri("error", "advanced_potions_2"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
        try {
            assertNotNull(authoringManager.findContentByUri(siteId, "error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
        try {
            assertNotNull(authoringManager.findContentByUri("error", "error"));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testEditContent() throws Exception {
        authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsContentRepository.findAll().size());
        String contentId = cmsContentRepository.findAll().get(0).getId();
        assertNotNull(contentId);
        Date modificationDate = cmsContentRepository.findAll().get(0).getModificationDate();
        assertNotNull(modificationDate);

        authoringManager.editContent(contentId, "a", "a", "a", "a", "a");
        assertEquals("a", cmsContentRepository.findAll().get(0).getName());
        assertEquals("a", cmsContentRepository.findAll().get(0).getTitle());
        assertEquals("a", cmsContentRepository.findAll().get(0).getUri());
        assertEquals("a", cmsContentRepository.findAll().get(0).getSummary());
        assertEquals("a", cmsContentRepository.findAll().get(0).getContent());
        assertTrue(modificationDate.before(cmsContentRepository.findAll().get(0).getModificationDate()));

        try {
            authoringManager.createContent("error", "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testDeleteContent() throws Exception {
        authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        assertEquals(1, cmsContentRepository.findAll().size());
        String contentId = cmsContentRepository.findAll().get(0).getId();

        authoringManager.deleteContent(contentId);
        assertEquals(0, cmsContentRepository.findAll().size());

        try {
            authoringManager.deleteContent("error");
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testAddContentTag() throws Exception {
        authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        String contentId = cmsContentRepository.findAll().get(0).getId();

        authoringManager.addContentTags(contentId, "potions, magic");
        assertEquals(2, cmsTagRepository.findAll().size());
        assertNotNull(cmsTagRepository.findAll().get(0).getId());
        assertEquals(siteId, cmsTagRepository.findAll().get(0).getSiteId());
        assertEquals(1, cmsTagRepository.findAll().get(0).getPopularity().intValue());
        assertEquals(1, cmsTagRepository.findAll().get(0).getCommentIds().size());
        assertEquals(contentId, cmsTagRepository.findAll().get(0).getCommentIds().iterator().next());

        try {
            authoringManager.addContentTags("error", "potions");
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }

    @Test
    public void testRemoveContentTag() throws Exception {
        authoringManager.createContent(siteId, "", "Advanced Potions 2", "", "", RandomStringUtils.randomAlphabetic(200));

        String contentId = cmsContentRepository.findAll().get(0).getId();
        authoringManager.addContentTags(contentId, "potions, magic");
        assertEquals(2, cmsTagRepository.findAll().size());
        assertEquals(1, cmsTagRepository.findAll().get(0).getCommentIds().size());
        assertEquals(2, cmsContentRepository.findAll().get(0).getTags().size());

        authoringManager.removeContentTags(contentId, "Magic");
        assertEquals(2, cmsTagRepository.findAll().size());
        assertEquals(1, cmsTagRepository.findAll().get(0).getCommentIds().size());
        assertEquals(0, cmsTagRepository.findAll().get(1).getPopularity().intValue());
        assertEquals(1, cmsContentRepository.findAll().get(0).getTags().size());

        try {
            authoringManager.removeContentTags("error", "potions");
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }

        try {
            authoringManager.removeContentTags(contentId, "error");
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }
}