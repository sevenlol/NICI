package tw.gov.ey.nici.network;

import java.util.List;

import tw.gov.ey.nici.models.NiciEvent;
import tw.gov.ey.nici.models.NiciEventInfo;
import tw.gov.ey.nici.models.NiciInfo;
import tw.gov.ey.nici.models.NiciProject;

public interface NiciClient {
    /* other methods */
    void clearCache();

    /* NiciProject */
    NiciProject getNiciProject();

    /* NiciMeeting */
    int getNiciEventCount();
    List<NiciEvent> getNiciEvent(int skip, int limit);
    NiciEvent getNiciEventById(String eventId);

    /* NiciMeetingInfo */
    int getNiciEventInfoCount();
    List<NiciEventInfo> getNiciEventInfo(int skip, int limit);
    NiciEventInfo getNiciEventInfoById(String eventInfoId);

    /* NiciInfo */
    int getNiciInfoCount();
    List<NiciInfo> getNiciInfo(int skip, int limit);
}
