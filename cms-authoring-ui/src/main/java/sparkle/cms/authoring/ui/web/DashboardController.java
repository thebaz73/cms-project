package sparkle.cms.authoring.ui.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sparkle.cms.authoring.common.business.AuthoringManager;
import sparkle.cms.authoring.common.business.CommentManager;
import sparkle.cms.authoring.common.business.ContentManager;
import sparkle.cms.authoring.ui.domain.AuthoringStatus;
import sparkle.cms.authoring.ui.domain.Datatable;
import sparkle.cms.domain.CmsComment;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;
import sparkle.cms.registration.common.business.RegistrationException;
import sparkle.cms.registration.common.business.RegistrationManager;
import sparkle.cms.registration.common.business.SiteManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static sparkle.cms.utils.UserUtils.isAuthor;
import static sparkle.cms.utils.UserUtils.isWebmaster;

/**
 * DashboardController
 * Created by thebaz on 02/04/15.
 */
@Controller(value = "navigationController")
public class DashboardController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;
    @Autowired
    private ContentManager contentManager;
    @Autowired
    private CommentManager commentManager;
    @Autowired
    private SiteManager siteManager;
    private List<CmsSite> cmsSites = new ArrayList<>();

    @RequestMapping({"/"})
    public String home() {
        return "redirect:/home";
    }

    @RequestMapping(value = {"/home/status"}, method = RequestMethod.GET)
    @ResponseBody
    public AuthoringStatus authoringStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthoringStatus authoringStatus = new AuthoringStatus();
        try {
            loadUserSites(registrationManager.findUser(request.getRemoteUser()));
            authoringStatus.setSitesCount(cmsSites.size());
            int contentsCount = 0;
            int authorsCount = 0;
            for (CmsSite cmsSite : cmsSites) {
                contentsCount += authoringManager.countContents(cmsSite);
                authorsCount += registrationManager.findSiteAuthors(cmsSite.getId()).size();
            }
            authoringStatus.setContentsCount(contentsCount);
            authoringStatus.setAuthorsCount(authorsCount);
            authoringStatus.setCommentsCount(0);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create dashboard. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return authoringStatus;
    }

    @RequestMapping(value = {"/home/sites"}, method = RequestMethod.GET)
    @ResponseBody
    public Datatable<List<Object>> sites(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datatable<List<Object>> dataTable = new Datatable<>();
        try {
            int draw = 0;
            try {
                draw = Integer.parseInt(request.getParameter("draw"));
            } catch (NumberFormatException e) {
                //ignore
            }
            dataTable.setDraw(draw);
            loadUserSites(registrationManager.findUser(request.getRemoteUser()));
            for (CmsSite site : cmsSites) {
                List<Object> list = new ArrayList<>();
                list.add(site.getName());
                list.add(site.getAddress());
                dataTable.getData().add(list);
            }
            dataTable.setRecordsFiltered(cmsSites.size());
            dataTable.setRecordsTotal(cmsSites.size());
        } catch (RegistrationException e) {
            String msg = String.format("Cannot load comments. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return dataTable;
    }

    @RequestMapping(value = {"/home/authors"}, method = RequestMethod.GET)
    @ResponseBody
    public Datatable<List<Object>> authors(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datatable<List<Object>> dataTable = new Datatable<>();
        try {
            int draw = 0;
            try {
                draw = Integer.parseInt(request.getParameter("draw"));
            } catch (NumberFormatException e) {
                //ignore
            }
            dataTable.setDraw(draw);
            loadUserSites(registrationManager.findUser(request.getRemoteUser()));
            int recordCount = 0;
            for (CmsSite cmsSite : cmsSites) {
                List<CmsUser> siteAuthors = registrationManager.findSiteAuthors(cmsSite.getId());
                for (CmsUser siteAuthor : siteAuthors) {
                    List<Object> list = new ArrayList<>();
                    list.add(siteAuthor.getName());
                    list.add(siteAuthor.getRoles().get(1).getRole());
                    dataTable.getData().add(list);
                    recordCount++;
                }
            }
            dataTable.setRecordsFiltered(recordCount);
            dataTable.setRecordsTotal(recordCount);
        } catch (RegistrationException e) {
            String msg = String.format("Cannot load comments. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return dataTable;
    }

    @RequestMapping(value = {"/home/comments"}, method = RequestMethod.GET)
    @ResponseBody
    public Datatable<List<Object>> comments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datatable<List<Object>> dataTable = new Datatable<>();
        try {
            int draw = 0;
            try {
                draw = Integer.parseInt(request.getParameter("draw"));
            } catch (NumberFormatException e) {
                //ignore
            }
            dataTable.setDraw(draw);
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            loadUserSites(cmsUser);

            List<CmsComment> comments = new ArrayList<>();
            if (isWebmaster(cmsUser)) {
                comments = commentManager.findAllComments(cmsUser);
            } else if (isAuthor(cmsUser)) {
                comments = commentManager.findAuthoredComments(cmsUser);
            }
            for (CmsComment comment : comments) {
                List<Object> list = new ArrayList<>();
                list.add(comment.getTimestamp());
                list.add(comment.getContentId());
                list.add(comment.getViewer().getName());
                list.add(comment.getContent());
                list.add(comment.getId());
                dataTable.getData().add(list);
            }
            dataTable.setRecordsFiltered(comments.size());
            dataTable.setRecordsTotal(comments.size());
        } catch (RegistrationException e) {
            String msg = String.format("Cannot load comments. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return dataTable;
    }

    @RequestMapping(value = {"/home/contents"}, method = RequestMethod.GET)
    @ResponseBody
    public Datatable<List<Object>> contents(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datatable<List<Object>> dataTable = new Datatable<>();
        try {
            int draw = 0;
            try {
                draw = Integer.parseInt(request.getParameter("draw"));
            } catch (NumberFormatException e) {
                //ignore
            }
            dataTable.setDraw(draw);
            loadUserSites(registrationManager.findUser(request.getRemoteUser()));
            List<CmsContent> contents = contentManager.findSitesContents(cmsSites);
            for (CmsContent content : contents) {
                List<Object> list = new ArrayList<>();
                list.add(content.getModificationDate());
                list.add(content.getTitle());
                list.add(content.getUri());
                list.add(content.getSummary());
                list.add(content.getId());
                dataTable.getData().add(list);
            }
            dataTable.setRecordsFiltered(contents.size());
            dataTable.setRecordsTotal(contents.size());
        } catch (RegistrationException e) {
            String msg = String.format("Cannot load comments. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return dataTable;
    }

    @RequestMapping({"/home"})
    public String dashboard() {
        return "index";
    }

    private List<CmsSite> loadUserSites(CmsUser cmsUser) throws RegistrationException {
        if (isWebmaster(cmsUser)) {
            cmsSites = siteManager.findSites(cmsUser);
        } else if (isAuthor(cmsUser)) {
            cmsSites = Arrays.asList(siteManager.findAuthoredSite(cmsUser));
        }

        return cmsSites;
    }
}
