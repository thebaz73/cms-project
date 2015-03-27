package ms.cms.service.authoring.web;

import ms.cms.domain.CmsPage;
import ms.cms.service.authoring.business.AuthoringException;
import ms.cms.service.authoring.business.AuthoringManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * PageService
 * Created by thebaz on 27/03/15.
 */
@RestController
public class PageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public void createPage(HttpServletResponse response,
                           @RequestParam(value = "siteId") String siteId,
                           @RequestParam(value = "name", defaultValue = "") String name,
                           @RequestParam(value = "title") String title,
                           @RequestParam(value = "uri", defaultValue = "") String uri,
                           @RequestParam(value = "summary", defaultValue = "") String summary,
                           @RequestParam(value = "content") String content) throws IOException {
        try {
            authoringManager.createPage(siteId, name, title, uri, summary, content);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot create page. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @RequestMapping(value = "/page/{param}", method = RequestMethod.GET)
    public CmsPage findPage(HttpServletResponse response, @PathVariable("param") String param) throws IOException {
        try {
            return authoringManager.findPage(param);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find page. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }
}
