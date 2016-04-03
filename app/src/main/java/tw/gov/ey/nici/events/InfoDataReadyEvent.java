package tw.gov.ey.nici.events;

import java.util.List;

import tw.gov.ey.nici.models.NiciInfo;

public class InfoDataReadyEvent {
    private int skip;
    private int total;
    private List<NiciInfo> infoList;

    public InfoDataReadyEvent(int skip, int total, List<NiciInfo> infoList) {
        if (skip < 0 || total < 0 || infoList == null) {
            throw new IllegalArgumentException();
        }
        this.skip = skip;
        this.total = total;
        this.infoList = infoList;
    }

    public int getSkip() { return skip; }
    public int getTotal() { return total; }
    public List<NiciInfo> getInfoList() { return infoList; }
}
