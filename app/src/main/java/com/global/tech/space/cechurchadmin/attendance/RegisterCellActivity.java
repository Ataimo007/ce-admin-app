package com.global.tech.space.cechurchadmin.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.dialogs.CellBottomSheets;
import com.global.tech.space.cechurchadmin.helpers.Helper;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputEditText;

import org.joda.time.LocalDate;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCellActivity extends AppCompatActivity {

    public static final int ADD_MEMBER = 2001;
    private static final int SELECT_LEADER = 3001;
    private static final int SELECT_ASSISTANT = 3002;

    private CEService.CEApi ceApi;
    private LocalDate cdob;
    private CellBottomSheets cells;

    private Member leader;
    private Member assistant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cell_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initViews();
    }

    private void initViews() {
        initMemberFields();
        initToolbar();
    }

    private void initToolbar() {
        CollapsingToolbarLayout toolbar = findViewById(R.id.cell_reg_collapse);
        toolbar.setCollapsedTitleTextColor(ResourcesCompat.getColor( getResources(), R.color.white, null ));
    }

    private boolean selectLeader( View view, MotionEvent event )
    {
        if ( event.getAction() == MotionEvent.ACTION_UP )
        {
            Intent intent = new Intent( this, SearchMembersActivity.class );
            intent.setAction( SearchMembersActivity.SELECT_MEMBER_NO_CELL);
            startActivityForResult( intent, SELECT_LEADER );
        }
        return view.performClick();
    }

    private boolean selectAssistant( View view, MotionEvent event )
    {
        if ( event.getAction() == MotionEvent.ACTION_UP )
        {
            Intent intent = new Intent( this, SearchMembersActivity.class );
            intent.setAction( SearchMembersActivity.SELECT_MEMBER_NO_CELL);
            startActivityForResult( intent, SELECT_ASSISTANT );
        }
        return view.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TextInputEditText field;
        if ( resultCode == RESULT_OK )
        {
            switch ( requestCode )
            {
                case SELECT_LEADER:
                   leader = Member.creator.fromGson( data.getStringExtra("member" ) );
                   field = findViewById( R.id.regs_cell_leader );
                   field.setText( leader.getFullName() );
                   break;


                case SELECT_ASSISTANT:
                   assistant = Member.creator.fromGson( data.getStringExtra("member" ) );
                   field = findViewById( R.id.regs_cell_assistant );
                   field.setText( assistant.getFullName() );
                   break;
            }
        }
    }

    private void initMemberFields() {
        TextInputEditText leader = findViewById( R.id.regs_cell_leader );
        TextInputEditText assistant = findViewById( R.id.regs_cell_assistant );

        leader.setCursorVisible( false );
        assistant.setCursorVisible( false );

        leader.setOnTouchListener( this::selectLeader );
        assistant.setOnTouchListener( this::selectAssistant );

    }



    private void init() {
        ceApi = CEService.getInstance().getCeApi();
    }

    public void register(View view) {
        String name = ( (TextInputEditText) findViewById(R.id.regs_cell_name) ).getText().toString();
        int leader = this.leader.id;
        int assistant = this.assistant.id;
        String location = ( (TextInputEditText) findViewById(R.id.regs_cell_location) ).getText().toString();
        String purpose = ( (TextInputEditText) findViewById(R.id.regs_cell_purpose) ).getText().toString();

        Cell cell = new Cell( name, leader, assistant, location, purpose );
        register( cell );
    }

    private void register(Cell cell) {
        Call<Cell> memberCall = ceApi.cellRegister(cell);
        memberCall.enqueue(new Callback<Cell>() {
            @Override
            public void onResponse(Call<Cell> call, Response<Cell> response) {
                if ( response.isSuccessful() )
                {
                    Cell cell = response.body();
                    if ( cell != null )
                    {
                        String msg = "Successfully Register Member";
                        Helper.toast( RegisterCellActivity.this, msg );

                        Intent intent = new Intent();
                        intent.putExtra( "cell", cell.toGson() );
                        setResult( RESULT_OK, intent );
                        finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<Cell> call, Throwable t) {

            }
        });
    }

}
