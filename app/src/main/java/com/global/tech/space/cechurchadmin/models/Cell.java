package com.global.tech.space.cechurchadmin.models;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Cell extends Model<Cell> implements Searchable
{
    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("leader_id")
    @Expose
    public Integer leaderId;

    @SerializedName("assistant_id")
    @Expose
    public Integer assistantId;

    @SerializedName("membership_strength")
    @Expose
    public int membershipStrength;

    @SerializedName("subject")
    @Expose
    public String subject;

    @SerializedName("venue")
    @Expose
    public String venue;

    @SerializedName("leader")
    @Expose
    public Member leader;

    @SerializedName("assistant")
    @Expose
    public Member assistant;

    public final static Cell creator = new Cell();

    public Cell()
    {
        super(Cell.class);
    }

    public Cell(String name, Integer leaderId, Integer assistantId, String venue, String subject ) {
        this();
        this.name = name;
        this.leaderId = leaderId;
        this.assistantId = assistantId;
        this.venue = venue;
        this.subject = subject;
    }

    @Override
    public boolean search(String query) {
        return ( name != null && name.toLowerCase().contains( query ) )
                || ( leader != null && leader.search( query ) )
                || ( assistant != null && assistant.search( query ) );
    }

//    public static Cell fromGson(String service )
//    {
//        Gson gson = GsonConverter.getGson();
//        return gson.fromJson( service, Cell.class );
//    }
}
