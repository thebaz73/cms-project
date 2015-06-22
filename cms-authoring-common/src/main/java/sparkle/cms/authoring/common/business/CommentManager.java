package sparkle.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsCommentRepository;
import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.domain.CmsComment;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;

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

    public List<CmsComment> findAllComments(CmsUser cmsUser) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsComment> cmsComments = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            List<CmsContent> cmsContents = cmsContentRepository.findBySiteId(cmsSite.getId());
            for (CmsContent cmsContent : cmsContents) {
                cmsComments.addAll(cmsCommentRepository.findByContentId(cmsContent.getId()));
            }
        }

        return cmsComments;
    }

    public List<CmsComment> findAuthoredComments(CmsUser cmsUser) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        List<CmsComment> cmsComments = new ArrayList<>();
        List<CmsContent> cmsContents = cmsContentRepository.findBySiteId(cmsSite.getId());
        for (CmsContent cmsContent : cmsContents) {
            cmsComments.addAll(cmsCommentRepository.findByContentId(cmsContent.getId()));
        }
        return cmsComments;
    }

    public void deleteSiteComments(String siteId) {
        List<CmsContent> cmsContents = cmsContentRepository.findBySiteId(siteId);
        for (CmsContent cmsContent : cmsContents) {
            cmsCommentRepository.deleteByContentId(cmsContent.getId());
        }
    }

    public void deleteContentComments(String contentId) {
        cmsCommentRepository.deleteByContentId(contentId);
    }
}
