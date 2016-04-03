package tw.gov.ey.nici.models;

import java.util.Date;

public class NiciInfo {
    private String title;
    private Date date;
    private String location;
    private String description;
    private String linkUrl;

    public NiciInfo() {}

    /* getter methods */

    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getLinkUrl() { return linkUrl; }

    /* setter methods */

    public NiciInfo setTitle(String title) {
        this.title = title; return this;
    }

    public NiciInfo setDate(Date date) {
        this.date = date; return this;
    }

    public NiciInfo setLocation(String location) {
        this.location = location; return this;
    }

    public NiciInfo setDescription(String description) {
        this.description = description; return this;
    }

    public NiciInfo setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl; return this;
    }
}
