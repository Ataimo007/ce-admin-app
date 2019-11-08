package com.global.tech.space.cechurchadmin.models;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.lang.reflect.Array;

public class Model< T extends Model > {

    private Class< T > type;

    @SerializedName("id")
    @Expose
    public int id;

    @SerializedName("created_at")
    @Expose
    public DateTime created_at;

    @SerializedName("updated_at")
    @Expose
    public DateTime updated_at;

    public Model( Class<T> tClass )
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

    public T[] fromGsonArray( String[] raw )
    {
        Gson gson = GsonConverter.getGson();
        T[] models = (T[]) Array.newInstance(type, raw.length);
        for ( int i = 0; i < raw.length; ++i )
        {
            models[ i ] = gson.fromJson( raw[ i ], type );
        }
        return models;
    }

    public String[] toGsonArray( T[] model )
    {
        Gson gson = GsonConverter.getGson();
        String[] raw = new String[model.length];
        for ( int i = 0; i < raw.length; ++i )
        {
            raw[ i ] = model[ i ].toGson();
        }
        return raw;
    }


}
