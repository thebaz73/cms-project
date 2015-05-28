package sparkle.cms.authoring.ui.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

/**
 * SolrConfig
 * Created by bazzoni on 26/05/2015.
 */
@Configuration
@EnableSolrRepositories(basePackages = {"sparkle.cms.data", "sparkle.cms.domain"}, multicoreSupport = true)
public class SolrConfig {
    @Value("${solr.host}")
    private String solrHostName;

    @Bean
    public SolrServer solrServer() {
        return new HttpSolrServer(solrHostName);
    }
}
