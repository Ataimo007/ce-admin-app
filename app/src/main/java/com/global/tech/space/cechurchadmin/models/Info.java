package com.global.tech.space.cechurchadmin.models;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public class Info< T > {

    private Class< T > type;

    public Info( Class<T> tClass )
    {
        type = tClass;
    }

    @Override
    public String toString() {
        return toGson();
    }

    public String toGson()
    {
        Gson gson = GsonConverter.getGson();
        return gson.toJson( this );
    }

    public T fromGson( String raw )
    {
        Gson gson = GsonConverter.getGson();
        return gson.fromJson( raw, type );
    }
}
