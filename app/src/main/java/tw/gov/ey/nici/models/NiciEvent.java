package tw.gov.ey.nici.models;

import java.util.Date;

public class NiciEvent {
    private String id;
    private String title;
    private Date date;
    private String location;
    private String description;

    public NiciEvent() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }

    public NiciEvent setId(String id) {
        this.id = id; return this;
    }

    public NiciEvent setTitle(String title) {
        this.title = title; return this;
    }

    public NiciEvent setDate(Date date) {
        this.date = date; return this;
    }

    public NiciEvent setLocation(String location) {
        this.location = location; return this;
    }

    public NiciEvent setDescription(String description) {
        this.description = description; return this;
    }
}
