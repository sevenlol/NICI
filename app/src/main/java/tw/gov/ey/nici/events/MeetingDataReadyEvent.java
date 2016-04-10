package tw.gov.ey.nici.events;

import java.util.List;

import tw.gov.ey.nici.models.NiciEvent;

public class MeetingDataReadyEvent {
    private int skip;
    private int total;
    private List<NiciEvent> eventList;

    public MeetingDataReadyEvent(int skip, int total, List<NiciEvent> eventList) {
        if (skip < 0 || total < 0 || eventList == null) {
            throw new IllegalArgumentException();
        }
        this.skip = skip;
        this.total = total;
        this.eventList = eventList;
    }

    public int getSkip() { return skip; }
    public int getTotal() { return total; }
    public List<NiciEvent> getEventList() { return eventList; }
}
