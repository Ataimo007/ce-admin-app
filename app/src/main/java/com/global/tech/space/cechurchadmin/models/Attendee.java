package com.global.tech.space.cechurchadmin.models;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalTime;

public class Attendee extends Model<Attendee>
{
    @SerializedName("member_id")
    @Expose
    public int memberId;

    @SerializedName("service_id")
    @Expose
    public int serviceId;

    @SerializedName("entry_time")
    @Expose
    public LocalTime entryTime;

    @SerializedName("member")
    @Expose
    public Member member;

    public final static Attendee creator = new Attendee();

    public Attendee()
    {
        super(Attendee.class);
    }

    public Attendee(int memberId, int serviceId, LocalTime entryTime) {
        this();
        this.memberId = memberId;
        this.serviceId = serviceId;
        this.entryTime = entryTime;
    }

    public Attendee(ChurchService service, Member member) {
        this( member.id, service.id, LocalTime.now() );
    }

//    public static Attendee fromGson(String attend) {
//        Gson gson = GsonConverter.getGson();
//        return gson.fromJson( attend, Attendee.class );
//    }
}
