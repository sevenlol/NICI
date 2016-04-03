package tw.gov.ey.nici.network;

import java.util.List;

import tw.gov.ey.nici.models.NiciInfo;

public interface NiciClient {
    /* other methods */
    void clearCache();

    /* NiciInfo */
    int getNiciInfoCount();
    List<NiciInfo> getNiciInfo(int skip, int limit);
}
