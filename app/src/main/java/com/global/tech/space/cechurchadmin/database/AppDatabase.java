package com.global.tech.space.cechurchadmin.database;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import static androidx.room.OnConflictStrategy.REPLACE;
import static com.global.tech.space.cechurchadmin.database.AppDatabase.*;

@Database( version = 1, entities = {Attendees.class}, exportSchema = false)
@TypeConverters( Converters.class )
public abstract class AppDatabase extends RoomDatabase
{
    private static volatile AppDatabase INSTANCE;

    public abstract AttendeeDao attendeeDao();

    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder( context.getApplicationContext(), AppDatabase.class, "post_it").build();
    }

    @Entity( tableName = "attendees" )
    public static class Attendees
    {
        @PrimaryKey
        public int id;

        @ColumnInfo(name = "member_id")
        public int memberId;

        @ColumnInfo(name = "church_id")
        public String churchId;

        @ColumnInfo(name = "service_name")
        public String serviceName;

        @ColumnInfo(name = "service_date")
        public String serviceDate;

        @ColumnInfo(name = "entry_time")
        public String entryTime;

        @ColumnInfo(name = "created_at")
        public DateTime createdAt;

        @ColumnInfo(name = "updated_at")
        public DateTime updatedAt;



        @Override
        public String toString() {
            return "Attendees{" +
                    "id=" + id +
                    ", memberId=" + memberId +
                    ", churchId='" + churchId + '\'' +
                    ", serviceName='" + serviceName + '\'' +
                    ", serviceDate='" + serviceDate + '\'' +
                    ", entryTime='" + entryTime + '\'' +
                    ", createdAt=" + createdAt +
                    ", updatedAt=" + updatedAt +
                    '}';
        }
    }

    @Dao
    public static abstract class AttendeeDao
    {
        @Query("SELECT * FROM attendees" )
        abstract List<Attendees> getAttendees();

        @Insert( onConflict = REPLACE )
        abstract void addUser( Attendees attendees);

        @Transaction
        public List<Attendees> closeService()
        {
            List<Attendees> attendees = getAttendees();
            return attendees;
        }


    }

    public static class Converters
    {
        @TypeConverter
        public DateTime fromDuration( String time )
        {
            return DateTime.parse( time, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss" ) );
        }

        @TypeConverter
        public String toDuration( DateTime time )
        {
            return time.toString( DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss" ) );
        }
    }
}


