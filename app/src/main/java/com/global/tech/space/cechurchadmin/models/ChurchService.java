package com.global.tech.space.cechurchadmin.models;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import static com.global.tech.space.cechurchadmin.models.States.*;

//public class ChurchService implements Parcelable
public class ChurchService extends Model< ChurchService > implements Searchable
{
    @SerializedName("church_id")
    @Expose
    public int churchId;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("date")
    @Expose
    public LocalDate date;

    @SerializedName("type")
    @Expose
    public String type;

    @SerializedName("attendance")
    @Expose
    public int attendance;

    @SerializedName("offering")
    @Expose
    public int offering;

    @SerializedName("status")
    @Expose
    public Status status;

    @SerializedName("start_time")
    @Expose
    public LocalTime startTime;

    @SerializedName("end_time")
    @Expose
    public LocalTime endTime;

    @SerializedName("description")
    @Expose
    public String description;

    public final static ChurchService creator = new ChurchService();

    public ChurchService()
    {
        super( ChurchService.class );
        name = DateTime.now().dayOfWeek().getAsText() + " Church Service";
        description = DateTime.now().dayOfWeek().getAsText() + " Church Service";
        type = getType();
        date = LocalDate.now();
        startTime = LocalTime.now();
        attendance = 0;
        status = Status.ONGOING;
    }

    public List<Attendee> attends(List< Member > members )
    {
        ArrayList<Attendee> attendees = new ArrayList<>();
        for ( Member member : members )
            attendees.add( attends( member ) );
        return attendees;
    }

    public Attendee attends(Member member )
    {
        Attendee attendee = new Attendee( this, member );
        return attendee;
    }

    public String getType()
    {
        if  ( DateTime.now().dayOfWeek().getAsText().equalsIgnoreCase("sunday" ) )
            return "SUNDAY";
        return "MIDWEEK";

    }

    @Override
    public boolean search(String query) {
        return ( name != null && name.toLowerCase().contains( query ) )
                || ( description != null && description.toLowerCase().contains( query ) );
    }

//    public static ChurchService fromGson(String service )
//    {
//        Gson gson = GsonConverter.getGson();
//        return gson.fromJson( service, ChurchService.class );
//    }

}
