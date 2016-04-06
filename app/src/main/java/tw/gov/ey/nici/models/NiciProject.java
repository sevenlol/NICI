package tw.gov.ey.nici.models;

import java.util.List;

public class NiciProject {
    private List<NiciContent> contentList;
    private String projectFileUrl;

    public NiciProject() {}

    /* getter methods */

    public List<NiciContent> getContentList() { return contentList; }
    public String getProjectFileUrl() { return projectFileUrl; }

    /* setter methods */

    public NiciProject setContentList(List<NiciContent> contentList) {
        this.contentList = contentList; return this;
    }

    public NiciProject setProjectFileUrl(String projectFileUrl) {
        this.projectFileUrl = projectFileUrl; return this;
    }
}
