package sparkle.cms.authentication.service.config;

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
 * Created by thebaz on 23/03/15.
 */
@Configuration
@EnableMongoRepositories({"sparkle.cms.data", "sparkle.cms.domain"})
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
