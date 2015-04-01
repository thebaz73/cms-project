package ms.cms.authoring.service.web;

import ms.cms.authoring.service.Application;
import ms.cms.domain.CmsPost;
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
public class PostServiceTest extends AbstractServiceTest {

    @Before
    public void setUp() throws Exception {
        prepareEnvironment();
        prepareHttpClient();
    }

    @Test
    public void testCreatePost() throws Exception {
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";

        CmsPost cmsPost = new CmsPost(siteId, "", title, "", null, "", RandomStringUtils.randomAlphabetic(200));

        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        HttpEntity<CmsPost> requestEntity = new HttpEntity<>(cmsPost, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post", HttpMethod.POST, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());

        //todo verify errors
    }

    @Test
    public void testFindPost() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPost> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsPost> entity = template.exchange("http://localhost:9000/api/post/{id}", HttpMethod.GET, requestEntity, CmsPost.class, postByUri.getId());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testFindPostByUri() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPost> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsPost> entity = template.exchange("http://localhost:9000/api/post/byUri?siteId={siteId}&uri={uri}", HttpMethod.GET, requestEntity, CmsPost.class, siteId, uri);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testEditPost() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);

        CmsPost cmsPost = new CmsPost(siteId, "a", "a", "a", null, "a", "a");
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPost> requestEntity = new HttpEntity<>(cmsPost, headers);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post/{id}", HttpMethod.PUT, requestEntity, Void.class, authoringManager.findPostByUri(siteId, uri).getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testDeletePost() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsPost> requestEntity = new HttpEntity<>(headers);


        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post/{id}", HttpMethod.DELETE, requestEntity, Void.class, authoringManager.findPostByUri(siteId, uri).getId());
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    public void testAddPostTags() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("tags", "potions, magic");

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(mvm, requestHeaders);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post/{id}/tags", HttpMethod.POST, requestEntity, Void.class, postByUri.getId());

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        postByUri = authoringManager.findPostByUri(siteId, uri);

        assertNotNull(postByUri);
        assertEquals(uri, postByUri.getUri());
        assertEquals(title, postByUri.getTitle());
        assertEquals("potions".toUpperCase(), postByUri.getTags().get(0).getTag());
        assertEquals("magic".toUpperCase(), postByUri.getTags().get(1).getTag());
        assertEquals("POST", postByUri.getType());
    }

    @Test
    public void testRemovePostTags() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);
        authoringManager.addPostTags(postByUri.getId(), "potions, magic");

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post/{id}/tag?tag={tag}", HttpMethod.DELETE, null, Void.class, postByUri.getId(), "magic");

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        postByUri = authoringManager.findPostByUri(siteId, uri);

        assertNotNull(postByUri);
        assertEquals(uri, postByUri.getUri());
        assertEquals(title, postByUri.getTitle());
        assertEquals("potions".toUpperCase(), postByUri.getTags().get(0).getTag());
        assertEquals("POST", postByUri.getType());
    }
}