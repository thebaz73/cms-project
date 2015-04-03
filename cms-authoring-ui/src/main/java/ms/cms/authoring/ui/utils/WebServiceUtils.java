package ms.cms.authoring.ui.utils;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;

/**
 * WebServiceUtils
 * Created by bazzoni on 03/04/2015.
 */
@Component
public class WebServiceUtils {
    private static WebServiceUtils instance;
    //@Value(value = "service.connection.host")
    private final String hostname = "localhost";
    //@Value(value = "service.connection.port")
    private final int port = 9100;
    //@Value(value = "service.connection.timeout")
    private final int timeout = 5 * 1000;

    private WebServiceUtils() {
        instance = this;
    }

    public static WebServiceUtils getInstance() {
        return instance;
    }

    @PostConstruct
    public void initialize() {
        //TODO initialize values with ConfigService
        //TODO manage session for cloud http://spring.io/blog/2015/03/01/the-portable-cloud-ready-http-session
    }

    public String createUrl(String uri) {
        return String.format("http://%s:%d/%s", hostname, port, uri);
    }

    public ClientHttpRequestFactory makeClient(HttpSession session, String username, String password) {
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
