package ms.cms.authoring.ui.web;

import ms.cms.authoring.ui.utils.WebServiceUtils;
import ms.cms.domain.CmsSite;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * ApiTunnelController
 * Created by bazzoni on 03/04/2015.
 */
@RestController(value = "apiTunnelController")
public class ApiTunnelController {
    @RequestMapping(value = "/tunnel/sites", params = {"username", "password"}, method = RequestMethod.GET)
    public List<CmsSite> findSites(HttpServletRequest request, String username, String password) {
        RestTemplate template = new RestTemplate(WebServiceUtils.getInstance().makeClient(request.getSession(), username, password, WebServiceUtils.ServiceType.REGISTRATION));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsSite> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<SiteList> entity = template.exchange(WebServiceUtils.getInstance().createUrl(WebServiceUtils.ServiceType.REGISTRATION, "/api/sites?param={param}"), HttpMethod.GET, requestEntity, SiteList.class, username);

        return entity.getBody();
    }

    @RequestMapping(value = "/tunnel/sites/authors", params = {"username", "password"}, method = RequestMethod.GET)
    public List<CmsSite> findSiteAuthors(HttpServletRequest request, String username, String password) {
        RestTemplate template = new RestTemplate(WebServiceUtils.getInstance().makeClient(request.getSession(), username, password, WebServiceUtils.ServiceType.REGISTRATION));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsSite> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<SiteList> entity = template.exchange(WebServiceUtils.getInstance().createUrl(WebServiceUtils.ServiceType.REGISTRATION, "/api/sites/authors?param={param}"), HttpMethod.GET, requestEntity, SiteList.class, username);

        return entity.getBody();
    }

    @RequestMapping(value = "/tunnel/site/authored", params = {"username", "password"}, method = RequestMethod.GET)
    public CmsSite findAuthoredSite(HttpServletRequest request, String username, String password) {
        RestTemplate template = new RestTemplate(WebServiceUtils.getInstance().makeClient(request.getSession(), username, password, WebServiceUtils.ServiceType.REGISTRATION));
        // Prepare acceptable media type
        List<MediaType> acceptableMediaTypes = new ArrayList<>();
        acceptableMediaTypes.add(MediaType.APPLICATION_JSON);
        // Prepare header
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(acceptableMediaTypes);
        // Pass the new person and header
        HttpEntity<CmsSite> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<CmsSite> entity = template.exchange(WebServiceUtils.getInstance().createUrl(WebServiceUtils.ServiceType.REGISTRATION, "/api/site/authored?param={param}"), HttpMethod.GET, requestEntity, CmsSite.class, username);

        return entity.getBody();
    }

    private class SiteList extends ArrayList<CmsSite> {
    }
}
