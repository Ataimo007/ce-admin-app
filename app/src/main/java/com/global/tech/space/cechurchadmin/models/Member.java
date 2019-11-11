package com.global.tech.space.cechurchadmin.models;

import android.util.Log;

import com.global.tech.space.cechurchadmin.helpers.GsonConverter;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import java.util.Objects;

import static com.global.tech.space.cechurchadmin.helpers.Helper.toWordCase;

public class Member extends Model< Member > implements Searchable
{
    @SerializedName("church_id")
    @Expose
    public int churchId;

    @SerializedName("first_name")
    @Expose
    public String firstName;

    @SerializedName("surname")
    @Expose
    public String surname;

    @SerializedName("other_names")
    @Expose
    public String otherNames;

    @SerializedName("gender")
    @Expose
    public Gender gender;

    @SerializedName("rank")
    @Expose
    public Rank rank;

    @SerializedName("email_address")
    @Expose
    public String emailAddress;

    @SerializedName("cell_id")
    @Expose
    public Integer cellId;

    @SerializedName("cell")
    @Expose
    public Cell cell;

    @SerializedName("phone_number")
    @Expose
    public String phoneNumber;

    @SerializedName("kings_chat_no")
    @Expose
    public String kingsChatNo;

    @SerializedName("date_of_birth")
    @Expose
    public DateOfBirth dateOfBirth;

    @SerializedName("home_address")
    @Expose
    public String homeAddress;

    public static enum Gender{ MALE, FEMALE;
        public static Gender valueOfIgnoreCase( String name )
        {
            return Gender.valueOf( name.toUpperCase() );
        }
    }

    public static enum Rank{
        FIRST_TIMER( 0, "First Timer" ),
        VISITOR( 0, "Visitor" ),
        MEMBER( 0, "Member" ),
        LEADER( 0, "Leader" ),
        SENIOR_LEADER( 0, "Senior Leader" ),
        CELL_EXECUTIVE( 0, "Cell Executive" ),
        CELL_LEADER( 0, "Cell Leader" ),
        COORDINATOR( 0, "Coordinator" ),
        PASTOR( 0, "Pastor" ),
        SUB_GROUP_PASTOR( 0, "Sub Group Pastor" ),
        GROUP_PASTOR( 0, "Group Pastor" ),
        ZONAL_PASTOR( 0, "Zonal Pastor" ),
        DEACON( 0, "Deacon" );

        public int rank;
        public String name;

        Rank(int value, String name)
        {
            rank = value;
            this.name = name;
        }

        public static Rank valueOfIgnoreCase( String name )
        {
            for ( Rank rank : Rank.values() )
                if ( rank.name.equals( name ) )
                    return rank;
            return null;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class DateOfBirth
    {
        private LocalDate date;
        private boolean ignoreYear = true;

        public DateOfBirth()
        {
            date = new LocalDate();
        }

        public DateOfBirth(LocalDate date) {
            this.date = date;
        }

        public DateOfBirth( String dob )
        {
            if ( dob == null || dob.isEmpty() )
                date = null;
            else
            {
                try
                {
                    date = LocalDate.parse(dob, DateTimeFormat.forPattern("MMM dd, yyyy") );
                    ignoreYear = false;
                }
                catch ( IllegalArgumentException ex )
                {
                    date = LocalDate.parse(dob, DateTimeFormat.forPattern("MMM dd,") );
                }
            }
        }

        public boolean needsUpdate()
        {
            return isNull() || isIgnoreYear();
        }

        public boolean isNull()
        {
            return date == null;
        }

        public boolean isIgnoreYear()
        {
            return ignoreYear;
        }

        public DateOfBirth(LocalDate date, boolean ignore) {
            this.date = date;
            ignoreYear = ignore;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DateOfBirth that = (DateOfBirth) o;
            if ( !ignoreYear )
                return date.equals(that.date);
            else
                return date.getMonthOfYear() == that.date.getMonthOfYear() && date.getDayOfMonth() == that.date.getDayOfMonth();
        }

        @Override
        public int hashCode() {
            return Objects.hash(date, ignoreYear);
        }

        public static DateOfBirth toDate(String dob )
        {
            try
            {
                LocalDate cdob = LocalDate.parse(dob, DateTimeFormat.forPattern("MMM dd, yyyy") );
                return new DateOfBirth( cdob, false );
            }
            catch ( Exception ex )
            {
                LocalDate cdob = LocalDate.parse(dob, DateTimeFormat.forPattern("MMM dd,") );
                return new DateOfBirth( cdob );
            }
        }

        public String store()
        {
            if ( date != null )
                return ignoreYear ? date.toString( "MMM dd," ) : date.toString( "MMM dd, yyyy" );
            return "";
        }

        public LocalDate getDate() {
            return date;
        }

        @Override
        public String toString() {
            return ignoreYear ? date.toString( DateTimeFormat.forPattern("EEEE dd, MMMM.") ) :
                    date.toString( DateTimeFormat.forPattern("EEEE dd, MMMM yyyy.") );
        }
    }

    public final static Member creator = new Member();

    public Member()
    {
        super( Member.class );
        churchId = 1;
    }

    public Member(String first, String surname, String otherNames, Gender gender, String phone, String kingsChatNo, String email, String dob, String address, int cellId) {
        this();
        firstName = first;
        this.surname = surname;
        this.otherNames = otherNames;
        this.gender = gender;
        phoneNumber = phone;
        this.kingsChatNo = kingsChatNo;
        emailAddress = email;
        dateOfBirth = new DateOfBirth( LocalDate.parse( dob ) ); //subject to change
        homeAddress = address;
        this.cellId = cellId;
    }

    public void update(String first, String surname, String otherNames, Gender gender, String phone, String kingsChatNo, String email,
                       DateOfBirth dob, String address, Cell cell, Rank rank ) {
        firstName = first;
        this.surname = surname;
        this.otherNames = otherNames;
        this.gender = gender;
        phoneNumber = phone;
        this.kingsChatNo = kingsChatNo;
        emailAddress = email;
        dateOfBirth = dob; //subject to change
        homeAddress = address;
        this.rank = rank;
        this.cellId = cell != null ? cell.id : null;
    }

    public Member(String first, String surname, String otherNames, Gender gender, String phone, String kingsChatNo, String email,
                  DateOfBirth dob, String address, Cell cell, Rank rank) {
        this();
        firstName = first;
        this.surname = surname;
        this.otherNames = otherNames;
        this.gender = gender;
        phoneNumber = phone;
        this.kingsChatNo = kingsChatNo;
        emailAddress = email;
        dateOfBirth = dob; //subject to change
        homeAddress = address;
        this.rank = rank;
        this.cellId = cell != null ? cell.id : null;
    }

    public String getFullName()
    {
        return String.format( "%s%s%s",
                firstName != null ? toWordCase( firstName ) + " " : "",
                surname != null ? toWordCase( surname ) + " " : "",
                otherNames != null ? toWordCase( otherNames ) : ""
        );
    }

    public Member(int churchId, String first, String surname, String otherNames, Gender gender, String phone, String kingsChatNo, String email, String dob, String address, int cellId) {
        this( first, surname, otherNames, gender, phone, kingsChatNo, email, dob, address, cellId );
        this.churchId = churchId;
    }

    @Override
    public boolean search( String query )
    {
        boolean month = (dateOfBirth != null && dateOfBirth.getDate().toString("MMMM").toLowerCase().contains(query));
        boolean truth = ( firstName != null && firstName.toLowerCase().contains(query) )
                || ( surname != null && surname.toLowerCase().contains(query) )
                || ( otherNames != null && otherNames.toLowerCase().contains(query) )
                || ( phoneNumber != null && phoneNumber.contains(query) )
                || ( emailAddress != null && emailAddress.toLowerCase().contains(query) )
                || (kingsChatNo != null && kingsChatNo.contains(query) )
                || (cell != null && cell.name != null && cell.name.toLowerCase().contains(query))
                || month
                || ( homeAddress != null && homeAddress.toLowerCase().contains(query) )
                || ( gender != null && gender.toString().toLowerCase().contains(query) );
        return truth;
    }

    public boolean needsUpdate()
    {
        boolean dob = dateOfBirth == null || dateOfBirth.needsUpdate();
        boolean cell = cellId == null;
        boolean gender = this.gender == null;

        return ( firstName == null || firstName.isEmpty() )
                || ( surname == null || surname.isEmpty() )
                || ( otherNames == null || otherNames.isEmpty() )
                || ( phoneNumber == null || phoneNumber.isEmpty() )
                || ( kingsChatNo == null || kingsChatNo.isEmpty() )
                || ( homeAddress == null || homeAddress.isEmpty() )
                || ( emailAddress == null || emailAddress.isEmpty() )
                || dob || cell || gender;
    }
}
