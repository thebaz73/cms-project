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
import sparkle.cms.content.service.web.domain.CommentData;
import sparkle.cms.data.CmsCommentRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.data.CmsUserRepository;
import sparkle.cms.domain.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * CommentService
 * Created by bazzoni on 02/04/2015.
 */
@RestController(value = "commentWebService")
@RequestMapping(value = "/api")
public class CommentService {
    @Autowired
    private CmsCommentRepository cmsCommentRepository;
    @Autowired
    private CmsUserRepository cmsUserRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    HttpEntity<Void> createComment(CommentData commentData) {
        final String email = commentData.getEmail();
        final List<CmsUser> byEmail = cmsUserRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            CmsUser viewer = new CmsUser("", email, email, "", new Date(), Arrays.asList(new CmsRole(Role.ROLE_USER), new CmsRole(Role.ROLE_VIEWER)));
            cmsUserRepository.save(viewer);
            byEmail.add(viewer);
        }
        CmsComment cmsComment = new CmsComment(commentData.getContentId(), commentData.getTimestamp(), commentData.getTitle(), commentData.getContent(), byEmail.get(0));
        final CmsSite cmsSite = cmsSiteRepository.findOne(commentData.getSiteId());
        cmsComment.setApproved(CommentApprovalMode.SELF_APPROVAL.equals(cmsSite.getCommentApprovalMode()));
        cmsCommentRepository.save(cmsComment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Secured({"ROLE_MANAGER"})
    @RequestMapping(value = "/comments/{contentId}", method = RequestMethod.GET)
    HttpEntity<PagedResources<CmsComment>> comments(PagedResourcesAssembler assembler,
                                                    @PathVariable("contentId") String contentId,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "10") int size) {

        PageRequest pageable = new PageRequest(page, size, Sort.Direction.DESC, "modificationDate");
        Page<CmsComment> contents = cmsCommentRepository.findByContentIdAndApproved(contentId, true, pageable);
        return new ResponseEntity<>(assembler.toResource(contents), HttpStatus.OK);
    }
}
