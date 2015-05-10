package sparkle.cms.authoring.common.business;

import com.hp.hpl.jena.graph.Triple;
import org.fcrepo.client.FedoraException;
import org.fcrepo.client.FedoraObject;
import org.fcrepo.client.FedoraRepository;
import org.fcrepo.client.impl.FedoraRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import sparkle.cms.data.CmsAssetRepository;
import sparkle.cms.data.CmsSiteRepository;
import sparkle.cms.domain.CmsAsset;
import sparkle.cms.domain.CmsSite;
import sparkle.cms.domain.CmsUser;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * AssetManager
 * Created by bazzoni on 04/05/2015.
 */
@Component
@ConfigurationProperties(prefix = "asset")
public class AssetManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private Map<String, String> repo;
    private int timeout;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;
    @Autowired
    private CmsAssetRepository cmsAssetRepository;
    private FedoraRepository fedoraRepository;

    public Map<String, String> getRepo() {
        return repo;
    }

    public void setRepo(Map<String, String> repo) {
        this.repo = repo;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @PostConstruct
    public void initialize() {
        logger.debug(String.format("Repository service: %s, timeout: %s", repo, timeout));
        String repositoryURL = String.format("http://%s:%s/rest/", repo.get("dns"), repo.get("port"));
        fedoraRepository = new FedoraRepositoryImpl(repositoryURL);
        try {
            logger.info(String.format("RepositoryObjectCount: %d", fedoraRepository.getRepositoryObjectCount()));
        } catch (FedoraException e) {
            logger.error(String.format("Cannot query repository: %s", e.toString()));
        }
    }

    public Page<CmsAsset> findAllAssets(CmsUser cmsUser, Pageable pageable) {
        List<CmsSite> cmsSites = cmsSiteRepository.findByWebMaster(cmsUser);
        List<CmsAsset> cmsAssets = new ArrayList<>();
        for (CmsSite cmsSite : cmsSites) {
            cmsAssets.addAll(cmsAssetRepository.findBySiteId(cmsSite.getId()));
        }
        return new PageImpl<>(cmsAssets, pageable, cmsAssets.size());
    }

    public Page<CmsAsset> findAuthoredAssets(CmsUser cmsUser, Pageable pageable) {
        CmsSite cmsSite = cmsSiteRepository.findOne(cmsUser.getAuthoredSiteId());
        return cmsAssetRepository.findBySiteId(cmsSite.getId(), pageable);
    }

    public void createSiteRepository(String siteId) {
        try {
            FedoraObject fedoraObject = fedoraRepository.createObject(siteId);
            logger.info(String.format("Object created %s", fedoraObject.toString()));
            Iterator<Triple> properties = fedoraObject.getProperties();
            logger.debug("Object properties:");
            while (properties.hasNext()) {
                Triple triple = properties.next();
                logger.debug(String.format("%s", triple));
            }
        } catch (FedoraException e) {
            logger.error(String.format("Cannot create repository %s: %s", siteId, e.toString()));
        }
    }

    public void deleteSiteRepository(String siteId) {
        try {
            FedoraObject fedoraObject = fedoraRepository.findOrCreateObject(siteId);
            fedoraObject.delete();
            logger.info(String.format("Object delete %s", fedoraObject.toString()));
        } catch (FedoraException e) {
            logger.error(String.format("Cannot delete repository %s: %s", siteId, e.toString()));
        }
    }
}
