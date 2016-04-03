package tw.gov.ey.nici.events;

public class InfoDataErrorEvent {
    private String id;

    public InfoDataErrorEvent(String id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
