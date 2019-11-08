package com.global.tech.space.cechurchadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalTime;

public class CellAttendee extends Model<CellAttendee>
{
    @SerializedName("member_id")
    @Expose
    public int memberId;

    @SerializedName("cell_meeting_id")
    @Expose
    public int cellMeetingId;

    @SerializedName("entry_time")
    @Expose
    public LocalTime entryTime;

    @SerializedName("member")
    @Expose
    public Member member;

    public final static CellAttendee creator = new CellAttendee();

    public CellAttendee()
    {
        super(CellAttendee.class);
    }

    public CellAttendee(int memberId, int cellMeetingId, LocalTime entryTime) {
        this();
        this.memberId = memberId;
        this.cellMeetingId = cellMeetingId;
        this.entryTime = entryTime;
    }

    public CellAttendee(CellMeeting cellMeeting, Member member) {
        this( member.id, cellMeeting.id, LocalTime.now() );
    }
}
