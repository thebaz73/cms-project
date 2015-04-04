package ms.cms.authoring.ui.utils;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * WebServiceUtils
 * Created by bazzoni on 03/04/2015.
 */
@Component
@ConfigurationProperties(prefix = "mscms")
public class WebServiceUtils implements InitializingBean {

    private static WebServiceUtils instance;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, String> authentication;
    private Map<String, String> authoring;
    private Map<String, String> registration;
    private int timeout;

    private WebServiceUtils() {
        instance = this;
    }

    public static WebServiceUtils getInstance() {
        return instance;
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //TODO initialize values with ConfigService
        //TODO manage session for cloud http://spring.io/blog/2015/03/01/the-portable-cloud-ready-http-session
        logger.debug(String.format("Authentication service: %s, timeout: %s", authentication, timeout));
        logger.debug(String.format("Authoring service: %s, timeout: %s", authoring, timeout));
        logger.debug(String.format("Registration service: %s, timeout: %s", registration, timeout));
    }

    public Map<String, String> getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Map<String, String> authentication) {
        this.authentication = authentication;
    }

    public Map<String, String> getAuthoring() {
        return authoring;
    }

    public void setAuthoring(Map<String, String> authoring) {
        this.authoring = authoring;
    }

    public Map<String, String> getRegistration() {
        return registration;
    }

    public void setRegistration(Map<String, String> registration) {
        this.registration = registration;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String createUrl(ServiceType serviceType, String uri) {
        String url = String.format("http://localhost:9000/%s", uri);
        switch (serviceType) {
            case AUTHENTICATION:
                url = String.format("http://%s:%s/%s", authentication.get("dns"), authentication.get("port"), uri);
                break;
            case AUTHORING:
                url = String.format("http://%s:%s/%s", authoring.get("dns"), authoring.get("port"), uri);
                break;
            case REGISTRATION:
                url = String.format("http://%s:%s/%s", registration.get("dns"), registration.get("port"), uri);
                break;
        }
        return url;
    }

    public ClientHttpRequestFactory makeClient(HttpSession session, String username, String password, ServiceType serviceType) {
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
        AuthScope authScope;
        switch (serviceType) {
            case AUTHENTICATION:
                authScope = new AuthScope(authentication.get("dns"), Integer.parseInt(authoring.get("port")), AuthScope.ANY_REALM);
                break;
            case AUTHORING:
                authScope = new AuthScope(authoring.get("dns"), Integer.parseInt(authoring.get("port")), AuthScope.ANY_REALM);
                break;
            case REGISTRATION:
                authScope = new AuthScope(registration.get("dns"), Integer.parseInt(registration.get("port")), AuthScope.ANY_REALM);
                break;
            default:
                authScope = new AuthScope(registration.get("dns"), Integer.parseInt(registration.get("port")), AuthScope.ANY_REALM);
        }
        Credentials credentials = new UsernamePasswordCredentials(username, password);
        credentialsProvider.setCredentials(authScope, credentials);

        client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
        session.setAttribute("client", client);
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    public enum ServiceType {
        AUTHENTICATION, AUTHORING, REGISTRATION
    }
}
