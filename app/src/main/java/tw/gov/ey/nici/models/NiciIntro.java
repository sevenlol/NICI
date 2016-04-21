package tw.gov.ey.nici.models;

import java.util.List;

public class NiciIntro {
    private List<NiciContent> contentList;
    private String videoId;
    private Integer videoLocationIndex;

    public NiciIntro() {}

    public List<NiciContent> getContentList() { return contentList; }
    public String getVideoId() { return videoId; }
    public Integer getVideoLocationIndex() { return videoLocationIndex; }

    public NiciIntro setContentList(List<NiciContent> contentList) {
        this.contentList = contentList; return this;
    }

    public NiciIntro setVideoId(String videoId) {
        this.videoId = videoId; return this;
    }

    public NiciIntro setVideoLocationIndex(Integer videoLocationIndex) {
        this.videoLocationIndex = videoLocationIndex; return this;
    }
}
