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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.domain.CmsContent;

/**
 * ContentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "authorWebService")
@RequestMapping(value = "/api")
public class AuthorService {
    @Autowired
    private CmsContentRepository contentRepository;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsContent>> contents(PagedResourcesAssembler assembler,
                                                    @RequestParam("q") String query,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageable = new PageRequest(page, size, Sort.Direction.DESC, "modificationDate");
        Page<CmsContent> contents = contentRepository.findAll(pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }
}
