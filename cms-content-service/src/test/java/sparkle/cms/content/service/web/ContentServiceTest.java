package sparkle.cms.content.service.web;

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

import sparkle.cms.content.service.Application;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsTag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class ContentServiceTest extends AbstractServiceTest {
    @Before
    public void setUp() throws Exception {
        prepareEnvironment();
        prepareHttpClient();
    }

    @Test
    public void testContentService() throws Exception {
        for (int i = 0; i < 25; i++) {
            CmsContent cmsContent = new CmsContent(siteId, "name" + i, "title" + i, "uri" + i, new Date(), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(200));
            cmsContent.setPublished(true);
            cmsContentRepository.save(cmsContent);
            CmsTag cmsTag = new CmsTag(siteId, "tag" + i, "tag" + i);
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
        @SuppressWarnings("rawtypes")
		ResponseEntity<HttpEntity> entity = template.exchange("http://localhost:9000/api/contents/" + siteId, HttpMethod.GET, requestEntity, HttpEntity.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        for (CmsContent cmsContent : cmsContentRepository.findAll()) {
            template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

            // Prepare acceptable media type
            acceptableMediaTypes = new ArrayList<>();
            acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
            // Prepare header
            headers = new HttpHeaders();
            headers.setAccept(acceptableMediaTypes);

            requestEntity = new HttpEntity<>(headers);
            // Pass the new person and header
            entity = template.exchange("http://localhost:9000/api/contents/" + siteId + "/" + cmsContent.getUri(), HttpMethod.GET, requestEntity, HttpEntity.class);
            assertEquals(HttpStatus.OK, entity.getStatusCode());
        }

        template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        // Prepare acceptable media type
        acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);

        requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ContentList> entityIterable = template.exchange("http://localhost:9000/api/contents/" + siteId + "?tag=tag1", HttpMethod.GET, requestEntity, ContentList.class);
        assertEquals(HttpStatus.OK, entityIterable.getStatusCode());
        assertEquals(1, entityIterable.getBody().size());
    }

    @SuppressWarnings("serial")
	private class ContentList extends ArrayList<CmsContent> {
    }
}