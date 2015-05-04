package ms.cms.authoring.ui.web;

import ms.cms.authoring.common.business.AuthoringManager;
import ms.cms.registration.common.business.RegistrationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * SearchController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "searchController")
public class SearchController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping(value = {"/search"}, params = {"query"}, method = RequestMethod.GET)
    public String search(Map<String, Object> model, String query) {
        model.put("date", new Date());
        model.put("docs", new ArrayList<>());
        model.put("query", query);
        return "search";
    }
}
