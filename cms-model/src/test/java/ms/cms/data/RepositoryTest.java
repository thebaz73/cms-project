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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
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
    @Autowired
    private CmsTagRepository tagRepository;

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
        CmsUser user = new CmsUser("John Doe", "john.doe@email.com", "jdoe", "jdoe", new Date(), cmsRoles);
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
        CmsUser user = new CmsUser("John Doe", "john.doe@email.com", "jdoe", "jdoe", new Date(), cmsRoles);
        userRepository.save(user);

        CmsSite site = new CmsSite("John Doe's Blog", new Date(), "www.jdoe.com", WorkflowType.SELF_APPROVAL_WF, user);
        siteRepository.save(site);

        assertEquals(1, siteRepository.findAll().size());
        assertEquals(site.getName(), siteRepository.findAll().get(0).getName());
        assertEquals(site.getCreationDate(), siteRepository.findAll().get(0).getCreationDate());
        assertEquals(site.getAddress(), siteRepository.findAll().get(0).getAddress());
        assertNotNull(siteRepository.findAll().get(0).getWebMaster());

        assertEquals(0, siteRepository.findByAddress("").size());
        assertEquals(1, siteRepository.findByAddress("www.jdoe.com").size());

        assertEquals(0, siteRepository.findByWebMaster(null).size());
        assertEquals(1, siteRepository.findByWebMaster(userRepository.findByUsername("jdoe").get(0)).size());

        assertEquals(0, siteRepository.findAll().get(0).getAuthors().size());

        CmsUser author01 = new CmsUser("Harry Potter", "harry.potter@hogwarts.com", "hpotter", "hpotter", new Date(), Arrays.asList(createCmsRole("ROLE_USER"), createCmsRole("ROLE_AUTHOR")));
        userRepository.save(author01);

        site.getAuthors().add(author01);
        siteRepository.save(site);

        assertEquals(1, siteRepository.findAll().get(0).getAuthors().size());
        assertNotNull(siteRepository.findAll().get(0).getAuthors().get(0).getId());
        assertEquals(author01.getName(), siteRepository.findAll().get(0).getAuthors().get(0).getName());

        CmsUser author02 = new CmsUser("Lord Voldemort", "voldemort@evil.com", "lvoldemort", "avada!kedavra", new Date(), Arrays.asList(createCmsRole("ROLE_USER"), createCmsRole("ROLE_AUTHOR")));
        userRepository.save(author02);

        site.getAuthors().add(author02);
        siteRepository.save(site);

        assertEquals(2, siteRepository.findAll().get(0).getAuthors().size());
        assertNotNull(siteRepository.findAll().get(0).getAuthors().get(1).getId());
        assertEquals(author02.getName(), siteRepository.findAll().get(0).getAuthors().get(1).getName());
    }

    @Test
    public void testAuthoring() {
        List<CmsRole> cmsRoles = new ArrayList<>();
        cmsRoles.add(createCmsRole("ROLE_USER"));
        cmsRoles.add(createCmsRole("ROLE_MANAGER"));
        CmsUser user = new CmsUser("John Doe", "john.doe@email.com", "jdoe", "jdoe", new Date(), cmsRoles);
        userRepository.save(user);

        CmsSite site = new CmsSite("John Doe's Blog", new Date(), "www.jdoe.com", WorkflowType.SELF_APPROVAL_WF, user);
        siteRepository.save(site);

        //PAGEs
        CmsPage page01 = createCmsPage(site.getId(), "page01", "Page 01", "/page_01", randomAlphanumeric(20), randomAlphabetic(200));

        site.getPages().add(page01);
        siteRepository.save(site);

        assertEquals(1, siteRepository.findAll().get(0).getPages().size());

        assertEquals(1, pageRepository.findAll().size());
        assertNotNull(pageRepository.findAll().get(0));
        assertNotNull(pageRepository.findAll().get(0).getId());
        assertEquals(page01.getName(), pageRepository.findAll().get(0).getName());
        assertEquals(page01.getTitle(), pageRepository.findAll().get(0).getTitle());
        assertEquals(page01.getUri(), pageRepository.findAll().get(0).getUri());
        assertEquals(page01.getModificationDate().getTime(), pageRepository.findAll().get(0).getModificationDate().getTime());
        assertEquals(page01.getSummary(), pageRepository.findAll().get(0).getSummary());
        assertEquals(page01.getContent(), pageRepository.findAll().get(0).getContent());

        assertEquals(0, pageRepository.findAll().get(0).getAssets().size());

        CmsAsset asset01 = createCmsAsset("asset01", "asset01", "/assets/asset01.png");

        page01.getAssets().add(asset01);
        pageRepository.save(page01);

        assertEquals(1, pageRepository.findAll().get(0).getAssets().size());
        assertNotNull(pageRepository.findAll().get(0).getAssets().get(0).getId());
        assertEquals(asset01.getName(), pageRepository.findAll().get(0).getAssets().get(0).getName());
        assertEquals(asset01.getModificationDate().getTime(), pageRepository.findAll().get(0).getAssets().get(0).getModificationDate().getTime());
        assertEquals(asset01.getTitle(), pageRepository.findAll().get(0).getAssets().get(0).getTitle());
        assertEquals(asset01.getUri(), pageRepository.findAll().get(0).getAssets().get(0).getUri());

        CmsAsset asset02 = createCmsAsset("asset02", "asset02", "/assets/asset02.png");

        page01.getAssets().add(asset02);
        pageRepository.save(page01);

        assertEquals(2, pageRepository.findAll().get(0).getAssets().size());
        assertNotNull(pageRepository.findAll().get(0).getAssets().get(1).getId());
        assertEquals(asset02.getName(), pageRepository.findAll().get(0).getAssets().get(1).getName());
        assertEquals(asset02.getModificationDate().getTime(), pageRepository.findAll().get(0).getAssets().get(1).getModificationDate().getTime());
        assertEquals(asset02.getTitle(), pageRepository.findAll().get(0).getAssets().get(1).getTitle());
        assertEquals(asset02.getUri(), pageRepository.findAll().get(0).getAssets().get(1).getUri());

        //POSTs
        CmsPost post01 = createCmsPost(site.getId(), "post01", "Post 01", "/post_01", randomAlphanumeric(20), randomAlphabetic(200));

        site.getPosts().add(post01);
        siteRepository.save(site);

        assertEquals(1, siteRepository.findAll().get(0).getPosts().size());

        assertEquals(1, postRepository.findAll().size());
        assertNotNull(postRepository.findAll().get(0));
        assertNotNull(postRepository.findAll().get(0).getId());
        assertEquals(post01.getName(), postRepository.findAll().get(0).getName());
        assertEquals(post01.getTitle(), postRepository.findAll().get(0).getTitle());
        assertEquals(post01.getUri(), postRepository.findAll().get(0).getUri());
        assertEquals(post01.getSummary(), postRepository.findAll().get(0).getSummary());
        assertEquals(post01.getModificationDate().getTime(), postRepository.findAll().get(0).getModificationDate().getTime());
        assertEquals(post01.getContent(), postRepository.findAll().get(0).getContent());

        assertEquals(0, postRepository.findAll().get(0).getAssets().size());

        CmsAsset asset03 = createCmsAsset("asset03", "asset03", "/assets/asset03.png");

        post01.getAssets().add(asset03);
        postRepository.save(post01);

        assertEquals(1, postRepository.findAll().get(0).getAssets().size());
        assertNotNull(postRepository.findAll().get(0).getAssets().get(0).getId());
        assertEquals(asset03.getName(), postRepository.findAll().get(0).getAssets().get(0).getName());
        assertEquals(asset03.getModificationDate().getTime(), postRepository.findAll().get(0).getAssets().get(0).getModificationDate().getTime());
        assertEquals(asset03.getTitle(), postRepository.findAll().get(0).getAssets().get(0).getTitle());
        assertEquals(asset03.getUri(), postRepository.findAll().get(0).getAssets().get(0).getUri());

        assertEquals(0, commentRepository.findAll().size());

        CmsUser viewer01 = new CmsUser("Harry Potter", "harry.potter@hogwarts.com", "hpotter", "hpotter", new Date(), Arrays.asList(createCmsRole("ROLE_USER"), createCmsRole("ROLE_VIEWER")));
        userRepository.save(viewer01);
        CmsComment comment01 = createCmsComment(post01.getId(), randomAlphanumeric(20), randomAlphabetic(200), viewer01);

        assertEquals(1, commentRepository.findAll().size());
        assertNotNull(commentRepository.findAll().get(0).getId());
        assertEquals(comment01.getTimestamp().getTime(), commentRepository.findAll().get(0).getTimestamp().getTime());
        assertEquals(comment01.getTitle(), commentRepository.findAll().get(0).getTitle());
        assertEquals(comment01.getContent(), commentRepository.findAll().get(0).getContent());

        assertEquals(1, commentRepository.findByContentId(post01.getId()).size());
        assertNotNull(commentRepository.findByContentId(post01.getId()).get(0).getId());
        assertEquals(comment01.getTimestamp().getTime(), commentRepository.findByContentId(post01.getId()).get(0).getTimestamp().getTime());
        assertEquals(comment01.getTitle(), commentRepository.findByContentId(post01.getId()).get(0).getTitle());
        assertEquals(comment01.getContent(), commentRepository.findByContentId(post01.getId()).get(0).getContent());


        CmsUser viewer02 = new CmsUser("Lord Voldemort", "voldemort@evil.com", "lvoldemort", "avada!kedavra", new Date(), Arrays.asList(createCmsRole("ROLE_USER"), createCmsRole("ROLE_VIEWER")));
        userRepository.save(viewer02);
        CmsComment comment02 = createCmsComment(post01.getId(), randomAlphanumeric(20), randomAlphabetic(200), viewer02);

        assertEquals(2, commentRepository.findAll().size());
        assertNotNull(commentRepository.findAll().get(1).getId());
        assertEquals(comment02.getTimestamp().getTime(), commentRepository.findAll().get(1).getTimestamp().getTime());
        assertEquals(comment02.getTitle(), commentRepository.findAll().get(1).getTitle());
        assertEquals(comment02.getContent(), commentRepository.findAll().get(1).getContent());

        assertEquals(2, commentRepository.findByContentId(post01.getId()).size());
        assertNotNull(commentRepository.findByContentId(post01.getId()).get(1).getId());
        assertEquals(comment02.getTimestamp().getTime(), commentRepository.findByContentId(post01.getId()).get(1).getTimestamp().getTime());
        assertEquals(comment02.getTitle(), commentRepository.findByContentId(post01.getId()).get(1).getTitle());
        assertEquals(comment02.getContent(), commentRepository.findByContentId(post01.getId()).get(1).getContent());

        CmsTag tag01 = new CmsTag(site.getId(), "potions");
        tag01.getCommentIds().add(post01.getId());
        tagRepository.save(tag01);

        assertEquals(1, tagRepository.findAll().size());
        assertNotNull(tagRepository.findAll().get(0).getId());
        assertEquals(1, tagRepository.findBySiteIdAndTag(site.getId(), "potions").size());
        assertNotNull(tagRepository.findBySiteIdAndTag(site.getId(), "potions").get(0).getId());
        assertEquals(tagRepository.findAll().get(0).getId(), tagRepository.findBySiteIdAndTag(site.getId(), "potions").get(0).getId());
        assertEquals(tagRepository.findAll().get(0).getTag(), tagRepository.findBySiteIdAndTag(site.getId(), "potions").get(0).getTag());
        assertEquals("potions", tagRepository.findBySiteIdAndTag(site.getId(), "potions").get(0).getTag());

        CmsTag tag02 = new CmsTag(site.getId(), "magic");
        tag02.getCommentIds().add(post01.getId());
        tagRepository.save(tag02);

        assertEquals(2, tagRepository.findAll().size());
        assertNotNull(tagRepository.findAll().get(1).getId());

        post01.getTags().add(tag01);
        post01.getTags().add(tag02);
        postRepository.save(post01);

        assertEquals(2, postRepository.findAll().get(0).getTags().size());
    }

    private CmsPage createCmsPage(String siteId, String name, String title, String uri, String summary, String content) {
        CmsPage cmsPage = new CmsPage(siteId, name, title, uri, new Date(), summary, content);
        pageRepository.save(cmsPage);

        return cmsPage;
    }

    private CmsPost createCmsPost(String siteId, String name, String title, String uri, String summary, String content) {
        CmsPost cmsPost = new CmsPost(siteId, name, title, uri, new Date(), summary, content);
        postRepository.save(cmsPost);

        return cmsPost;
    }

    private CmsAsset createCmsAsset(String name, String title, String uri) {
        CmsAsset cmsAsset = new CmsAsset();
        cmsAsset.setName(name);
        cmsAsset.setModificationDate(new Date());
        cmsAsset.setTitle(title);
        cmsAsset.setUri(uri);
        assetRepository.save(cmsAsset);

        return cmsAsset;
    }

    private CmsComment createCmsComment(String contentId, String title, String content, CmsUser viewer) {
        CmsComment cmsComment = new CmsComment();
        cmsComment.setContentId(contentId);
        cmsComment.setTimestamp(new Date());
        cmsComment.setTitle(title);
        cmsComment.setContent(content);
        cmsComment.setViewer(viewer);
        commentRepository.save(cmsComment);

        return cmsComment;
    }

    private CmsRole createCmsRole(String roleName) {
        CmsRole cmsRole = new CmsRole(roleName);
        roleRepository.save(cmsRole);

        return cmsRole;
    }
}