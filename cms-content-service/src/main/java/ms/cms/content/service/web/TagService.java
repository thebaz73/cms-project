package ms.cms.content.service.web;

import ms.cms.data.CmsTagRepository;
import ms.cms.domain.CmsTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ContentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "tagWebService")
@RequestMapping(value = "/api")
public class TagService {
    @Autowired
    private CmsTagRepository tagRepository;

    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/tags/{siteId}", method = RequestMethod.GET)
    List<CmsTag> tags(@PathVariable("siteId") String siteId) {
        return tagRepository.findBySiteId(siteId);
    }
}
