package ms.cms.authoring.common.business;

import ms.cms.data.CmsPageRepository;
import ms.cms.data.CmsPostRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.domain.CmsPage;
import ms.cms.domain.CmsPost;
import ms.cms.domain.CmsSite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

import static ms.cms.authoring.common.utils.AuthoringUtils.abbreviateHtml;
import static ms.cms.authoring.common.utils.AuthoringUtils.toPrettyURL;

/**
 * AuthoringManager
 * Created by thebaz on 27/03/15.
 */
@Component
public class AuthoringManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsPostRepository cmsPostRepository;
    private int maxWidth;

    public void initialize(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public void createPage(String siteId, String name, String title, String uri, String summary, String content) throws AuthoringException {
        CmsSite cmsSite = cmsSiteRepository.findOne(siteId);
        if (cmsSite == null) {
            throw new AuthoringException("Site not found");
        }
        if (name.isEmpty()) {
            name = title;
        }
        if (uri.isEmpty()) {
            uri = toPrettyURL(title);
        }
        if (summary.isEmpty()) {
            summary = abbreviateHtml(content, maxWidth, false);
        }

        CmsPage cmsPage = new CmsPage(cmsSite.getId(), name, title, uri, new Date(), summary, content);
        cmsPageRepository.save(cmsPage);
    }

    public CmsPage findPage(String id) throws AuthoringException {
        CmsPage cmsPage = cmsPageRepository.findOne(id);
        if (cmsPage != null) {
            return cmsPage;
        }

        throw new AuthoringException("Wrong search parameter");
    }

    public CmsPage findPageByUri(String siteId, String uri) throws AuthoringException {
        List<CmsPage> bySiteIdAndUri = cmsPageRepository.findBySiteIdAndUri(siteId, uri);
        if (!bySiteIdAndUri.isEmpty()) {
            return bySiteIdAndUri.get(0);
        }

        throw new AuthoringException("Wrong search parameter");
    }

    public void editPage(String id, String name, String title, String uri, String summary, String content) throws AuthoringException {
        CmsPage cmsPage = cmsPageRepository.findOne(id);
        if (cmsPage == null) {
            throw new AuthoringException("Page not found");
        }
        if (name.isEmpty()) {
            name = title;
        }
        if (uri.isEmpty()) {
            uri = toPrettyURL(title);
        }
        if (summary.isEmpty()) {
            summary = abbreviateHtml(content, maxWidth, false);
        }

        cmsPage.setName(name);
        cmsPage.setTitle(title);
        cmsPage.setUri(uri);
        cmsPage.setModificationDate(new Date());
        cmsPage.setSummary(summary);
        cmsPage.setContent(content);

        cmsPageRepository.save(cmsPage);
    }

    public void deletePage(String id) throws AuthoringException {
        CmsPage cmsPage = cmsPageRepository.findOne(id);
        if (cmsPage == null) {
            throw new AuthoringException("Page not found");
        }

        cmsPageRepository.delete(cmsPage);
    }

    public void createPost(String siteId, String name, String title, String uri, String summary, String content) throws AuthoringException {
        CmsSite cmsSite = cmsSiteRepository.findOne(siteId);
        if (cmsSite == null) {
            throw new AuthoringException("Site not found");
        }
        if (name.isEmpty()) {
            name = title;
        }
        if (uri.isEmpty()) {
            uri = toPrettyURL(title);
        }
        if (summary.isEmpty()) {
            summary = abbreviateHtml(content, maxWidth, false);
        }

        CmsPost cmsPost = new CmsPost(cmsSite.getId(), name, title, uri, new Date(), summary, content);
        cmsPostRepository.save(cmsPost);
    }

    public CmsPost findPost(String id) throws AuthoringException {
        CmsPost cmsPost = cmsPostRepository.findOne(id);
        if (cmsPost != null) {
            return cmsPost;
        }

        throw new AuthoringException("Wrong search parameter");
    }

    public CmsPost findPostByUri(String siteId, String uri) throws AuthoringException {
        List<CmsPost> bySiteIdAndUri = cmsPostRepository.findBySiteIdAndUri(siteId, uri);
        if (!bySiteIdAndUri.isEmpty()) {
            return bySiteIdAndUri.get(0);
        }

        throw new AuthoringException("Wrong search parameter");
    }

    public void editPost(String id, String name, String title, String uri, String summary, String content) throws AuthoringException {
        CmsPost cmsPost = cmsPostRepository.findOne(id);
        if (cmsPost == null) {
            throw new AuthoringException("Post not found");
        }
        if (name.isEmpty()) {
            name = title;
        }
        if (uri.isEmpty()) {
            uri = toPrettyURL(title);
        }
        if (summary.isEmpty()) {
            summary = abbreviateHtml(content, maxWidth, false);
        }

        cmsPost.setName(name);
        cmsPost.setTitle(title);
        cmsPost.setUri(uri);
        cmsPost.setModificationDate(new Date());
        cmsPost.setSummary(summary);
        cmsPost.setContent(content);

        cmsPostRepository.save(cmsPost);
    }

    public void deletePost(String id) throws AuthoringException {
        CmsPost cmsPost = cmsPostRepository.findOne(id);
        if (cmsPost == null) {
            throw new AuthoringException("Post not found");
        }

        cmsPostRepository.delete(cmsPost);
    }
}
