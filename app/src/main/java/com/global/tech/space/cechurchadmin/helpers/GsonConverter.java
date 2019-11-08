package com.global.tech.space.cechurchadmin.helpers;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.Type;
import static com.global.tech.space.cechurchadmin.models.States.*;

public class GsonConverter
{

    public final Gson gson;
    private static GsonConverter converter;

    public static Gson getGson()
    {
        if ( converter == null )
        {
            converter = new GsonConverter();
        }
        return converter.gson;
    }

    private GsonConverter()
    {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter( DateTime.class, new DateTimeParsers() );
        builder.registerTypeAdapter( LocalTime.class, new TimeParsers() );
        builder.registerTypeAdapter( LocalDate.class, new DateParsers() );
        builder.registerTypeAdapter( Years.class, new YearsParsers() );
        builder.registerTypeAdapter( Member.DateOfBirth.class, new DOBParsers() );
        builder.registerTypeAdapter( MonthDay.class, new MonthDayParsers() );
        builder.registerTypeAdapter( Status.class, new ServiceStatusParsers() );
        builder.registerTypeAdapter( Member.Gender.class, new GenderParsers() );

        gson = builder.excludeFieldsWithoutExposeAnnotation().create();
    }

    private class DateParsers implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate>
    {
        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse( json.getAsString() );
        }

        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.toString() );
        }
    }

    private class DOBParsers implements JsonSerializer<Member.DateOfBirth>, JsonDeserializer<Member.DateOfBirth>
    {
        @Override
        public Member.DateOfBirth deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            return new Member.DateOfBirth( json.getAsString() );
        }

        @Override
        public JsonElement serialize(Member.DateOfBirth src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.store() );
        }
    }

    private class YearsParsers implements JsonSerializer<Years>, JsonDeserializer<Years>
    {
        @Override
        public Years deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Years.years( json.getAsInt() );
        }

        @Override
        public JsonElement serialize(Years src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.getYears() );
        }
    }

    private class MonthDayParsers implements JsonSerializer<MonthDay>, JsonDeserializer<MonthDay>
    {
        @Override
        public MonthDay deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return MonthDay.parse( json.getAsString(), DateTimeFormat.forPattern("MM-dd") );
        }

        @Override
        public JsonElement serialize(MonthDay src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.toString("MM-dd" ) );
        }
    }

    private class TimeParsers implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime>
    {
        @Override
        public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalTime.parse( json.getAsString(), DateTimeFormat.forPattern("HH:mm:ss") );
        }

        @Override
        public JsonElement serialize(LocalTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.toString( "HH:mm:ss" ) );
        }
    }

    private class DateTimeParsers implements JsonSerializer<DateTime>, JsonDeserializer<DateTime>
    {
        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return DateTime.parse( json.getAsString(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss") );
        }

        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.toString( "yyyy-MM-dd HH:mm:ss" ) );
        }
    }

    private class ServiceStatusParsers implements JsonSerializer<Status>, JsonDeserializer<Status>
    {

        @Override
        public Status deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Status.valueOf( json.getAsString() );
        }

        @Override
        public JsonElement serialize(Status src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.name() );
        }
    }

    private class GenderParsers implements JsonSerializer<Member.Gender>, JsonDeserializer<Member.Gender>
    {
        @Override
        public Member.Gender deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Member.Gender.valueOfIgnoreCase( json.getAsString() );
        }

        @Override
        public JsonElement serialize(Member.Gender src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.name().toLowerCase() );
        }
    }

    private class RankParsers implements JsonSerializer<Member.Rank>, JsonDeserializer<Member.Rank>
    {
        @Override
        public Member.Rank deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return Member.Rank.valueOfIgnoreCase( json.getAsString() );
        }

        @Override
        public JsonElement serialize(Member.Rank src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive( src.name().toLowerCase() );
        }
    }
}
