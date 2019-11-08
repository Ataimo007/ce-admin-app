package com.global.tech.space.cechurchadmin;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.CellAttendee;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.CellWeekInfo;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.global.tech.space.cechurchadmin.models.ExistanceInfo;
import com.global.tech.space.cechurchadmin.models.ModelResponse;
import com.global.tech.space.cechurchadmin.models.RegisteredMember;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class CEService
{
    private static CEService ceService;
    private static final String host = "http://192.168.43.69:8000/";
//    private static final String host = "http://192.168.43.69:8000/";

    private final CEApi ceApi;

    private CEService()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel( HttpLoggingInterceptor.Level.BODY );
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout( 5, TimeUnit.MINUTES )
                .retryOnConnectionFailure( true )
                .readTimeout( 60, TimeUnit.SECONDS )
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( host )
                .client( client )
                .addConverterFactory(GsonConverterFactory.create(GsonConverter.getGson()))
                .build();

        ceApi = retrofit.create(CEApi.class);
    }

    public static void main(String[] args) {
//        DateTime.now().toString("")
        System.out.println( "Time " + DateTime.now().toString( "yyyy-MM-dd HH:mm:ss") );
        System.out.println( "Date " + LocalDate.now());
        System.out.println( "Time " + LocalTime.now().toString( "HH:mm:ss"));
    }

    public static CEService getInstance()
    {
        if ( ceService == null )
            ceService = new CEService();
        return ceService;
    }

    public CEApi getCeApi() {
        return ceApi;
    }


    public interface CEApi
    {
        @GET("api/members/getAll")
        Call<List<Member>> getMembers();

        @GET("api/members/no_cell")
        Call<List<Member>> getMembersNotInCell();

        @GET("api/service/getAttendees")
        Call<List<Attendee>> serviceAttendees(@Query("service_id") int serviceId);

        @GET("api/cell_meeting/get_attendees")
        Call<List<CellAttendee>> cellMeetingAttendees(@Query("cell_meeting_id") int cellMeetingId);

        @GET("api/service/getRecentAttendees")
        Call<List<Attendee>> serviceRecentAttendees(@Query("recent") int recent, @Query("service_id") int serviceId);

        @GET("api/cell_meeting/getRecentAttendees")
        Call<List<CellAttendee>> CellMeetingRecentAttendees(@Query("recent") int recent, @Query("cell_meeting_id") int serviceId);

        @GET("api/service/getAbsentees")
        Call<List<Member>> serviceAbsentees(@Query("service_id") int serviceId);

        @GET("api/cell_meeting/getAbsentees")
        Call<List<Member>> cellMeetingsAbsentees(@Query("cell_id") int serviceId, @Query("cell_meeting_id") int cellMeetingId);

        @GET("api/cell/members/{cell}")
        Call<List<Member>> getCellMembers(@Path("cell") int cellId);

        @GET("api/service/getAll")
        Call<List<ChurchService>> getServices();


        // 'name', 'date', 'type', 'start_time'
//        @FormUrlEncoded
//        @POST("api/service/beginParams")
//        Call<List<Member>> beginService( @Field("name") String name, @Field("date") String date,
//                                         @Field("type") String type );


        @POST("api/service/begin")
        Call<ChurchService> beginService( @Body ChurchService churchService);

        @POST("api/cell_meeting/begin")
        Call<CellMeeting> beginCellMeeting(@Body CellMeeting churchService);

        @POST("api/member/register")
        Call<RegisteredMember> memberRegister(@Body Member member );

        @POST("api/member/update")
        Call<ModelResponse<Member>> memberUpdate(@Body Member member );

//        @POST("api/member/edit")
//        Call<Member> editMember(Member member);

        @POST("api/cell/register")
        Call<Cell> cellRegister( @Body Cell cell );

        @POST("api/service/attends")
        Call<Integer> attendsService(@Body List<Attendee> members );

        @POST("api/service/attend")
        Call<List<Attendee>> attendService(@Body List<Attendee> members );

        @POST("api/cell_meeting/attends")
        Call<Integer> attendsCellMeeting(@Body List<CellAttendee> members);

        @POST("api/members/update_cell")
        Call<Integer> addCellMembers(@Body List<Member> members );

        @POST("api/member/update_cell")
        Call<Integer> addCellMembers(@Body Member member );

        @GET("api/cell/getAll")
        Call<List<Cell>> getCells();

        @GET("api/cell_meeting/get_all_week")
        Call<List<CellWeekInfo>> getCellMeetingsWeek();

        @GET("api/cell_meeting/get_by_week")
        Call<List<CellMeeting>> getCellMeetingsOfWeek(@Query("week") int week);

        @GET("api/cell_meeting/cell/{cell}")
        Call<List<CellMeeting>> getCellMeetings(@Path("cell") int cellId);

        @POST("api/service/close")
        Call<ChurchService> closeService( @Body ChurchService churchService );

        @POST("api/cell_meeting/close")
        Call<CellMeeting> closeCellMeeting( @Body CellMeeting cellMeeting );

        @POST("api/service/update_attendance")
        Call<ChurchService> updateAttendance( @Body ChurchService churchService );

        @POST("api/cell_meeting/update_attendance")
        Call<CellMeeting> updateCellAttendance( @Body CellMeeting churchService );

        @GET("api/member/get/{member}")
        Call<Member> getMember( @Path("member") int id );

        @GET("api/cell/get/{cell}")
        Call<Cell> getCell( @Path("cell") int id );

        @GET("api/members/search/name")
        Call<ArrayList<Member>> getMember( @Query("name") String name);

        @GET("api/members/search/exist")
        Call<ExistanceInfo> isMemberExisting(@Query("first_name") String first, @Query("surname") String sur,
                                             @Query("other_names") String other, @Query("phone_number") String number );
    }


}


