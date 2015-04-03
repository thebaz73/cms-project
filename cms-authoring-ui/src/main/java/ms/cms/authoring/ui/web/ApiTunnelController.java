package ms.cms.authoring.ui.web;

import ms.cms.domain.CmsSite;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * ApiTunnelController
 * Created by bazzoni on 03/04/2015.
 */
@RestController(value = "apiTunnelController")
public class ApiTunnelController {
    @Value(value = "service.connection.hostname")
    private final String hostname = "localhost";
    @Value(value = "service.connection.port")
    private final int port = 9000;
    @Value(value = "service.connection.timeout")
    private int timeout = 5 * 1000;

    @PostConstruct
    public void initialize() {
        //TODO initialize values with ConfigService
        //TODO manage session for cloud http://spring.io/blog/2015/03/01/the-portable-cloud-ready-http-session
    }

    @RequestMapping(value = "/tunnel/userSites", params = {"username", "password"}, method = RequestMethod.GET)
    public List mySites(HttpServletRequest request, String username, String password) {
        RestTemplate template = new RestTemplate(makeClient(request.getSession(), username, password));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsSite> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List> entity = template.exchange(createUrl("/api/sites?param={param}"), HttpMethod.GET, requestEntity, List.class, username);

        return entity.getBody();
    }

    private String createUrl(String uri) {
        return String.format("http://%s:%d/%s", hostname, port, uri);
    }

    private ClientHttpRequestFactory makeClient(HttpSession session, String username, String password) {
        HttpClient client = (HttpClient) session.getAttribute("client");
        if (client != null) {
            return new HttpComponentsClientHttpRequestFactory(client);
        }

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthScope authScope = new AuthScope(hostname, port, AuthScope.ANY_REALM);
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        credentialsProvider.setCredentials(authScope, credentials);

        client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
        session.setAttribute("client", client);
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
