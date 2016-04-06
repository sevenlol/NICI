package tw.gov.ey.nici.events;

public class ProjectDataRequestEvent {
    private String id;

    public ProjectDataRequestEvent(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
