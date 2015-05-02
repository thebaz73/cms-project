package ms.cms.content.service.web;

import ms.cms.content.service.Application;
import ms.cms.domain.CmsContent;
import ms.cms.domain.CmsTag;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * TagServiceTest
 * Created by thebaz on 02/05/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class TagServiceTest extends AbstractServiceTest {

    @Before
    public void setUp() throws Exception {
        prepareEnvironment();
        prepareHttpClient();
    }

    @Test
    public void testTags() throws Exception {
        for (int i = 0; i < 25; i++) {
            CmsContent cmsContent = new CmsContent(siteId, "name" + i, "title" + i, "uri" + i, new Date(), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(200));
            cmsContent.setPublished(true);
            cmsContentRepository.save(cmsContent);
            CmsTag cmsTag = new CmsTag(siteId, "tag" + i);
            cmsTag.getContentIds().add(cmsContent.getId());
            cmsTag.setPopularity(i);
            cmsTagRepository.save(cmsTag);
            cmsContent.getTags().add(cmsTag);
            cmsContentRepository.save(cmsContent);
        }

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);

        HttpEntity<CmsContent> requestEntity = new HttpEntity<>(headers);
        // Pass the new person and header
        ResponseEntity<List> entity = template.exchange("http://localhost:9000/api/tags/" + siteId, HttpMethod.GET, requestEntity, List.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertEquals(25, entity.getBody().size());
    }
}