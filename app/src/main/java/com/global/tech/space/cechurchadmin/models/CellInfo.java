package com.global.tech.space.cechurchadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CellInfo extends Info<CellInfo>
{
    @SerializedName("id")
    @Expose
    public Integer id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("leader_name")
    @Expose
    public String leaderName;

    @SerializedName("assistant_name")
    @Expose
    public String assistantName;

    public final static CellInfo creator = new CellInfo();

    public CellInfo()
    {
        super(CellInfo.class);
    }
}
