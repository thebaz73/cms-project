package ms.cms.data;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import ms.cms.domain.*;
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
import static org.junit.Assert.assertNotNull;


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
    @Autowired
    private CmsPageRepository pageRepository;
    @Autowired
    private CmsPostRepository postRepository;
    @Autowired
    private CmsAssetRepository assetRepository;
    @Autowired
    private CmsCommentRepository commentRepository;

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
        pageRepository.deleteAll();
        postRepository.deleteAll();
        assetRepository.deleteAll();
        commentRepository.deleteAll();
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

    @Test
    public void testAuthoring() {
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

        CmsPage page01 = createCmsPage("page01", "Curriculum Vitae", "/curriculum_vitae", "summary", "content");

        site.getPages().add(page01);
        siteRepository.save(site);

        assertEquals(1, pageRepository.findAll().size());
        assertNotNull(pageRepository.findAll().get(0));
        assertNotNull(pageRepository.findAll().get(0).getId());
        assertEquals(page01.getName(), pageRepository.findAll().get(0).getName());
        assertEquals(page01.getTitle(), pageRepository.findAll().get(0).getTitle());
        assertEquals(page01.getUri(), pageRepository.findAll().get(0).getUri());
        assertEquals(page01.getSummary(), pageRepository.findAll().get(0).getSummary());
        assertEquals(page01.getContent(), pageRepository.findAll().get(0).getContent());

        assertEquals(0, pageRepository.findAll().get(0).getAssets().size());

        CmsAsset asset01 = createCmsAsset("asset01", "photo01", "/assets/photo01.png");

        page01.getAssets().add(asset01);
        pageRepository.save(page01);

        assertEquals(1, pageRepository.findAll().get(0).getAssets().size());
        assertNotNull(pageRepository.findAll().get(0).getAssets().get(0).getId());
        assertEquals(asset01.getName(), pageRepository.findAll().get(0).getAssets().get(0).getName());
        assertEquals(asset01.getTitle(), pageRepository.findAll().get(0).getAssets().get(0).getTitle());
        assertEquals(asset01.getUri(), pageRepository.findAll().get(0).getAssets().get(0).getUri());

        CmsAsset asset02 = createCmsAsset("asset02", "photo02", "/assets/photo02.png");

        page01.getAssets().add(asset02);
        pageRepository.save(page01);

        assertEquals(2, pageRepository.findAll().get(0).getAssets().size());
        assertNotNull(pageRepository.findAll().get(0).getAssets().get(1).getId());
        assertEquals(asset02.getName(), pageRepository.findAll().get(0).getAssets().get(1).getName());
        assertEquals(asset02.getTitle(), pageRepository.findAll().get(0).getAssets().get(1).getTitle());
        assertEquals(asset02.getUri(), pageRepository.findAll().get(0).getAssets().get(1).getUri());
    }

    private CmsPage createCmsPage(String name, String title, String uri, String summary, String content) {
        CmsPage cmsPage = new CmsPage();
        cmsPage.setName(name);
        cmsPage.setTitle(title);
        cmsPage.setUri(uri);
        cmsPage.setSummary(summary);
        cmsPage.setContent(content);
        pageRepository.save(cmsPage);

        return cmsPage;
    }

    private CmsAsset createCmsAsset(String name, String title, String uri) {
        CmsAsset cmsAsset = new CmsAsset();
        cmsAsset.setName(name);
        cmsAsset.setTitle(title);
        cmsAsset.setUri(uri);
        assetRepository.save(cmsAsset);

        return cmsAsset;
    }

    private CmsRole createCmsRole(String roleName) {
        CmsRole cmsRole = new CmsRole(roleName);
        roleRepository.save(cmsRole);

        return cmsRole;
    }
}