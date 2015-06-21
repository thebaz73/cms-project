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
import sparkle.cms.content.service.web.domain.CommentData;
import sparkle.cms.domain.CmsContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * CommentServiceTest
 * Created by bazzoni on 21/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class CommentServiceTest extends AbstractServiceTest {

    @Before
    public void setUp() throws Exception {
        prepareEnvironment();
        prepareHttpClient();
    }

    @Test
    public void testComment() throws Exception {
        CmsContent cmsContent = new CmsContent(siteId, "name", "title", "uri", new Date(), RandomStringUtils.randomAlphabetic(100), RandomStringUtils.randomAlphabetic(200));
        cmsContent.setPublished(true);
        cmsContentRepository.save(cmsContent);

        CommentData commentData = new CommentData();
        commentData.setSiteId(siteId);
        commentData.setTimestamp(new Date());
        commentData.setTimestamp(new Date());
        commentData.setTitle(RandomStringUtils.randomAlphabetic(50));
        commentData.setContent(RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);

        HttpEntity<CommentData> requestEntity = new HttpEntity<>(commentData, headers);
        // Pass the new person and header
        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/comments/" + cmsContent.getId(), HttpMethod.GET, requestEntity, Void.class);
        assertEquals(HttpStatus.CREATED, entity.getStatusCode());
    }
}