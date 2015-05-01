package ms.cms.authoring.service.web;

import ms.cms.authoring.service.Application;
import ms.cms.domain.CmsContent;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    public void testCreateContent() throws Exception {
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        String title = "Advanced Potions 2";
        //String uri = "advanced_potions_2";

        CmsContent cmsContent = new CmsContent(siteId, "", title, "", null, "", RandomStringUtils.randomAlphabetic(200));

        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        HttpEntity<CmsContent> requestEntity = new HttpEntity<>(cmsContent, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/content", HttpMethod.POST, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());

        //todo verify errors
    }

    @Test
    public void testFindContent() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createContent(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsContent contentByUri = authoringManager.findContentByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsContent> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsContent> entity = template.exchange("http://localhost:9000/api/content/{id}", HttpMethod.GET, requestEntity, CmsContent.class, contentByUri.getId());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testFindContentByUri() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createContent(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsContent> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsContent> entity = template.exchange("http://localhost:9000/api/content/byUri?siteId={siteId}&uri={uri}", HttpMethod.GET, requestEntity, CmsContent.class, siteId, uri);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testEditContent() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createContent(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        CmsContent cmsContent = new CmsContent(siteId, "a", "a", "a", null, "a", "a");
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsContent> requestEntity = new HttpEntity<>(cmsContent, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/content/{id}", HttpMethod.PUT, requestEntity, Void.class, authoringManager.findContentByUri(siteId, uri).getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testDeleteContent() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createContent(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsContent> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/content/{id}", HttpMethod.DELETE, requestEntity, Void.class, authoringManager.findContentByUri(siteId, uri).getId());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testAddContentTags() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createContent(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsContent contentByUri = authoringManager.findContentByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("tags", "potions, magic");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(mvm, requestHeaders);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/content/{id}/tags", HttpMethod.POST, requestEntity, Void.class, contentByUri.getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        contentByUri = authoringManager.findContentByUri(siteId, uri);

        assertNotNull(contentByUri);
        assertEquals(uri, contentByUri.getUri());
        assertEquals(title, contentByUri.getTitle());
        assertEquals("potions", contentByUri.getTags().get(0).getTag());
        assertEquals("magic", contentByUri.getTags().get(1).getTag());
        assertEquals("CONTENT", contentByUri.getType());
    }

    @Test
    public void testRemoveContentTags() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createContent(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsContent contentByUri = authoringManager.findContentByUri(siteId, uri);
        authoringManager.addContentTags(contentByUri.getId(), "potions, magic");

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/content/{id}/tag?tag={tag}", HttpMethod.DELETE, null, Void.class, contentByUri.getId(), "magic");

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        contentByUri = authoringManager.findContentByUri(siteId, uri);

        assertNotNull(contentByUri);
        assertEquals(uri, contentByUri.getUri());
        assertEquals(title, contentByUri.getTitle());
        assertEquals("potions", contentByUri.getTags().get(0).getTag());
        assertEquals("CONTENT", contentByUri.getType());
    }
}