package tw.gov.ey.nici.models;

import java.util.Date;

public class NiciEventInfo {
    private String id;
    private String title;
    private Date date;
    private String location;
    private String description;

    public NiciEventInfo() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }

    public NiciEventInfo setId(String id) {
        this.id = id; return this;
    }

    public NiciEventInfo setTitle(String title) {
        this.title = title; return this;
    }

    public NiciEventInfo setDate(Date date) {
        this.date = date; return this;
    }

    public NiciEventInfo setLocation(String location) {
        this.location = location; return this;
    }

    public NiciEventInfo setDescription(String description) {
        this.description = description; return this;
    }
}
