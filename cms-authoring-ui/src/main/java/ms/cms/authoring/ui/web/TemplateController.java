package ms.cms.authoring.ui.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * TemplateController
 * Created by bazzoni on 03/04/2015.
 */
@Controller
public class TemplateController {

    @RequestMapping(value = "/template/{name}", method = GET)
    public String show(@PathVariable("name") String name) {
        return "handlebars/" + name;
    }

}
