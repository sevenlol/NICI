package tw.gov.ey.nici.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NiciService {
    @GET("app/apis/about.aspx?Mode=Team")
    Call<ResponseBody> getIntro();

    @GET("app/apis/about.aspx?Mode=Project")
    Call<ResponseBody> getProject();

    @GET("app/apis/meeting.aspx?Mode=List&Type=Log")
    Call<ResponseBody> getMeetingList(
            @Query("PageIndex") int pageIndex,
            @Query("PageSize") int pageLimit);

    @GET("app/apis/meeting.aspx?Mode=Content&Type=Log")
    Call<ResponseBody> getMeetingById(@Query("Id") String id);

    @GET("app/apis/meeting.aspx?Mode=List&Type=News")
    Call<ResponseBody> getMeetingInfoList(
            @Query("PageIndex") int pageIndex,
            @Query("PageSize") int pageLimit);

    @GET("app/apis/meeting.aspx?Mode=Content&Type=News")
    Call<ResponseBody> getMeetingInfoById(@Query("Id") String id);

    @GET("app/apis/info.aspx")
    Call<ResponseBody> getInfoList(
            @Query("PageIndex") int pageIndex,
            @Query("PageSize") int pageLimit);
}
