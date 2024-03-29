package com.global.tech.space.cechurchadmin.helpers;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonHelpers
{
    public static String getString(JsonObject object, String name)
    {
        JsonElement jsonElement = object.get(name);
        if ( !jsonElement.isJsonNull() )
            return jsonElement.getAsJsonPrimitive().getAsString();
        return "";
    }

    public static long getInteger(JsonObject object, String name, int defaultValue )
    {
        JsonElement jsonElement = object.get(name);
        if ( !jsonElement.isJsonNull() )
            return jsonElement.getAsJsonPrimitive().getAsLong();
        return defaultValue;
    }

    public static long getLong(JsonObject object, String name, long defaultValue)
    {
        JsonElement jsonElement = object.get(name);
        if ( !jsonElement.isJsonNull() )
            return jsonElement.getAsJsonPrimitive().getAsLong();
        return defaultValue;
    }

    public static long getLong(JsonObject object, String name)
    {
        return getLong( object, name, -1 );
    }
}
