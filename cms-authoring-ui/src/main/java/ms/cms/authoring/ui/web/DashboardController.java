package ms.cms.authoring.ui.web;

import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.authoring.ui.domain.AuthoringStatus;
import ms.cms.authoring.ui.domain.Datatable;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import ms.cms.registration.common.business.RegistrationException;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static ms.cms.utils.UserUtils.*;

/**
 * NavigationController
 * Created by thebaz on 02/04/15.
 */
@Controller(value = "navigationController")
public class DashboardController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping({"/"})
    public String home() {
        return "redirect:/home";
    }

    @RequestMapping(value = {"/home/status"}, method = RequestMethod.GET)
    @ResponseBody
    public AuthoringStatus authoringStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        AuthoringStatus authoringStatus = new AuthoringStatus();
        try {
            CmsUser cmsUser = registrationManager.findUser(request.getRemoteUser());
            if (isAdmin(cmsUser)) {
                authoringStatus.setSitesCount(0);
                authoringStatus.setContentsCount(0);
                authoringStatus.setAuthorsCount(0);
                authoringStatus.setCommentsCount(0);
            } else if (isWebmaster(cmsUser)) {
                List<CmsSite> cmsSites = registrationManager.findSites(cmsUser.getId());
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
            } else if (isAuthor(cmsUser)) {
                authoringStatus.setSitesCount(1);
                CmsSite cmsSite = registrationManager.findAuthoredSite(cmsUser.getId());
                int contentsCount = authoringManager.countContents(cmsSite);
                int authorsCount = registrationManager.findSiteAuthors(cmsSite.getId()).size();
                authoringStatus.setContentsCount(contentsCount);
                authoringStatus.setAuthorsCount(authorsCount);
                authoringStatus.setCommentsCount(0);
            }
        } catch (RegistrationException e) {
            String msg = String.format("Cannot create dashboard. Reason: %s", e.getMessage());
            logger.info(msg, e);
            response.sendError(400, msg);
        }
        return authoringStatus;
    }

    @RequestMapping(value = {"/home/comments"}, method = RequestMethod.GET)
    @ResponseBody
    public Datatable<List<Object>> comments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datatable<List<Object>> datatable = new Datatable<>();
        datatable.setDraw(1);
        datatable.setRecordsFiltered(1);
        datatable.setRecordsTotal(1);
        List<Object> list = new ArrayList<>();
        list.add(new Date());
        list.add("Half Blood prince");
        list.add("Severus Snape");
        list.add("I am Half Blood Prince");
        list.add(UUID.randomUUID().toString());
        datatable.getData().add(list);
        return datatable;
    }

    @RequestMapping(value = {"/home/contents"}, method = RequestMethod.GET)
    @ResponseBody
    public Datatable<List<Object>> contents(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Datatable<List<Object>> datatable = new Datatable<>();
        datatable.setDraw(1);
        datatable.setRecordsFiltered(1);
        datatable.setRecordsTotal(1);
        List<Object> list = new ArrayList<>();
        list.add(new Date());
        list.add("Half Blood prince");
        list.add("/half_blood_prince");
        list.add("bla bls");
        list.add(UUID.randomUUID().toString());
        datatable.getData().add(list);
        return datatable;
    }

    @RequestMapping({"/home"})
    public String dashboard() {
        return "index";
    }
}
