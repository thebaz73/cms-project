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
import sparkle.cms.data.CmsCommentRepository;
import sparkle.cms.domain.CmsComment;

/**
 * CommentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "commentWebService")
@RequestMapping(value = "/api")
public class CommentService {
    @Autowired
    private CmsCommentRepository cmsCommentRepository;

    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    HttpEntity<Void> createComment(CmsComment cmsComment) {
        //TODO Add control based on site config (ie Comment always valid or upon approval)
        cmsCommentRepository.save(cmsComment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/contents/{contentId}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsComment>> contents(PagedResourcesAssembler assembler,
                                                    @PathVariable("contentId") String contentId,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageable = new PageRequest(page, size, Sort.Direction.DESC, "modificationDate");
        Page<CmsComment> contents = cmsCommentRepository.findByContentId(contentId, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }
}
