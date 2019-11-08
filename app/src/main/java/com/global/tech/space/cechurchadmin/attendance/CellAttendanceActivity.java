package com.global.tech.space.cechurchadmin.attendance;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.adapters.CellAttendeeAdapter;
import com.global.tech.space.cechurchadmin.models.CellAttendee;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.States;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CellAttendanceActivity extends AppCompatActivity {

    public static final int ADD_MEETING = 1001;
    public static final int UPDATE_MEETING = 1002;
    public static final int ADD_MEMBER = 1003;

    private CellAttendeeAdapter adapter;
    private RecyclerView list;
    private CEService ceService;
//    private CellMeeting current;
    private CellMeeting cellMeeting;
    private List<CellAttendee> attendees = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initAppBar();
        initControls();
    }

    private void initAppBar() {
        getSupportActionBar().setTitle( cellMeeting.cell.name );
        getSupportActionBar().setSubtitle( "Cell Meeting Attendance : " + 0 );
    }

    private void initControls()
    {
        initView();
        initRecyclerView();
        ifClosed();
        initEdit();
    }

    private void initEdit()
    {
        BottomAppBar bottom = findViewById(R.id.attendance_bottom_bar);
        bottom.setNavigationOnClickListener( v -> {

        });
    }

    private void ifClosed() {
        if  ( cellMeeting.status.equals( States.Status.CLOSED ) )
        {
            FloatingActionButton add = findViewById(R.id.add_members);
            add.hide();
        }
    }

    private void initView()
    {
        list = findViewById( R.id.members_list );
    }

    private void initRecyclerView() {
        adapter = new CellAttendeeAdapter( this, attendees );
        LinearLayoutManager layout = new LinearLayoutManager( this );
        list.setLayoutManager( layout );
        list.setAdapter(adapter);

        list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 5;
                outRect.bottom = 5;
                outRect.left = 10;
                outRect.right = 10;

                if ( parent.getChildAdapterPosition( view ) == parent.getAdapter().getItemCount() - 1 )
                    outRect.bottom = 200;
            }
        });
    }

    private void init() {
        ceService = CEService.getInstance();
        cellMeeting = CellMeeting.creator.fromGson(getIntent().getStringExtra("cell_meeting"));
        getAttendance();
    }

    private void getAttendance() {
        Log.d( "Attendance_Activity", "getting churchServices from api"  );
        Call<List<CellAttendee>> attendeesCall = ceService.getCeApi().cellMeetingAttendees(cellMeeting.id);
        attendeesCall.enqueue(new Callback<List<CellAttendee>>() {
            @Override
            public void onResponse(Call<List<CellAttendee>> call, Response<List<CellAttendee>> response) {
                if ( response.isSuccessful() )
                {
                    attendees.clear();
                    attendees.addAll( response.body() );
                    if ( adapter != null )
                    {
                        adapter.notifyDataSetChanged();
                        updateAttendance();
                    }
                    Log.d( "Attendance_Activity", "churchServices " + attendees);
                }
                else {
                    Log.d( "Attendance_Activity", "churchServices unsuccessful " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<List<CellAttendee>> call, Throwable t) {
                Log.d( "Attendance_Activity", "churchServices failed " + t.getMessage() );
            }
        });
    }

    public void addMembers(View view) {
        Intent search = new Intent( this, SearchMembersActivity.class );
        search.putExtra( "cell_meeting", getIntent().getStringExtra("cell_meeting" ) );
        search.setAction( SearchMembersActivity.ATTEND_CELL_MEETING );
        startActivityForResult( search, ADD_MEMBER );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == ADD_MEMBER )
        {
            if  ( data != null )
                updateInsert( data.getIntExtra("insert_size", 0 ) );
        }
    }

    private void updateAttendance()
    {
        getSupportActionBar().setSubtitle( "Cell Meeting Attendance : " + attendees.size() );
    }

    private void updateInsert(int size) {
        Call<List<CellAttendee>> recent = ceService.getCeApi().CellMeetingRecentAttendees(size, cellMeeting.id);
        recent.enqueue(new Callback<List<CellAttendee>>() {
            @Override
            public void onResponse(Call<List<CellAttendee>> call, Response<List<CellAttendee>> response) {
                if ( response.isSuccessful() )
                {
                    List<CellAttendee> recents = response.body();
                    int first = attendees.size();
                    attendees.addAll( recents );
                    adapter.notifyItemRangeInserted( first, size );
                    list.invalidateItemDecorations();
                    cellMeeting.attendance = attendees.size();
                    update();
                }
            }

            @Override
            public void onFailure(Call<List<CellAttendee>> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ( cellMeeting.status.equals( States.Status.ONGOING ) )
        {
            MenuInflater inflater = new MenuInflater(this);
            inflater.inflate( R.menu.attendance_top, menu );
            return true;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() )
        {
            case R.id.action_close:
                close();
                break;
        }

        return super.onOptionsItemSelected(item);

    }

    private void update() {
        Call<CellMeeting> update = ceService.getCeApi().updateCellAttendance(cellMeeting);
        update.enqueue(new Callback<CellMeeting>() {
            @Override
            public void onResponse(Call<CellMeeting> call, Response<CellMeeting> response) {
                if ( response.isSuccessful() )
                {
                    CellMeeting service = response.body();
                    if ( service != null )
                        updateAttendance();
                }
            }

            @Override
            public void onFailure(Call<CellMeeting> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.putExtra( "cell_meeting", cellMeeting.toGson() );
        intent.putExtra( "meeting_index", getIntent().getIntExtra("meeting_index", -1) );
        setResult( RESULT_OK, intent );
        return super.onKeyDown(keyCode, event);
    }

    private void close() {
        cellMeeting.endTime = new LocalTime();
        Call<CellMeeting> close = ceService.getCeApi().closeCellMeeting(cellMeeting);
        close.enqueue(new Callback<CellMeeting>() {
            @Override
            public void onResponse(Call<CellMeeting> call, Response<CellMeeting> response) {
                if ( response.isSuccessful() )
                {
                    CellMeeting cellMeeting = response.body();
                    if ( cellMeeting.status.equals( States.Status.CLOSED ) )
                    {
                        Intent intent = new Intent();
                        intent.putExtra( "cell_meeting", cellMeeting.toGson() );
                        intent.putExtra( "meeting_index", getIntent().getIntExtra("meeting_index", -1) );
                        setResult( RESULT_OK, intent );
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<CellMeeting> call, Throwable t) {

            }
        });
    }

    public void actions(View view) {
    }

}

