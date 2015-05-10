package sparkle.cms.authoring.ui.domain;

/**
 * AuthoringStatus
 * Created by thebaz on 12/04/15.
 */
public class AuthoringStatus {
    int sitesCount;
    int contentsCount;
    int authorsCount;
    int commentsCount;

    public int getSitesCount() {
        return sitesCount;
    }

    public void setSitesCount(int sitesCount) {
        this.sitesCount = sitesCount;
    }

    public int getContentsCount() {
        return contentsCount;
    }

    public void setContentsCount(int contentsCount) {
        this.contentsCount = contentsCount;
    }

    public int getAuthorsCount() {
        return authorsCount;
    }

    public void setAuthorsCount(int authorsCount) {
        this.authorsCount = authorsCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
}
