package com.global.tech.space.cechurchadmin.interactive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.RegisterMemberActivity;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.Member;

public class InteractionSuccessActivity extends AppCompatActivity {

    public static final String ATTENDING_SERVICE = "attending_service";
    public static final String ATTENDED_SERVICE = "attended_service";
    private static final int UPDATE_MEMBER = 1001;
    private Member member;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.interactive_success );

        init();
        initView();
    }

    private void init() {
        String action = getIntent().getAction();

        Log.d("Interactive_Session", "Successful Action " + action );
        Log.d("Interactive_Session", "Successful Attendance " + getIntent().getStringExtra("attend") );
        Log.d("Interactive_Session", "Successful Member " + getIntent().getStringExtra("member") );

        if (action != null && action.equals(ATTENDED_SERVICE))
        {
            member = Member.creator.fromGson(getIntent().getStringExtra("member"));
        }
        else
        {
            Attendee attend = Attendee.creator.fromGson(getIntent().getStringExtra("attend"));
            member = attend.member;
        }
    }

    private void initView() {
        ((TextView) findViewById(R.id.welcome_message)).setText( String.format("Welcome %s",
                member.gender.equals( Member.Gender.MALE ) ? "Brother" : "Sister" ) );
        ((TextView) findViewById(R.id.user_name)).setText( member.getFullName() );

        String action = getIntent().getAction();
        if (action != null && action.equals(ATTENDED_SERVICE))
            ((TextView) findViewById(R.id.info_msg)).setText( R.string.attended );

        if (member.needsUpdate())
            findViewById(R.id.interact_update).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.interact_update).setVisibility(View.GONE);
    }

    public void finish(View view) {
        Intent intent = new Intent( this, InteractiveNameActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        String action = getIntent().getAction();
        if (action == null || !action.equals(ATTENDED_SERVICE))
            intent.putExtra("attend", getIntent().getStringExtra("attend") );
        startActivity( intent );
    }

    public void updateDetails(View view) {
        Intent intent = new Intent( this, RegisterMemberActivity.class );
        intent.setAction( RegisterMemberActivity.EDIT_MEMBER );
        intent.putExtra( "member", member.toGson() );
        startActivityForResult( intent, UPDATE_MEMBER );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK )
        {
            if ( requestCode == UPDATE_MEMBER )
            {
                member = Member.creator.fromGson(data.getStringExtra("member"));
                initView();
            }
        }
    }
}
