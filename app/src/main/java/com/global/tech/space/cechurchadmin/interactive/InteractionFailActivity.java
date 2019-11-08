package com.global.tech.space.cechurchadmin.interactive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.models.Member;

public class InteractionFailActivity extends AppCompatActivity {

    public static final String UNRECOGNIZED_GROUP = "unrecognized_group";
    public static final String UNRECOGNIZED_USER = "unrecognized_user";
    public static final String REG_ISSUES = "reg_issues";

    private Member member;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.interactive_fail );

        init();
        initView();
    }

    private void init() {
        member = Member.creator.fromGson(getIntent().getStringExtra("member"));
        name = getIntent().getStringExtra( "name" );
    }

    private void initView() {
        String action = getIntent().getAction();
        action = action != null ? action : "";
        switch (action)
        {
            case UNRECOGNIZED_USER:
            case REG_ISSUES:
                ((TextView) findViewById(R.id.welcome_message)).setText( String.format("Welcome %s",
                        member.gender.equals( Member.Gender.MALE ) ? "Brother" : "Sister" ) );
                ((TextView) findViewById(R.id.user_name)).setText( member.getFullName() );
                break;

            case UNRECOGNIZED_GROUP:
                ((TextView) findViewById(R.id.welcome_message)).setText( "Welcome" );
                ((TextView) findViewById(R.id.user_name)).setText( name );
                break;

            case "":
                ((TextView) findViewById(R.id.welcome_message)).setText( "Welcome" );
                ((TextView) findViewById(R.id.user_name)).setText( "" );
                break;
        }

    }

    public void finish(View view) {
        Intent intent = new Intent( this, InteractiveNameActivity.class );
        intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivity( intent );
    }
}
