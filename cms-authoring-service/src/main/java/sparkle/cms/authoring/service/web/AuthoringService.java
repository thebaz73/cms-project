package sparkle.cms.authoring.service.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.authoring.common.business.AuthoringException;
import sparkle.cms.authoring.common.business.AuthoringManager;
import sparkle.cms.domain.CmsContent;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AuthoringService
 * Created by thebaz on 27/03/15.
 */
@RestController(value = "contentWebService")
@RequestMapping(value = "/api")
public class AuthoringService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthoringManager authoringManager;

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content", method = RequestMethod.POST)
    public void createContent(HttpServletResponse response, @RequestBody CmsContent cmsContent) throws IOException {
        try {
            authoringManager.createContent(cmsContent.getSiteId(), cmsContent.getName(), cmsContent.getTitle(), cmsContent.getUri(), cmsContent.getSummary(), cmsContent.getContent());
        } catch (AuthoringException e) {
            String msg = String.format("Cannot create content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content/{id}", method = RequestMethod.GET)
    public CmsContent findContent(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        try {
            return authoringManager.findContent(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content/byUri", method = RequestMethod.GET)
    public CmsContent findContentByUri(HttpServletResponse response,
                                       @RequestParam(value = "siteId") String siteId,
                                       @RequestParam(value = "uri") String uri) throws IOException {
        try {
            return authoringManager.findContentByUri(siteId, uri);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }

        return null;
    }

    //TODO change method to and json object content
    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content/{id}", method = RequestMethod.PUT)
    public void editContent(HttpServletResponse response,
                            @PathVariable(value = "id") String id, @RequestBody CmsContent cmsContent) throws IOException {
        try {
            authoringManager.editContent(id, cmsContent.getName(), cmsContent.getTitle(), cmsContent.getUri(), cmsContent.getSummary(), cmsContent.getContent());
        } catch (AuthoringException e) {
            String msg = String.format("Cannot edit content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content/{id}", method = RequestMethod.DELETE)
    public void deleteContent(HttpServletResponse response,
                              @PathVariable(value = "id") String id) throws IOException {
        try {
            authoringManager.deleteContent(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot delete content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content/{id}/tags", method = RequestMethod.POST)
    public void addContentTags(HttpServletResponse response,
                               @PathVariable(value = "id") String id,
                               @RequestParam(value = "tags") String tags) throws IOException {
        try {
            authoringManager.addContentTags(id, tags);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot add tags to content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/content/{id}/tag", method = RequestMethod.DELETE)
    public void removeContentTags(HttpServletResponse response,
                                  @PathVariable(value = "id") String id,
                                  @RequestParam(value = "tag") String tag) throws IOException {
        try {
            authoringManager.removeContentTags(id, tag);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot remove tags to content. Reason: %s", e.toString());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
    }
}
