package ms.cms.authoring.service.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.net.UnknownHostException;

/**
 * MongoConfig
 * Created by thebaz on 27/03/15.
 */
@Configuration
@EnableMongoRepositories({"ms.cms.data", "ms.cms.domain"})
public class MongoConfig extends AbstractMongoConfiguration {
    @Value("${spring.data.mongodb.name}")
    private String databaseName;
    @Value("${spring.data.mongodb.host}")
    private String hostName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public Mongo mongo() throws UnknownHostException {
        MongoClient client = new MongoClient(hostName);
        client.setWriteConcern(WriteConcern.SAFE);
        return client;
    }

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {
        return new MongoTemplate(mongo(), getDatabaseName());
    }
}
