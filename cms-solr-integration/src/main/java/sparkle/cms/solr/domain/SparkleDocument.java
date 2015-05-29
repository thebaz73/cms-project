package sparkle.cms.solr.domain;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * SparkleDocument
 * Created by bazzoni on 28/05/2015.
 */
@SolrDocument(solrCoreName = "sparkleDocs")
public class SparkleDocument {
    public static final String FIELD_CONTENT = "content";
    public static final String FIELD_SUMMARY = "summary";
    public static final String FIELD_ID = "id";
    public static final String FIELD_TITLE = "title";

    @Id
    @Field
    private String id;

    @Field
    private String content;

    @Field
    private String summary;

    @Field
    private String title;

    public SparkleDocument() {
    }

    public static Builder getBuilder(String id, String title) {
        return new Builder(id, title);
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "SparkleDocument{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", summary='" + summary + '\'' +
                ", title='" + title + '\'' +
                '}';
    }

    public static class Builder {
        private SparkleDocument build;

        public Builder(String id, String title) {
            build = new SparkleDocument();
            build.id = id;
            build.title = title;
        }

        public Builder content(String content) {
            build.content = content;
            return this;
        }

        public Builder summary(String summary) {
            build.summary = summary;
            return this;
        }

        public SparkleDocument build() {
            return build;
        }
    }
}
