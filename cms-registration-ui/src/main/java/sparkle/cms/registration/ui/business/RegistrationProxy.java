package sparkle.cms.registration.ui.business;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.ui.domain.UserRegistration;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * RegistrationProxy
 * Created by thebaz on 07/04/15.
 */
@Component
@ConfigurationProperties(prefix = "mscms")
public class RegistrationProxy implements InitializingBean {
    public static final String FIND_USER_URI = "/public/user?param={param}";
    private static final String CREATE_USER_URI = "/public/user/manager";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, String> authentication;
    private Map<String, String> authoring;
    private Map<String, String> registration;
    private int timeout;

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

    public boolean userExists(HttpSession session, UserRegistration registration) {
        RestTemplate template = new RestTemplate(makeClient(session));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsUser> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsUser> entity;
        try {
            entity = template.exchange(createUrl(ServiceType.REGISTRATION, FIND_USER_URI), HttpMethod.GET, requestEntity, CmsUser.class, registration.getEmail());
        } catch (RestClientException e) {
            logger.info(String.format("Error calling %s", createUrl(ServiceType.REGISTRATION, FIND_USER_URI)), e);
            return false;
        }

        return entity.getStatusCode() == HttpStatus.OK && entity.getBody().getEmail().equalsIgnoreCase(registration.getEmail());
    }

    public boolean createWebmaster(HttpSession session, UserRegistration registration) {
        RestTemplate template = new RestTemplate(makeClient(session));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        CmsUser cmsUser = new CmsUser();
        cmsUser.setName(registration.getName());
        cmsUser.setEmail(registration.getEmail());
        cmsUser.setUsername(registration.getEmail());
        cmsUser.setPassword(registration.getPassword());
        HttpEntity<CmsUser> requestEntity = new HttpEntity<>(cmsUser, headers);

        ResponseEntity<Void> entity;
        try {
            entity = template.exchange(createUrl(ServiceType.REGISTRATION, CREATE_USER_URI), HttpMethod.POST, requestEntity, Void.class);
            //TODO send email through Amazon Simple Email Service (Amazon SES)
        } catch (RestClientException e) {
            logger.info(String.format("Error calling %s", createUrl(ServiceType.REGISTRATION, CREATE_USER_URI)), e);
            return false;
        }

        return entity.getStatusCode() == HttpStatus.OK;
    }

    private String createUrl(ServiceType serviceType, String uri) {
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

    private ClientHttpRequestFactory makeClient(HttpSession session) {
        HttpClient client = (HttpClient) session.getAttribute("client");
        if (client != null) {
            return new HttpComponentsClientHttpRequestFactory(client);
        }

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();


        client = HttpClientBuilder.create()
                .setDefaultRequestConfig(config)
                .build();
        session.setAttribute("client", client);
        return new HttpComponentsClientHttpRequestFactory(client);
    }

    public enum ServiceType {
        AUTHENTICATION, AUTHORING, REGISTRATION
    }
}
