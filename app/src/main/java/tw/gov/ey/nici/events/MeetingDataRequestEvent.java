package tw.gov.ey.nici.events;

public class MeetingDataRequestEvent {
    private String id;
    private int count;

    public MeetingDataRequestEvent(String id, int count) {
        if (id == null || count <= 0) {
            throw new IllegalArgumentException();
        }
        this.id = id;
        this.count = count;
    }

    public String getId() { return id; }
    public int getCount() { return count; }
}
