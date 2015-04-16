package ms.cms.authoring.common.business;

import ms.cms.data.CmsCommentRepository;
import ms.cms.data.CmsContentRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.domain.CmsComment;
import ms.cms.domain.CmsContent;
import ms.cms.domain.CmsSite;
import ms.cms.domain.CmsUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CommentManager
 * Created by thebaz on 16/04/15.
 */
@Component
public class CommentManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsContentRepository cmsContentRepository;
    @Autowired
    private CmsCommentRepository cmsCommentRepository;

    public Page<CmsComment> findAllComments(CmsUser cmsUser, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsComment> cmsComments = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            List<CmsContent> cmsContents = cmsContentRepository.findBySiteId(cmsSite.getId());
            for (CmsContent cmsContent : cmsContents) {
                cmsComments.addAll(cmsCommentRepository.findByContentId(cmsContent.getId()));
            }
        }
        return new PageImpl<>(cmsComments, pageable, cmsComments.size());
    }

    public Page<CmsComment> findAuthoredComments(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        List<CmsComment> cmsComments = new ArrayList<>();
        List<CmsContent> cmsContents = cmsContentRepository.findBySiteId(cmsSite.getId());
        for (CmsContent cmsContent : cmsContents) {
            cmsComments.addAll(cmsCommentRepository.findByContentId(cmsContent.getId()));
        }
        return new PageImpl<>(cmsComments, pageable, cmsComments.size());
    }
}
