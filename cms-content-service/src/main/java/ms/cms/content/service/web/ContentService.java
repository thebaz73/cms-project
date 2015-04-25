package ms.cms.content.service.web;

import ms.cms.data.CmsContentRepository;
import ms.cms.domain.CmsContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * ContentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "contentWebService")
@RequestMapping(value = "/api")
public class ContentService {
    @Autowired
    private CmsContentRepository contentRepository;

    @RequestMapping(value = "/contents/{siteId}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsContent>> contents(Pageable pageable,
                                                    PagedResourcesAssembler assembler,
                                                    @PathVariable("siteId") String siteId) {

        Page<CmsContent> contents = contentRepository.findBySiteId(siteId, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }

    @RequestMapping(value = "/contents/{siteId}/{uri}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsContent>> content(Pageable pageable,
                                                   PagedResourcesAssembler assembler,
                                                   @PathVariable("siteId") String siteId,
                                                   @PathVariable("uri") String uri) {

        Page<CmsContent> contents = contentRepository.findBySiteIdAndUri(siteId, uri, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }
}
