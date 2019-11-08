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
public class CellMeeting extends Model< CellMeeting > implements Searchable
{
    @SerializedName("church_id")
    @Expose
    public int churchId;

    @SerializedName("cell_id")
    @Expose
    public int cellId;

    @SerializedName("cell")
    @Expose
    public Cell cell;

    @SerializedName("week")
    @Expose
    public int week;

    @SerializedName("year")
    @Expose
    public int year;


    @SerializedName("date")
    @Expose
    public LocalDate date;

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

    public final static CellMeeting creator = new CellMeeting();

    public CellMeeting()
    {
        super(CellMeeting.class);
    }

    public CellMeeting( int cellId )
    {
        this();
        this.cellId = cellId;
    }

    public List<CellAttendee> attends(List< Member > members )
    {
        ArrayList<CellAttendee> attendees = new ArrayList<>();
        for ( Member member : members )
            attendees.add( attends( member ) );
        return attendees;
    }

    public CellAttendee attends(Member member )
    {
        CellAttendee attendee = new CellAttendee( this, member );
        return attendee;
    }

    @Override
    public boolean search(String query) {
        return ( cell != null && cell.search( query ) )
                || ( description != null && description.toLowerCase().contains( query ) );
    }

//    public static CellMeeting fromGson(String service )
//    {
//        Gson gson = GsonConverter.getGson();
//        return gson.fromJson( service, CellMeeting.class );
//    }

}
