package com.global.tech.space.cechurchadmin.interactive;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.helpers.Helper;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractiveConfirmActivity extends AppCompatActivity
{
    private CEService.CEApi ceApi;
    private Member[] members;
    private String name;
    private int question = 1;
    private Member.DateOfBirth dob;
    private ChurchService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_extra);

        init();
        initView();
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
        initInfo();
    }

    private void initInfo() {
        String[] gMembers = getIntent().getStringArrayExtra("members");
        members = new Member[ gMembers.length ];
        for ( int i = 0; i < gMembers.length; ++i )
            members[ i ] = Member.creator.fromGson( gMembers[ i ] );
        name = getIntent().getStringExtra("name");
        service = ChurchService.creator.fromGson( getIntent().getStringExtra("service") );
    }

    private void initView() {
        initName();
        initQuestion();
    }

    private void initQuestion() {
        ((TextInputEditText) findViewById(R.id.interactive_response)).getText().clear();
        String question = getQuestion();
        if ( question != null )
            ((TextInputLayout) findViewById(R.id.interactive_question)).setHint( question  );
    }

    private String getQuestion() {
        TextInputEditText field = (TextInputEditText) findViewById(R.id.interactive_response);
        String q = null;
        switch ( question )
        {
            case 1:
                for ( Member member : members )
                    if ( member.phoneNumber != null && !member.phoneNumber.isEmpty()  ||
                            member.kingsChatNo != null && !member.kingsChatNo.isEmpty() )
                    {
                        q = "Enter Your Phone Number";
                        field.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    }

                if ( q != null )
                {
                    question = 1;
                    break;
                }

            case 2:
                for ( Member member : members )
                    if ( member.emailAddress != null && !member.emailAddress.isEmpty() )
                    {
                        q = "Enter Your Email Address";
                        field.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        break;
                    }

                if ( q != null )
                {
                    question = 2;
                    break;
                }

            case 3:
                for ( Member member : members )
                    if ( member.dateOfBirth != null )
                    {
                        q = "Enter Your Date Of Birth";
                        date( field );
                        break;
                    }

                if ( q != null )
                {
                    question = 3;
                    break;
                }

            case 4:
                q = "Enter Your Full Name";
                question = 4;
                break;

            default:
                error();
        }
        return q;
    }

    private void date(TextInputEditText field) {
        field.setOnTouchListener( this::selectDob);
    }

    private boolean selectDob(View view, MotionEvent event )
    {
        if ( event.getAction() == MotionEvent.ACTION_UP )
        {
            DateTime now = DateTime.now();
            DatePickerDialog picker = new DatePickerDialog(this, R.style.Theme_MaterialComponents_Light_Dialog_Alert,
                    (cView, year, month, dayOfMonth) ->
                    {
                        dob = new Member.DateOfBirth( LocalDate.parse(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth),
                                DateTimeFormat.forPattern("yyyy-MM-dd")), false );
                        TextInputEditText field = (TextInputEditText) view;
                        field.setText( dob.toString() );
                    },
                    now.getYear(), now.getMonthOfYear(), now.getDayOfMonth() );
            picker.setTitle( "Select Date Of Birth" );
            picker.show();
        }
        return view.performClick();
    }

    private void initName() {
        if ( members.length == 1 )
        {
            Member member = members[ 0 ];
            ((TextView) findViewById(R.id.welcome_message ) ).setText( String.format("Welcome %s",
                    member.gender == Member.Gender.MALE ? "Brother" : "Sister" ));
            ((TextView) findViewById(R.id.user_name ) ).setText( member.getFullName() );
        }
        else
        {
            ((TextView) findViewById(R.id.welcome_message ) ).setText( "Welcome" );
            ((TextView) findViewById(R.id.user_name ) ).setText( name );
        }
    }

    public void noRemembrance(View view) {
        next();
    }

    private void next() {
        ++question;
        initQuestion();
    }

    public void confirm(View view) {
        String response = ((TextInputEditText) findViewById(R.id.interactive_response)).getText().toString();

        int truth = 0;
        Member trueMember = null;

        switch ( question )
        {
            case 1:
                response = response.trim().toLowerCase();
                for ( Member member : members )
                {
                    if ( member.phoneNumber != null && member.phoneNumber.equals(response) ||
                            member.kingsChatNo != null && member.kingsChatNo.equals(response) )
                    {
                        trueMember = member;
                        break;
                    }
                }
                if  ( trueMember != null )
                    success( trueMember );
                else
                    incorrect();
                break;


            case 2:
                response = response.trim().toLowerCase();
                for ( Member member : members )
                    if ( member.emailAddress != null && member.emailAddress.equals(response) )
                    {
                        trueMember = member;
                        break;
                    }
                if  ( trueMember != null )
                    success( trueMember );
                else
                    incorrect();
                break;

            case 3:
                for ( Member member : members )
                    if ( member.dateOfBirth != null && member.dateOfBirth.equals( dob ) )
                    {
                        trueMember = member;
                        ++truth;
                    }

                if ( truth == 0 )
                {
                    incorrect();
                    break;
                }

                if ( truth == 1 )
                {
                    success( trueMember );
                    break;
                }

                next();
                break;

            case 4:
                response = response.trim().toLowerCase();
                for ( Member member : members )
                    if ( checkFullName( member, response ) )
                    {
                        trueMember = member;
                        ++truth;
                    }

                if ( truth == 0 )
                {
                    incorrect();
                    break;
                }

                if ( truth == 1 )
                {
                    success( trueMember );
                    break;
                }

                next();
                break;


            default:
                error();
        }
    }

    private boolean checkFullName(Member member, String response) {
        String[] names = response.split(" ");
        boolean first = false, sur = false, others = false;

        for ( String name : names )
        {
            if ( member.firstName.trim().toLowerCase().equals( name ) )
                first = true;
        }

        for ( String name : names )
        {
            if ( member.surname.trim().toLowerCase().equals( name ) )
                sur = true;
        }

        for ( String name : names )
        {
            if ( member.otherNames.trim().toLowerCase().equals( name ) )
                others = true;
        }

        return first && sur && others;
    }

    private void incorrect() {
        String msg = "Please the Information Given is Incorrect";
        Helper.toast( this, msg );
    }

    private void error() {
        if ( members.length == 1 )
            errorMsg( members[ 0 ] );
        else
            errorMsg();
    }

    private void errorMsg() {
        Intent intent = new Intent( this, InteractionFailActivity.class );
        intent.putExtra( "name", name );
        intent.setAction(InteractionFailActivity.UNRECOGNIZED_GROUP);
        startActivity( intent );
    }

    private void success( Member member )
    {
        attend( member );
    }

    private void success(Attendee attended) {
        Intent intent = new Intent( this, InteractionSuccessActivity.class );
        intent.putExtra( "attend", attended.toGson() );
        intent.setAction(InteractionSuccessActivity.ATTENDING_SERVICE);
        Log.d("Interactive_Session", "Confirming Attendance " + attended );
        startActivity( intent );
    }

    private void succeeded(Member member) {
        Intent intent = new Intent( this, InteractionSuccessActivity.class );
        intent.putExtra( "member", member.toGson() );
        intent.setAction(InteractionSuccessActivity.ATTENDED_SERVICE);
        Log.d("Interactive_Session", "Confirming Member " + member );
        startActivity( intent );
    }

    private void errorMsg(Member member) {
        Intent intent = new Intent( this, InteractionFailActivity.class );
        intent.putExtra( "member", member.toGson() );
        startActivity( intent );
    }

    private void attend(Member member) {
        Attendee attends = service.attends(member);
        Call<List<Attendee>> attended = ceApi.attendService(Lists.newArrayList(attends));
        attended.enqueue(new Callback<List<Attendee>>() {
            @Override
            public void onResponse(Call<List<Attendee>> call, Response<List<Attendee>> response) {
                if ( response.isSuccessful() )
                {
                    List<Attendee> attend = response.body();
                    if ( attend != null )
                    {
                        if  ( !attend.isEmpty() )
                            success( attend.get( 0 ) );
                        else
                            succeeded( member );
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Attendee>> call, Throwable t) {

            }
        });
    }
}
