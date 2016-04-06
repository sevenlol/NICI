package tw.gov.ey.nici.network;

import java.util.List;

import tw.gov.ey.nici.models.NiciInfo;
import tw.gov.ey.nici.models.NiciProject;

public interface NiciClient {
    /* other methods */
    void clearCache();

    /* NiciProject */
    NiciProject getNiciProject();

    /* NiciInfo */
    int getNiciInfoCount();
    List<NiciInfo> getNiciInfo(int skip, int limit);
}
