package ms.cms.authoring.ui.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * NavigationController
 * Created by thebaz on 02/04/15.
 */
@Controller(value = "navigationController")
public class NavigationController {
    @RequestMapping({"/"})
    public String home() {
        return "redirect:home";
    }

    @RequestMapping({"home"})
    public String dashboard(Map<String, Object> model) {
        model.put("date", new Date());
        return "index";
    }

    @RequestMapping({"/user"})
    public String userProfile(Map<String, Object> model) {
        model.put("date", new Date());
        return "user";
    }

    @RequestMapping({"/settings"})
    public String appSettings(Map<String, Object> model) {
        model.put("date", new Date());
        return "settings";
    }

    @RequestMapping({"/contents"})
    public String contentsAuthoring(Map<String, Object> model) {
        model.put("date", new Date());
        return "contents";
    }

    @RequestMapping({"/template"})
    public String templateManagement(Map<String, Object> model) {
        model.put("date", new Date());
        return "template";
    }

    @RequestMapping(value = {"/search"}, params = {"query"}, method = RequestMethod.GET)
    public String search(Map<String, Object> model, String query) {
        model.put("date", new Date());
        model.put("docs", new ArrayList<>());
        model.put("query", query);
        return "search";
    }
}
