package tw.gov.ey.nici.events;

public class IntroDataRequestEvent {
    private String id;

    public IntroDataRequestEvent(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
