package ms.cms.authoring.service.web;

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
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SuppressWarnings("deprecation")
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
        //String uri = "advanced_potions_2";

        CmsPage cmsPage = new CmsPage(siteId, "", title, "", null, "", RandomStringUtils.randomAlphabetic(200));

        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        HttpEntity<CmsPage> requestEntity = new HttpEntity<>(cmsPage, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/page", HttpMethod.POST, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());

        //todo verify errors
    }

    @Test
    public void testFindPage() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPage pageByUri = authoringManager.findPageByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPage> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsPage> entity = template.exchange("http://localhost:9000/api/page/{id}", HttpMethod.GET, requestEntity, CmsPage.class, pageByUri.getId());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testFindPageByUri() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPage> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsPage> entity = template.exchange("http://localhost:9000/api/page/byUri?siteId={siteId}&uri={uri}", HttpMethod.GET, requestEntity, CmsPage.class, siteId, uri);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testEditPage() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        CmsPage cmsPage = new CmsPage(siteId, "a", "a", "a", null, "a", "a");
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPage> requestEntity = new HttpEntity<>(cmsPage, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/page/{id}", HttpMethod.PUT, requestEntity, Void.class, authoringManager.findPageByUri(siteId, uri).getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testDeletePage() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPage(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPage> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/page/{id}", HttpMethod.DELETE, requestEntity, Void.class, authoringManager.findPageByUri(siteId, uri).getId());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}