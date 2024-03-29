package sparkle.cms.authoring.ui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.authoring.common.business.AuthoringManager;
import sparkle.cms.authoring.common.business.CommentManager;
import sparkle.cms.authoring.common.business.ContentManager;
import sparkle.cms.domain.CmsComment;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * CommentController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "commentController")
public class CommentController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;
    @Autowired
    private SiteManager siteManager;
    @Autowired
    private ContentManager contentManager;
    @Autowired
    private CommentManager commentManager;

    @ModelAttribute("allContents")
    public List<CmsContent> allContents(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return contentManager.findAllContents(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return contentManager.findAuthoredContents(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @ModelAttribute("allComments")
    public Page<CmsComment> allComments(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "name");
            if (isWebmaster(cmsUser)) {
                return commentManager.findAllComments(cmsUser, pageable);
            } else if (isAuthor(cmsUser)) {
                return commentManager.findAuthoredComments(cmsUser, pageable);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage comments. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @RequestMapping({"/comments"})
    public String commentsManagement() {
        return "comments";
    }

    @RequestMapping(value = {"/comments/{commentId}"}, method = RequestMethod.GET)
    public String showComment(ModelMap model, @PathVariable("commentId") String commentId) throws IOException {
        CmsComment cmsComment = commentManager.findComment(commentId);
        model.put("cmsComment", cmsComment);
        model.put("commentId", cmsComment.getId());
        return "comments";
    }

    @RequestMapping(value = {"/comments/approve/{commentId}"}, method = RequestMethod.GET)
    public String approve(@PathVariable("commentId") String commentId) throws IOException {
        commentManager.approve(commentId, true);
        return "redirect:/contents";
    }

    @RequestMapping(value = {"/comments/disapprove/{commentId}"}, method = RequestMethod.GET)
    public String disapprove(@PathVariable("commentId") String commentId) throws IOException {
        commentManager.approve(commentId, false);
        return "redirect:/contents";
    }

    @RequestMapping(value = {"/comments/{commentId}"}, method = RequestMethod.DELETE)
    public String delete(@PathVariable("commentId") String commentId) throws IOException {
        commentManager.deleteComment(commentId);
        return "redirect:/contents";
    }
}
