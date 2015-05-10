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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.authoring.common.business.*;
import sparkle.cms.authoring.ui.domain.ContentData;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsTag;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * ContentController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "contentController")
public class ContentController {
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
    @Autowired
    private TagManager tagManager;

    @ModelAttribute("allSites")
    public List<CmsSite> allSites(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                return siteManager.findAllSites(cmsUser);
            } else if (isAuthor(cmsUser)) {
                return siteManager.findAuthoredSites(cmsUser);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage sites. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @ModelAttribute("allContents")
    public Page<CmsContent> allContents(HttpServletRequest request, HttpServletResponse response,
                                        @RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize) throws IOException {
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            Pageable pageable = new PageRequest(page, pageSize, Sort.Direction.ASC, "name");
            if (isWebmaster(cmsUser)) {
                return contentManager.findAllContents(cmsUser, pageable);
            } else if (isAuthor(cmsUser)) {
                return contentManager.findAuthoredContents(cmsUser, pageable);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return null;
    }

    @RequestMapping(value = {"/tags"}, method = RequestMethod.GET)
    @ResponseBody
    public List<String> tags(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<String> tagList = new ArrayList<>();
        try {
            List<CmsSite> allSites = new ArrayList<>();
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isWebmaster(cmsUser)) {
                allSites = siteManager.findAllSites(cmsUser);
            } else if (isAuthor(cmsUser)) {
                allSites = siteManager.findAuthoredSites(cmsUser);
            }
            for (CmsSite site : allSites) {
                List<CmsTag> siteTags = tagManager.findSiteTags(site.getId());
                tagList.addAll(siteTags.stream().map(CmsTag::getTag).collect(Collectors.toList()));
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot manage sites. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return tagList;
    }


    @RequestMapping({"/contents"})
    public String show(ModelMap model) {
        model.put("contentData", new ContentData());
        model.put("mode", "add");
        return "contents";
    }

    @RequestMapping(value = {"/contents"}, method = RequestMethod.POST)
    public String addContent(HttpServletResponse response, @ModelAttribute("contentData") ContentData contentData,
                             final BindingResult bindingResult, final ModelMap model) throws IOException {
        if (bindingResult.hasErrors()) {
            return "contents";
        }
        try {
            authoringManager.createContent(contentData.getSiteId(), "", contentData.getTitle(), "", contentData.getSummary(), contentData.getContent());
            model.clear();
        } catch (AuthoringException e) {
            String msg = String.format("Cannot author contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "redirect:/contents";
    }

    @RequestMapping(value = {"/contents/{contentId}"}, method = RequestMethod.PUT)
    public String editContent(HttpServletResponse response, @ModelAttribute("contentData") ContentData contentData,
                              final BindingResult bindingResult, final ModelMap model, @PathVariable("contentId") String contentId) throws IOException {
        if (bindingResult.hasErrors()) {
            return "contents";
        }
        try {
            CmsContent cmsContent = authoringManager.findContent(contentId);
            authoringManager.editContent(contentId, cmsContent.getName(), cmsContent.getTitle(), cmsContent.getUri(), cmsContent.getSummary(), contentData.getContent());
            authoringManager.addContentTags(contentId, contentData.getTags());
            model.clear();
        } catch (AuthoringException e) {
            String msg = String.format("Cannot author contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "redirect:/contents";
    }

    @RequestMapping(value = {"/contents/{contentId}"}, method = RequestMethod.GET)
    public String editMode(HttpServletResponse response, ModelMap model, @PathVariable("contentId") String contentId) throws IOException {
        try {
            CmsContent cmsContent = authoringManager.findContent(contentId);
            ContentData contentData = new ContentData();
            contentData.setSiteId(cmsContent.getSiteId());
            contentData.setTitle(cmsContent.getTitle());
            contentData.setSummary(cmsContent.getSummary());
            contentData.setContent(cmsContent.getContent());
            StringBuilder tagList = new StringBuilder();
            for (CmsTag tag : cmsContent.getTags()) {
                tagList.append(",").append(tag.getTag());
            }
            contentData.setTags(tagList.toString());
            model.put("contentData", contentData);
            model.put("contentId", cmsContent.getId());
            model.put("mode", "edit");
        } catch (AuthoringException e) {
            String msg = String.format("Cannot author contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "contents";
    }

    @RequestMapping(value = {"/publish/{contentId}"}, method = RequestMethod.GET)
    public String publish(HttpServletResponse response, ModelMap model, @PathVariable("contentId") String contentId) throws IOException {
        contentManager.publish(contentId, true);
        return "redirect:/contents";
    }

    @RequestMapping(value = {"/unpublish/{contentId}"}, method = RequestMethod.GET)
    public String unpublish(HttpServletResponse response, ModelMap model, @PathVariable("contentId") String contentId) throws IOException {
        contentManager.publish(contentId, false);
        return "redirect:/contents";
    }

    @RequestMapping(value = {"/contents/{contentId}"}, method = RequestMethod.DELETE)
    public String delete(HttpServletResponse response, @PathVariable("contentId") String contentId) throws IOException {
        try {
            commentManager.deleteContentComments(contentId);
            authoringManager.deleteContent(contentId);
        } catch (AuthoringException e) {
            String msg = String.format("Cannot author contents. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return "redirect:/contents";
    }
}
