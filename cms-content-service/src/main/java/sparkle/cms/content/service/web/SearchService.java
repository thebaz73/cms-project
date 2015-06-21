package sparkle.cms.content.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.plugin.mgmt.PluginService;
import sparkle.cms.plugin.mgmt.search.SparkleDocument;

import java.util.List;

/**
 * ContentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "searchWebService")
@RequestMapping(value = "/api")
public class SearchService {
    @Autowired
    private PluginService pluginService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/search/{siteId}", params = {"q"}, method = RequestMethod.GET)
    HttpEntity<List<? extends SparkleDocument>> contents(@PathVariable("siteId") String siteId,
                                                         @RequestParam("q") String searchTerms) {

        List<? extends SparkleDocument> contents = pluginService.getSearchPlugin().search(siteId, searchTerms);
        return new ResponseEntity<>(contents, HttpStatus.OK);
    }
}
