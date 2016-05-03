package tw.gov.ey.nici.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import tw.gov.ey.nici.models.*;
import tw.gov.ey.nici.utils.PaginationUtil;

import java.util.List;
import java.util.Map;

// TODO abstract parser class and use a common method for handling request
public class NiciClientImpl implements NiciClient {

    private NiciService service = null;

    private Integer eventCount = null;
    private Integer eventInfoCount = null;
    private Integer infoCount = null;

    public NiciClientImpl(String baseUrl) {
        if (baseUrl == null || baseUrl.equals("")) {
            throw new IllegalArgumentException();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .build();
        service = retrofit.create(NiciService.class);
    }

    @Override
    public void clearCache() {
        // clear cached counts
        eventCount = null;
        eventInfoCount = null;
        infoCount = null;
    }

    @Override
    public NiciIntro getNiciIntro() {
        checkService();

        Call<ResponseBody> call = service.getIntro();
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                throw new NiciRequestFailedException();
            }
            return NiciIntro.Parser.parse(response.getData());
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    @Override
    public NiciProject getNiciProject() {
        checkService();

        Call<ResponseBody> call = service.getProject();
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                // TODO add log
                throw new NiciRequestFailedException();
            }
            return NiciProject.Parser.parse(response.getData());
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    /*
     * must be called after getNiciEvent
     * TODO probably change this, but im lazy
     */
    @Override
    public int getNiciEventCount() {
        return eventCount;
    }

    /*
     * skip must be divisible by limit
     */
    @Override
    public List<NiciEvent> getNiciEvent(int skip, int limit) {
        // might generate exception
        // skip has to be divisible by limit atm
        int pageIndex = PaginationUtil.getPageIndex(skip, limit);

        checkService();

        Call<ResponseBody> call = service.getMeetingList(pageIndex, limit);
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                // TODO add log
                throw new NiciRequestFailedException();
            }
            Map.Entry<Integer, List<NiciEvent>> entry =  NiciEvent.ListParser.parse(response.getData());
            if (entry.getKey() != null) {
                eventCount = entry.getKey();
            }
            return entry.getValue();
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    @Override
    public NiciEvent getNiciEventById(String eventId) {
        checkService();
        if (eventId == null || eventId.equals("")) {
            throw new IllegalArgumentException();
        }

        Call<ResponseBody> call = service.getMeetingById(eventId);
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                // TODO add log
                throw new NiciRequestFailedException();
            }
            return NiciEvent.Parser.parse(response.getData());
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    /*
     * must be called after getNiciEventInfo
     * TODO probably change this, but im lazy
     */
    @Override
    public int getNiciEventInfoCount() {
        return eventInfoCount;
    }

    /*
     * skip must be divisible by limit
     */
    @Override
    public List<NiciEventInfo> getNiciEventInfo(int skip, int limit) {
        // might generate exception
        // skip has to be divisible by limit atm
        int pageIndex = PaginationUtil.getPageIndex(skip, limit);

        checkService();

        Call<ResponseBody> call = service.getMeetingInfoList(pageIndex, limit);
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                // TODO add log
                throw new NiciRequestFailedException();
            }
            Map.Entry<Integer, List<NiciEventInfo>> entry =  NiciEventInfo.ListParser.parse(response.getData());
            if (entry.getKey() != null) {
                eventInfoCount = entry.getKey();
            }
            return entry.getValue();
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    @Override
    public NiciEventInfo getNiciEventInfoById(String eventInfoId) {
        checkService();
        if (eventInfoId == null || eventInfoId.equals("")) {
            throw new IllegalArgumentException();
        }

        Call<ResponseBody> call = service.getMeetingInfoById(eventInfoId);
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                // TODO add log
                throw new NiciRequestFailedException();
            }
            return NiciEventInfo.Parser.parse(response.getData());
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    /*
     * must be called after getNiciInfo
     * TODO probably change this, but im lazy
     */
    @Override
    public int getNiciInfoCount() {
        return infoCount;
    }

    /*
     * skip must be divisible by limit
     */
    @Override
    public List<NiciInfo> getNiciInfo(int skip, int limit) {
        // might generate exception
        // skip has to be divisible by limit atm
        int pageIndex = PaginationUtil.getPageIndex(skip, limit);

        checkService();

        Call<ResponseBody> call = service.getInfoList(pageIndex, limit);
        try {
            NiciResponse response = NiciResponse.Parser.parse(call.execute());
            if (response == null || !response.getIsSuccess()) {
                // request failed throw exception
                // TODO add log
                throw new NiciRequestFailedException();
            }
            Map.Entry<Integer, List<NiciInfo>> entry =  NiciInfo.ListParser.parse(response.getData());
            if (entry.getKey() != null) {
                infoCount = entry.getKey();
            }
            return entry.getValue();
        } catch (Exception e) {
            // TODO add log
            throw new NiciRequestFailedException();
        }
    }

    private void checkService() {
        if (service == null) {
            throw new RuntimeException();
        }
    }
}
