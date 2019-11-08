package com.global.tech.space.cechurchadmin.models;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CellWeekInfo extends Info<CellWeekInfo> implements Searchable
{
    @SerializedName("week")
    @Expose
    public Integer week;

    @SerializedName("meetings_held")
    @Expose
    public Integer meetingsHeld;

    public final static CellWeekInfo creator = new CellWeekInfo();

    public CellWeekInfo()
    {
        super( CellWeekInfo.class );
    }

    public CellWeekInfo(int week, int meetingsHeld) {
        this();
        this.week = week;
        this.meetingsHeld = meetingsHeld;
    }

    @Override
    public boolean search(String query) {
        try
        {
            int week = Integer.parseInt(query);
            return this.week == week;
        }
        catch ( NumberFormatException ex )
        {
            return false;
        }

    }

//    public static CellWeekInfo fromGson(String service )
//    {
//        Gson gson = GsonConverter.getGson();
//        return gson.fromJson( service, CellWeekInfo.class );
//    }
}
