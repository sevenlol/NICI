package tw.gov.ey.nici.events;

public class MeetingInfoDataErrorEvent {
    private String id;

    public MeetingInfoDataErrorEvent(String id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        this.id = id;
    }

    public String getId() { return id; }
}
