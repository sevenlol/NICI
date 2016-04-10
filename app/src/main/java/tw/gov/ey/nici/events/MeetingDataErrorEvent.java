package tw.gov.ey.nici.events;

public class MeetingDataErrorEvent {
    private String id;

    public MeetingDataErrorEvent(String id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
