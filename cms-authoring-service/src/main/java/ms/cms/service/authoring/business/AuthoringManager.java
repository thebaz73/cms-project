package ms.cms.service.authoring.business;

import ms.cms.data.CmsPageRepository;
import ms.cms.data.CmsSiteRepository;
import ms.cms.domain.CmsPage;
import ms.cms.domain.CmsSite;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Date;

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
    @Value("${summary.max.width}")
    private int maxWidth;

    public void initialize() {
        //initialize values using Cloud ConfigService
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
            summary = StringUtils.abbreviate(content, maxWidth);
        }

        CmsPage cmsPage = new CmsPage(name, title, uri, new Date(), summary, content);
        cmsPageRepository.save(cmsPage);
    }

    private String toPrettyURL(String string) {
        return Normalizer.normalize(string.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "-");
    }

    public CmsPage findPage(String param) throws AuthoringException {
        CmsPage cmsPage = cmsPageRepository.findOne(param);
        if (cmsPage != null) {
            return cmsPage;
        }

        throw new AuthoringException("Wrong search parameter");
    }
}
