package tw.gov.ey.nici.models;

import java.util.List;

public class NiciMeetingDetail {
    private NiciEvent event;
    private List<NiciContent> contentList;

    public NiciMeetingDetail() {}

    public NiciEvent getEvent() { return event; }
    public List<NiciContent> getContentList() { return contentList; }

    public NiciMeetingDetail setEvent(NiciEvent event) {
        this.event = event; return this;
    }

    public NiciMeetingDetail setContentList(List<NiciContent> contentList) {
        this.contentList = contentList; return this;
    }
}
