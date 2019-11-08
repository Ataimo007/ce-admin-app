package com.global.tech.space.cechurchadmin.interactive;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractiveNameActivity extends AppCompatActivity
{
    private static final int CONFIRM_NAME = 1001;
    private CEService.CEApi ceApi;
    private ArrayList<Attendee> attendees = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_name);

        init();
        initView();
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
    }

    private void initView() {
        ChurchService service = ChurchService.creator.fromGson( getIntent().getStringExtra("service") );
        ((TextView) findViewById(R.id.service_name) ).setText( service.name );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ( intent.hasExtra("attend") )
        {
            Attendee attend = Attendee.creator.fromGson(intent.getStringExtra("attend"));
            attendees.add( attend );
        }
    }

    public void checkName(View view) {
        TextInputEditText responseField = findViewById(R.id.interactive_response);
        String name = responseField.getText().toString();
        Call<ArrayList<Member>> members = ceApi.getMember(name);
        members.enqueue(new Callback<ArrayList<Member>>() {
            @Override
            public void onResponse(Call<ArrayList<Member>> call, Response<ArrayList<Member>> response) {
                if ( response.isSuccessful() )
                {
                    ArrayList<Member> members = response.body();
                    responseField.getText().clear();
                    if ( !members.isEmpty() )
                        confirm( members, name );
                    else
                        isFirstTimer( name );
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Member>> call, Throwable t) {

            }
        });
    }

    private void isFirstTimer(String name) {
        Intent intent = new Intent( this, InteractiveFirstActivity.class );
        intent.putExtra( "name", name );
        intent.putExtra( "service", getIntent().getStringExtra("service") );
        startActivity( intent );
    }

    private void confirm(ArrayList<Member> members, String name) {
        Intent intent = new Intent( this, InteractiveConfirmActivity.class );
        intent.putExtra( "members", Member.creator.toGsonArray( members.toArray(new Member[0]) ) );
        intent.putExtra( "name", name );
        intent.putExtra( "service", getIntent().getStringExtra("service") );
        startActivity( intent );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.putExtra( "attend", Attendee.creator.toGsonArray( attendees.toArray(new Attendee[0] )) );
        intent.putExtra( "service_index", getIntent().getIntExtra( "service_index", -1 ) );
        setResult( RESULT_OK, intent );
        return super.onKeyDown(keyCode, event);
    }


}
