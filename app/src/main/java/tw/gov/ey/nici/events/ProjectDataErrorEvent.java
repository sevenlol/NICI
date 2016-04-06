package tw.gov.ey.nici.events;

public class ProjectDataErrorEvent {
    private String id;

    public ProjectDataErrorEvent(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
