package ms.cms.content.service.web;

import ms.cms.data.*;
import ms.cms.domain.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * AbstractServiceTest
 * Created by thebaz on 31/03/15.
 */
public abstract class AbstractServiceTest {
    public static final String USERNAME = "lvoldemort";
    public static final String PASSWORD = "avada!kedavra";

    protected String siteId;
    protected HttpClient client;
    @Autowired
    protected CmsUserRepository cmsUserRepository;
    @Autowired
    protected CmsRoleRepository cmsRoleRepository;
    @Autowired
    protected CmsSiteRepository cmsSiteRepository;
    @Autowired
    protected CmsContentRepository cmsContentRepository;
    @Autowired
    protected CmsTagRepository cmsTagRepository;

    protected void prepareEnvironment() {
        cmsRoleRepository.deleteAll();
        cmsUserRepository.deleteAll();
        cmsSiteRepository.deleteAll();
        cmsContentRepository.deleteAll();
        cmsTagRepository.deleteAll();
        for (Role role : Role.ALL) {
            List<CmsRole> byRole = cmsRoleRepository.findByRole(role.getName());
            if (byRole.isEmpty()) {
                CmsRole cmsRole = new CmsRole(role.getName());
                cmsRoleRepository.save(cmsRole);
            }
        }
        CmsUser webMaster = new CmsUser("Tom Riddle",
                "voldemort@evil.com",
                USERNAME,
                PASSWORD,
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

    protected void prepareHttpClient() {
        int timeout = 5;

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .setSocketTimeout(timeout * 1000)
                .build();

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthScope authScope = new AuthScope("localhost", 9000, AuthScope.ANY_REALM);
        Credentials credentials = new UsernamePasswordCredentials(USERNAME, PASSWORD);
        credentialsProvider.setCredentials(authScope, credentials);

        client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}
