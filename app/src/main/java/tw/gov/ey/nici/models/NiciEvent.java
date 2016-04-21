package tw.gov.ey.nici.models;

import java.util.Date;
import java.util.List;

public class NiciEvent {
    private String id;
    private String title;
    private Date date;
    private String location;
    private String description;
    private String minutesTaker;
    private String coverImageUrl;
    private String eventMinutesUrl;
    private List<NiciContent> eventContentList;
    private List<RelatedFile> relatedFileList;


    public NiciEvent() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public Date getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getMinutesTaker() { return minutesTaker; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getEventMinutesUrl() { return eventMinutesUrl; }
    public List<NiciContent> getEventContentList() { return eventContentList; }
    public List<RelatedFile> getRelatedFileList() { return relatedFileList; }

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

    public NiciEvent setMinutesTaker(String minutesTaker) {
        this.minutesTaker = minutesTaker; return this;
    }

    public NiciEvent setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl; return this;
    }

    public NiciEvent setEventMinutesUrl(String eventMinutesUrl) {
        this.eventMinutesUrl = eventMinutesUrl; return this;
    }

    public NiciEvent setEventContentList(List<NiciContent> eventContentList) {
        this.eventContentList = eventContentList; return this;
    }

    public NiciEvent setRelatedFileList(List<RelatedFile> relatedFileList) {
        this.relatedFileList = relatedFileList; return this;
    }

    public static class RelatedFile {
        String fileTitle;
        String fileUrl;
        String fileLabel;
    }
}
