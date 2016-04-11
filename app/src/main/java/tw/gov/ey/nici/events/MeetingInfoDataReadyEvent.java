package tw.gov.ey.nici.events;

import java.util.List;

import tw.gov.ey.nici.models.NiciEventInfo;

public class MeetingInfoDataReadyEvent {
    private int skip;
    private int total;
    private List<NiciEventInfo> eventInfoList;

    public MeetingInfoDataReadyEvent(int skip, int total, List<NiciEventInfo> eventInfoList) {
        if (skip < 0 || total < 0 || eventInfoList == null) {
            throw new IllegalArgumentException();
        }
        this.skip = skip;
        this.total = total;
        this.eventInfoList = eventInfoList;
    }

    public int getSkip() { return skip; }
    public int getTotal() { return total; }
    public List<NiciEventInfo> getEventInfoList() { return eventInfoList; }
}
