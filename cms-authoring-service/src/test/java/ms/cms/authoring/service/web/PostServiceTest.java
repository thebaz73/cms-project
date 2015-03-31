package ms.cms.authoring.service.web;

import ms.cms.authoring.common.business.AuthoringException;
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

        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("siteId", siteId);
        mvm.add("title", title);
        mvm.add("content", RandomStringUtils.randomAlphabetic(200));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(mvm, requestHeaders);

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post", HttpMethod.POST, requestEntity, Void.class);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);

        assertNotNull(postByUri);
        assertEquals(uri, postByUri.getUri());
        assertEquals(title, postByUri.getTitle());
        assertEquals("POST", postByUri.getType());

        //todo verify errors
    }

    @Test
    public void testFindPost() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<CmsPost> entity = template.exchange("http://localhost:9000/api/post/" + postByUri.getId(), HttpMethod.GET, null, CmsPost.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPost body = entity.getBody();

        assertEquals(postByUri.getId(), body.getId());
        assertEquals(uri, body.getUri());
        assertEquals(title, body.getTitle());
        assertEquals("POST", body.getType());
    }

    @Test
    public void testFindPostByUri() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<CmsPost> entity = template.exchange("http://localhost:9000/api/post/byUri?siteId={siteId}&uri={uri}", HttpMethod.GET, null, CmsPost.class, siteId, uri);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        CmsPost body = entity.getBody();

        assertEquals(postByUri.getId(), body.getId());
        assertEquals(uri, body.getUri());
        assertEquals(title, body.getTitle());
        assertEquals("POST", body.getType());
    }

    @Test
    public void testEditPost() throws Exception {
//        String title = "Advanced Potions 2";
//        String uri = "advanced_potions_2";
//        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
//        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);
//
//        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
//
//        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
//        mvm.add("name", "a");
//        mvm.add("title", "a");
//        mvm.add("uri", "a");
//        mvm.add("summary", "a");
//        mvm.add("content", "a");
//
//        HttpHeaders requestHeaders = new HttpHeaders();
//        requestHeaders.add("Accept", "*/*");
//        requestHeaders.add("Content-Type", "application/json");
//        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(mvm, requestHeaders);
//
//        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post/{id}", HttpMethod.PUT, requestEntity, Void.class, postByUri.getId());
//
//        assertEquals(HttpStatus.OK, entity.getStatusCode());
//        CmsPost editedPost = authoringManager.findPostByUri(siteId, uri);
//
//        assertNotNull(editedPost);
//        assertEquals("a", editedPost.getUri());
//        assertEquals("a", editedPost.getName());
//        assertEquals("a", editedPost.getTitle());
//        assertEquals("a", editedPost.getSummary());
//        assertEquals("a", editedPost.getContent());
//        assertEquals("POST", editedPost.getType());
    }

    @Test
    public void testDeletePost() throws Exception {
        String title = "Advanced Potions 2";
        String uri = "advanced_potions_2";
        authoringManager.createPost(siteId, "", title, "", "", RandomStringUtils.randomAlphabetic(200));
        CmsPost postByUri = authoringManager.findPostByUri(siteId, uri);

        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

        ResponseEntity<Void> entity = template.exchange("http://localhost:9000/api/post/" + postByUri.getId(), HttpMethod.DELETE, null, Void.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());

        try {
            authoringManager.findPostByUri(siteId, uri);
        } catch (AuthoringException e) {
            assertEquals(AuthoringException.class, e.getClass());
        }
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