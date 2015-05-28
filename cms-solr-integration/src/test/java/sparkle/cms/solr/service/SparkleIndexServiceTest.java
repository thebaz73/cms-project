package sparkle.cms.solr.service;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.apache.solr.client.solrj.SolrServer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.data.CmsRoleRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.*;
import sparkle.cms.solr.domain.SparkleDocument;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

/**
 * SparkleIndexServiceTest
 * Created by bazzoni on 28/05/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@EnableMongoRepositories(basePackages = "sparkle.cms")
@EnableSolrRepositories(basePackages = {"sparkle.cms.solr.data", "sparkle.cms.domain"}, multicoreSupport = true)
@ComponentScan
@ContextConfiguration(classes = {SparkleIndexServiceTest.class})
public class SparkleIndexServiceTest extends AbstractMongoConfiguration {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsUserRepository cmsUserRepository;
    @Autowired
    private CmsRoleRepository cmsRoleRepository;
    @Autowired
    private CmsContentRepository cmsContentRepository;
    @Autowired
    private SparkleIndexService sparkleIndexService;

    private CmsSite cmsSite;

    @Bean
    public static SolrServer solrServer() throws Exception {
        return solrServerFactoryBean().getObject();
    }

    @Bean
    public static EmbeddedSolrServerFactoryBean solrServerFactoryBean() {
        System.setProperty("solr.solr.home", System.getProperty("java.io.tmpdir") + "solr");
//        CoreContainer.Initializer initializer = new CoreContainer.Initializer();
//        CoreContainer coreContainer = initializer.initialize();
//        EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");
        EmbeddedSolrServerFactoryBean factory = new EmbeddedSolrServerFactoryBean();
        factory.setSolrHome(System.getProperty("java.io.tmpdir") + "solr");
        return factory;
    }

    @Bean
    public static SolrTemplate solrTemplate() throws Exception {
        return new SolrTemplate(solrServerFactoryBean().getObject());
    }

    public String getDatabaseName() {
        return "cms-test";
    }

    @Bean
    public Mongo mongo() throws UnknownHostException {
        MongoClient client = new MongoClient("192.168.108.129");
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
        CmsUser cmsUser = new CmsUser("lvoldemort", "avada!kedavra", "voldemort@evil.com", "Tom Riddle", new Date(),
                Arrays.asList(cmsRoleRepository.findByRole("ROLE_USER").get(0),
                        cmsRoleRepository.findByRole("ROLE_MANAGER").get(0)));
        cmsUserRepository.save(cmsUser);

        cmsSite = new CmsSite("evil.com", new Date(), "evil.com", WorkflowType.SELF_APPROVAL_WF, cmsUser);
        cmsSiteRepository.save(cmsSite);
    }

    @Test
    public void testSolrIntegration() {
        List<CmsContent> contentList = new ArrayList<>();
        contentList.add(new CmsContent(cmsSite.getId(), "Test01", "Test01", "/test01", new Date(), randomAlphanumeric(20), randomAlphabetic(200)));
        contentList.add(new CmsContent(cmsSite.getId(), "Test02", "Test03", "/test02", new Date(), randomAlphanumeric(20), randomAlphabetic(200)));
        contentList.add(new CmsContent(cmsSite.getId(), "Test03", "Test03", "/test03", new Date(), randomAlphanumeric(20), randomAlphabetic(200)));
        contentList.add(new CmsContent(cmsSite.getId(), "Test04", "Test04", "/test04", new Date(), randomAlphanumeric(20), randomAlphabetic(200)));

        List<CmsContent> savedContentList = new ArrayList<>();
        for (CmsContent cmsContent : contentList) {
            savedContentList.add(cmsContentRepository.save(cmsContent));
        }

        for (CmsContent cmsContent : savedContentList) {
            sparkleIndexService.addToIndex(cmsContent);
        }

        final List<SparkleDocument> foundDocuments = sparkleIndexService.search("test");

        foundDocuments.forEach(foundDocument -> {
            System.out.println(foundDocument.toString());
        });
    }
}