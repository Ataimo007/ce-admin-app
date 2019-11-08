package com.global.tech.space.cechurchadmin.interactive;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.RegisterMemberActivity;
import com.global.tech.space.cechurchadmin.models.Member;

public class InteractiveFirstActivity extends AppCompatActivity
{
    private static final int REGISTER_FIRST = 1001;
    private String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interactive_first);

        init();
        initName();
    }

    private void init() {
        name = getIntent().getStringExtra("name");
    }

    private void initName() {
        ((TextView) findViewById(R.id.welcome_message ) ).setText( "Welcome" );
        ((TextView) findViewById(R.id.user_name ) ).setText( name );
    }

    public void yesResponse(View view) {
        Intent intent = new Intent( this, InteractiveInfoActivity.class );
        intent.putExtra( "name", name );
        intent.putExtra( "service", getIntent().getStringExtra("service") );
        startActivity( intent );
    }

    public void noResponse(View view) {
        Intent intent = new Intent( this, InteractiveInfoActivity.class );
        intent.setAction(InteractiveInfoActivity.REG_ISSUES);
        intent.putExtra( "name", name );
        intent.putExtra( "service", getIntent().getStringExtra("service") );
        startActivity( intent );
    }

}
