package tw.gov.ey.nici.events;

import tw.gov.ey.nici.models.NiciProject;

public class ProjectDataReadyEvent {
    private NiciProject project;

    public ProjectDataReadyEvent(NiciProject project) {
        if (project == null || project.getContentList() == null) {
            throw new IllegalArgumentException();
        }
        this.project = project;
    }

    public NiciProject getProject() { return project; }
}
