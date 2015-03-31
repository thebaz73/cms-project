package ms.cms.authoring.service.web;

import ms.cms.authoring.common.business.AuthoringException;
import ms.cms.authoring.service.Application;
import ms.cms.domain.CmsPage;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@IntegrationTest
public class PageServiceTest extends AbstractServiceTest {

    @Before
    public void setUp() throws Exception {
        prepareEnvironment();
        prepareHttpClient();
    }

    @Test
    public void testCreatePage() throws Exception {
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("siteId", siteId);
        mvm.add("title", title);
        mvm.add("content", RandomStringUtils.randomAlphabetic(200));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(mvm, requestHeaders);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/page", HttpMethod.POST, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPage pageByUri = authoringManager.findPageByUri(siteId, uri);

        assertNotNull(pageByUri);
        assertEquals(uri, pageByUri.getUri());
        assertEquals(title, pageByUri.getTitle());
        assertEquals("PAGE", pageByUri.getType());

        //todo verify errors
    }

    @Test
    public void testFindPage() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPage pageByUri = authoringManager.findPageByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<CmsPage> entity = template.exchange("http://localhost:9000/api/page/" + pageByUri.getId(), HttpMethod.GET, null, CmsPage.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPage body = entity.getBody();

        assertEquals(pageByUri.getId(), body.getId());
        assertEquals(uri, body.getUri());
        assertEquals(title, body.getTitle());
        assertEquals("PAGE", body.getType());
    }

    @Test
    public void testFindPageByUri() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPage pageByUri = authoringManager.findPageByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<CmsPage> entity = template.exchange("http://localhost:9000/api/page/byUri?siteId={siteId}&uri={uri}", HttpMethod.GET, null, CmsPage.class, siteId, uri);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPage body = entity.getBody();

        assertEquals(pageByUri.getId(), body.getId());
        assertEquals(uri, body.getUri());
        assertEquals(title, body.getTitle());
        assertEquals("PAGE", body.getType());
    }

    @Test
    public void testEditPage() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPage pageByUri = authoringManager.findPageByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/page/{id}?title={title}&content={content}", HttpMethod.PUT, null, Void.class, pageByUri.getId(), "a", "a");

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPage editedPage = authoringManager.findPageByUri(siteId, "a");

        assertNotNull(editedPage);
        assertEquals("a", editedPage.getUri());
        assertEquals("a", editedPage.getName());
        assertEquals("a", editedPage.getTitle());
        assertEquals("a", editedPage.getSummary());
        assertEquals("a", editedPage.getContent());
        assertEquals("PAGE", editedPage.getType());
    }

    @Test
    public void testDeletePage() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPage pageByUri = authoringManager.findPageByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/page/" + pageByUri.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        try {
            authoringManager.findPageByUri(siteId, uri);
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
    }
}