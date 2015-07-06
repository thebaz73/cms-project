package sparkle.cms.authoring.common.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sparkle.cms.authoring.common.utils.AuthoringUtils;
import sparkle.cms.data.CmsCommentRepository;
import sparkle.cms.data.CmsContentRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.data.CmsTagRepository;
import sparkle.cms.domain.CmsContent;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsTag;
import sparkle.cms.plugin.mgmt.PluginService;
import sparkle.cms.plugin.mgmt.search.SparkleDocument;

import java.util.*;

import static sparkle.cms.authoring.common.utils.AuthoringUtils.abbreviateHtml;
import static sparkle.cms.authoring.common.utils.AuthoringUtils.toPrettyURL;

/**
 * AuthoringManager
 * Created by thebaz on 27/03/15.
 */
@Component
public class AuthoringManager {
    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsContentRepository cmsContentRepository;
    @Autowired
    private CmsCommentRepository cmsCommentRepository;
    @Autowired
    private CmsTagRepository cmsTagRepository;
    @Autowired
    private PluginService pluginService;
    private int maxWidth;

    public void initialize(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Transactional
    public void createContent(String siteId, String name, String title, String uri, String summary, String content) throws AuthoringException {
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

        CmsContent cmsContent = new CmsContent(cmsSite.getId(), name, title, uri, new Date(), summary, content);
        CmsContent savedCmsContent = cmsContentRepository.save(cmsContent);
        if (pluginService.getSearchPlugin() != null) {
            pluginService.getSearchPlugin().addToIndex(savedCmsContent.getId(), savedCmsContent.getSiteId(), savedCmsContent.getTitle(), savedCmsContent.getUri(),
                    savedCmsContent.getModificationDate().getTime(), savedCmsContent.getSummary(), savedCmsContent.getContent());
        }
    }

    public CmsContent findContent(String id) throws AuthoringException {
        CmsContent cmsContent = cmsContentRepository.findOne(id);
        if (cmsContent != null) {
            return cmsContent;
        }

        throw new AuthoringException("Wrong search parameter");
    }

    public CmsContent findContentByUri(String siteId, String uri) throws AuthoringException {
        List<CmsContent> bySiteIdAndUri = cmsContentRepository.findBySiteIdAndUri(siteId, uri);
        if (!bySiteIdAndUri.isEmpty()) {
            return bySiteIdAndUri.get(0);
        }

        throw new AuthoringException("Wrong search parameter");
    }

    @Transactional
    public void editContent(String id, String name, String title, String uri, String summary, String content) throws AuthoringException {
        CmsContent cmsContent = cmsContentRepository.findOne(id);
        if (cmsContent == null) {
            throw new AuthoringException("Content not found");
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

        cmsContent.setName(name);
        cmsContent.setTitle(title);
        cmsContent.setUri(uri);
        cmsContent.setModificationDate(new Date());
        cmsContent.setSummary(summary);
        cmsContent.setContent(content);

        CmsContent savedCmsContent = cmsContentRepository.save(cmsContent);
        if (pluginService.getSearchPlugin() != null) {
            pluginService.getSearchPlugin().update(savedCmsContent.getId(), savedCmsContent.getSiteId(), savedCmsContent.getTitle(), savedCmsContent.getUri(),
                    savedCmsContent.getModificationDate().getTime(), savedCmsContent.getSummary(), savedCmsContent.getContent());
        }
    }

    public void deleteContent(String id) throws AuthoringException {
        CmsContent cmsContent = cmsContentRepository.findOne(id);
        if (cmsContent == null) {
            throw new AuthoringException("Content not found");
        }
        for (CmsTag cmsTag : cmsContent.getTags()) {
            cmsTag.getContentIds().remove(cmsContent.getId());
            cmsTag.setPopularity(cmsTag.getPopularity() - 1);
            cmsTagRepository.save(cmsTag);
        }
        cmsContentRepository.delete(cmsContent);
        if (pluginService.getSearchPlugin() != null) {
            pluginService.getSearchPlugin().deleteFromIndex(cmsContent.getId());
        }
    }

    public void addContentTags(String id, String tags) throws AuthoringException {
        CmsContent cmsContent = cmsContentRepository.findOne(id);
        if (cmsContent == null) {
            throw new AuthoringException("Content not found");
        }
        cmsContent.getTags().clear();
        for (String tag : tags.split(",")) {
            if (!tag.isEmpty()) {
                String uri = AuthoringUtils.toPrettyURL(tag.trim());
                List<CmsTag> bySiteIdAndTag = cmsTagRepository.findBySiteIdAndUri(cmsContent.getSiteId(), uri);
                CmsTag cmsTag;
                if (!bySiteIdAndTag.isEmpty()) {
                    cmsTag = bySiteIdAndTag.get(0);
                } else {
                    cmsTag = new CmsTag(cmsContent.getSiteId(), tag.trim(), uri);
                }
                Set<String> contentIds = cmsTag.getContentIds();
                if (!contentIds.contains(cmsContent.getId())) {
                    contentIds.add(cmsContent.getId());
                    cmsTag.setPopularity(cmsTag.getPopularity() + 1);
                    cmsTagRepository.save(cmsTag);
                }

                cmsContent.getTags().add(cmsTag);
            }
        }
        cmsContentRepository.save(cmsContent);
    }

    public void removeContentTags(String id, String tag) throws AuthoringException {
        CmsContent cmsContent = cmsContentRepository.findOne(id);
        if (cmsContent == null) {
            throw new AuthoringException("Content not found");
        }
        List<CmsTag> bySiteIdAndTag = cmsTagRepository.findBySiteIdAndUri(cmsContent.getSiteId(), tag);
        if (bySiteIdAndTag.isEmpty()) {
            throw new AuthoringException("Tag not found");
        }
        List<CmsTag> allTags = new ArrayList<>(cmsContent.getTags());
        allTags.stream().filter(cmsTag -> cmsTag.getTag().equalsIgnoreCase(tag.trim())).forEach(cmsTag -> cmsContent.getTags().remove(cmsTag));
        cmsContentRepository.save(cmsContent);

        CmsTag cmsTag = bySiteIdAndTag.get(0);
        List<String> allIds = new ArrayList<>(cmsTag.getContentIds());
        allIds.stream().filter(commentId -> commentId.equals(id)).forEach(commentId -> cmsTag.getContentIds().remove(commentId));
        cmsTag.setPopularity(cmsTag.getPopularity() - 1);
        cmsTagRepository.save(cmsTag);
    }

    public int countContents(CmsSite cmsSite) {
        return cmsContentRepository.countBySiteId(cmsSite.getId());
    }

    public int countComments(CmsSite cmsSite) {
        return cmsCommentRepository.countBySiteId(cmsSite.getId());
    }

    public List<? extends SparkleDocument> searchContent(String siteId, String query) {
        if (pluginService.getSearchPlugin() != null) {
            return pluginService.getSearchPlugin().search(siteId, query);
        }

        return Collections.emptyList();
    }
}
