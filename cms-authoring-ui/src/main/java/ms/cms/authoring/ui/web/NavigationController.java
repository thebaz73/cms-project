package ms.cms.authoring.ui.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

/**
 * NavigationController
 * Created by thebaz on 02/04/15.
 */
@Controller(value = "navigationController")
public class NavigationController {
    @RequestMapping("/")
    public String dashboard(Map<String, Object> model) {
        model.put("date", new Date());
        return "dashboard";
    }
}
