package com.global.tech.space.cechurchadmin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExistanceInfo<T> extends Info<T>
{
    @SerializedName("first_name")
    @Expose
    public boolean firstName;

    @SerializedName("surname")
    @Expose
    public boolean surname;

    @SerializedName("other_names")
    @Expose
    public boolean otherNames;

    @SerializedName("phone_number")
    @Expose
    public boolean phoneNumber;

    @SerializedName("existence")
    @Expose
    public boolean existence;

    public final static ExistanceInfo creator = new ExistanceInfo();

    public ExistanceInfo()
    {
        super((Class<T>) ExistanceInfo.class);
    }

    public ExistanceInfo(Class<T> registeredMemberClass) {
        super(registeredMemberClass);
    }

//    public boolean isExistence() {
//        if ( phoneNumber != null && phoneNumber )
//            return true;
//        else
//            return ( firstName != null && firstName ) && ( surname != null && surname ) && ( otherNames != null && otherNames );
//    }

    public boolean isExistence() {
        return existence;
    }
}
