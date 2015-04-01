package ms.cms.authoring.service.web;

import ms.cms.authoring.common.business.AuthoringException;
import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.domain.CmsPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * PostService
 * Created by thebaz on 27/03/15.
 */
@RestController
@RequestMapping(value = "/api")
public class PostService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuthoringManager authoringManager;

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public void createPost(HttpServletResponse response, @RequestBody CmsPost cmsPost) throws IOException {
        try {
            authoringManager.createPost(cmsPost.getSiteId(), cmsPost.getName(), cmsPost.getTitle(), cmsPost.getUri(), cmsPost.getSummary(), cmsPost.getContent());
        } catch (AuthoringException e) {
            String msg = String.format("Cannot create post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post/{id}", method = RequestMethod.GET)
    public CmsPost findPost(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        try {
            return authoringManager.findPost(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post/byUri", method = RequestMethod.GET)
    public CmsPost findPostByUri(HttpServletResponse response,
                                 @RequestParam(value = "siteId") String siteId,
                                 @RequestParam(value = "uri") String uri) throws IOException {
        try {
            return authoringManager.findPostByUri(siteId, uri);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot find post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }

        return null;
    }

    //TODO change method to and json object post
    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post/{id}", method = RequestMethod.PUT)
    public void editPost(HttpServletResponse response,
                         @PathVariable(value = "id") String id, @RequestBody CmsPost cmsPost) throws IOException {
        try {
            authoringManager.editPost(id, cmsPost.getName(), cmsPost.getTitle(), cmsPost.getUri(), cmsPost.getSummary(), cmsPost.getContent());
        } catch (AuthoringException e) {
            String msg = String.format("Cannot edit post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post/{id}", method = RequestMethod.DELETE)
    public void deletePost(HttpServletResponse response,
                           @PathVariable(value = "id") String id) throws IOException {
        try {
            authoringManager.deletePost(id);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot delete post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post/{id}/tags", method = RequestMethod.POST)
    public void addPostTags(HttpServletResponse response,
                            @PathVariable(value = "id") String id,
                            @RequestParam(value = "tags") String tags) throws IOException {
        try {
            authoringManager.addPostTags(id, tags);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot add tags to post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }

    @Secured({"ROLE_MANAGER", "ROLE_AUTHOR"})
    @RequestMapping(value = "/post/{id}/tag", method = RequestMethod.DELETE)
    public void removePostTags(HttpServletResponse response,
                               @PathVariable(value = "id") String id,
                               @RequestParam(value = "tag") String tag) throws IOException {
        try {
            authoringManager.removePostTags(id, tag);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot remove tags to post. Reason: %s", e.toString());
            logger.info(msg);
            response.sendError(400, msg);
        }
    }
}
