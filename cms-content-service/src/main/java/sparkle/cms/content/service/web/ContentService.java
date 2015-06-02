package sparkle.cms.content.service.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.data.CmsTagRepository;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsTag;

import java.util.ArrayList;
import java.util.List;

/**
 * ContentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "contentWebService")
@RequestMapping(value = "/api")
public class ContentService {
    @Autowired
    private CmsContentRepository contentRepository;
    @Autowired
    private CmsTagRepository tagRepository;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/contents/{siteId}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsContent>> contents(PagedResourcesAssembler assembler,
                                                    @PathVariable("siteId") String siteId,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageable = new PageRequest(page, size, Sort.Direction.DESC, "modificationDate");
        Page<CmsContent> contents = contentRepository.findBySiteIdAndPublished(siteId, true, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }

    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/contents/{siteId}", params = {"tag"}, method = RequestMethod.GET)
    Iterable<CmsContent> tagContents(@PathVariable("siteId") String siteId,
                                     @RequestParam("tag") String tag) {
        Iterable<CmsContent> contents = new ArrayList<>();
        List<CmsTag> bySiteIdAndTag = tagRepository.findBySiteIdAndUri(siteId, tag);
        if (!bySiteIdAndTag.isEmpty()) {
            contents = contentRepository.findAll(bySiteIdAndTag.get(0).getContentIds());
        }
        return contents;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/contents/{siteId}/{uri}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsContent>> content(Pageable pageable,
                                                   PagedResourcesAssembler assembler,
                                                   @PathVariable("siteId") String siteId,
                                                   @PathVariable("uri") String uri) {

        Page<CmsContent> contents = contentRepository.findBySiteIdAndUri(siteId, uri, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }
}
