package sparkle.cms.authoring.ui.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sparkle.cms.authoring.common.business.AuthoringManager;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.registration.common.business.RegistrationManager;

import java.util.Date;

/**
 * SearchController
 * Created by thebaz on 12/04/15.
 */
@Controller(value = "searchController")
public class SearchController {
    //private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RegistrationManager registrationManager;
    @Autowired
    private AuthoringManager authoringManager;

    @RequestMapping(value = {"/search"}, params = {"query"}, method = RequestMethod.GET)
    public String search(ModelMap model, String query,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        model.put("date", new Date());
        model.put("query", query);
        Pageable pageable = new PageRequest(page, pageSize,
                Sort.Direction.ASC, "popularity");
        Page<CmsContent> docs = authoringManager.searchContent(query, pageable);
        model.put("docs", docs);
        return "search";
    }
}
