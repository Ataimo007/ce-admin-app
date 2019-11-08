package com.global.tech.space.cechurchadmin.interactive;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.RegisterMemberActivity;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.common.collect.Lists;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractiveInfoActivity extends AppCompatActivity {
    private static final int REGISTER_MEMBER = 1001;
    public static final String REG_ISSUES = "reg_issues";
    private ChurchService service;
    private CEService.CEApi ceApi;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_info );

        init();
        initView();
        initName();
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
        service = ChurchService.creator.fromGson(getIntent().getStringExtra("service"));
        name = getIntent().getStringExtra("name");
    }

    private void initName() {
        ((TextView) findViewById(R.id.welcome_message ) ).setText( "Welcome" );
        ((TextView) findViewById(R.id.user_name ) ).setText( name );
    }

    private void initView() {
        String action = getIntent().getAction();
        if ( action != null && action.equals(REG_ISSUES))
            ((TextView) findViewById(R.id.info_msg)).setText(R.string.reg_issues);
    }

    public void info(View view) {
        Intent intent = new Intent( this, RegisterMemberActivity.class );
        startActivityForResult( intent, REGISTER_MEMBER );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
        {
            if ( requestCode == REGISTER_MEMBER )
            {
                String action = data.getAction();
                Member member = Member.creator.fromGson( data.getStringExtra("member"));
                if ( action != null && action.equals( RegisterMemberActivity.REG_FAIL ))
                {
                    Intent intent = new Intent( this, InteractionFailActivity.class );
                    intent.setAction( InteractionFailActivity.REG_ISSUES);
                    intent.putExtra("member", member.toGson() );
                    startActivity(intent);
                }
                else
                {
                    attend( member );
                }
            }
        }
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
