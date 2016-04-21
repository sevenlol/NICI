package tw.gov.ey.nici.events;

public class IntroDataErrorEvent {
    private String id;

    public IntroDataErrorEvent(String id) {
        if (id == null || id.equals("")) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
