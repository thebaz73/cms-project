package sparkle.cms.content.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.domain.CmsContent;

/**
 * ContentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "searchWebService")
@RequestMapping(value = "/api")
public class SearchService {
    @Autowired
    private CmsContentRepository contentRepository;

    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/authors/{siteId}/{authorId}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsContent>> contents(PagedResourcesAssembler assembler,
                                                    @PathVariable("siteId") String siteId,
                                                    @PathVariable("authorId") String authorId,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageable = new PageRequest(page, size, Sort.Direction.DESC, "modificationDate");
        Page<CmsContent> contents = contentRepository.findBySiteIdAndPublished(siteId, true, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }
}
