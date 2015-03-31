package ms.cms.authoring.service.web;

import ms.cms.authoring.common.business.AuthoringException;
import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.domain.CmsPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * PageService
 * Created by thebaz on 27/03/15.
 */
@RestController
@RequestMapping(value = "/api")
public class PageService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthoringManager authoringManager;

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
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

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/page/{id}", method = RequestMethod.GET)
    public CmsPage findPage(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        try {
            return authoringManager.findPage(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find page. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/page/byUri", method = RequestMethod.GET)
    public CmsPage findPageByUri(HttpServletResponse response,
                                 @RequestParam(value = "siteId") String siteId,
                                 @RequestParam(value = "uri") String uri) throws IOException {
        try {
            return authoringManager.findPageByUri(siteId, uri);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find page. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    //TODO change method to and json object post
    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/page/{id}", method = RequestMethod.PUT)
    public void editPage(HttpServletResponse response,
                         @PathVariable(value = "id") String id,
                         @RequestParam(value = "name", defaultValue = "") String name,
                         @RequestParam(value = "title") String title,
                         @RequestParam(value = "uri", defaultValue = "") String uri,
                         @RequestParam(value = "summary", defaultValue = "") String summary,
                         @RequestParam(value = "content") String content) throws IOException {
        try {
            authoringManager.editPage(id, name, title, uri, summary, content);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot edit page. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/page/{id}", method = RequestMethod.DELETE)
    public void deletePage(HttpServletResponse response,
                           @PathVariable(value = "id") String id) throws IOException {
        try {
            authoringManager.deletePage(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot delete page. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }
}
