package com.global.tech.space.cechurchadmin.attendance;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.MainSearchActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.dialogs.CellDialogFragment;
import com.global.tech.space.cechurchadmin.adapters.CellMeetingAdapter;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.CellWeekInfo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeekCellActivity extends AppCompatActivity implements CellDialogFragment.CellDialogListener
{
    public static final int ADD_MEETING = 1001;
    public static final int UPDATE_MEETING = 1002;

    private CellMeetingAdapter adapter;
    private RecyclerView list;
    private CEService ceService;
    private List<CellMeeting> cellMeetings = new ArrayList<>();
    private CellWeekInfo cellWeekInfo;
    private CellMeeting newWeekMeeting;
    private boolean weekUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_cell_meetings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        initAppBar();
        initControls();
    }

    private void initAppBar() {
        getSupportActionBar().setTitle( "Week " + cellWeekInfo.week );
        getSupportActionBar().setSubtitle( "Meetings Held : " + cellWeekInfo.meetingsHeld );
    }

    private void initControls()
    {
        initView();
        initRecyclerView();
    }

    private void initView()
    {
        list = findViewById( R.id.members_list );
    }

    private void initRecyclerView() {
        adapter = new CellMeetingAdapter( this, cellMeetings, UPDATE_MEETING );
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
        cellWeekInfo = CellWeekInfo.creator.fromGson( getIntent().getStringExtra( "week_meetings" ) );
        ceService = CEService.getInstance();
        getWeekMeetings();
    }

    private void getWeekMeetings() {
        Log.d( "Attendance_Activity", "getting churchServices from api"  );
        Call<List<CellMeeting>> attendeesCall = ceService.getCeApi().getCellMeetingsOfWeek( cellWeekInfo.week );
        attendeesCall.enqueue(new Callback<List<CellMeeting>>() {
            @Override
            public void onResponse(Call<List<CellMeeting>> call, Response<List<CellMeeting>> response) {
                if ( response.isSuccessful() )
                {
                    cellMeetings.clear();
                    cellMeetings.addAll( response.body() );
                    if ( adapter != null )
                    {
                        adapter.notifyDataSetChanged();
                    }
                    Log.d( "Attendance_Activity", "churchServices " + cellMeetings);
                }
                else {
                    Log.d( "Attendance_Activity", "churchServices unsuccessful " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<List<CellMeeting>> call, Throwable t) {
                Log.d( "Attendance_Activity", "churchServices failed " + t.getMessage() );
            }
        });
    }

//    public void addMembers(View view) {
//        Intent search = new Intent( this, SearchMembersActivity.class );
//        search.putExtra( "cell_meeting", getIntent().getStringExtra("cell_meeting" ) );
//        startActivityForResult( search, 11 );
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CellMeeting cellMeeting;
        switch ( requestCode )
        {
            case ADD_MEETING:
                cellMeeting = CellMeeting.creator.fromGson(data.getStringExtra( "cell_meeting" ));
                if ( cellMeeting.week == cellWeekInfo.week )
                {
                    cellMeetings.add( 0, cellMeeting );
                    adapter.notifyItemInserted( 0 );
//                    ++cellMeeting.week;
                    ++cellWeekInfo.meetingsHeld;
                    weekUpdate = true;
                }
                else
                {
                    newWeekMeeting = cellMeeting;
                    finishNewMeeting();
                }
                break;

            case UPDATE_MEETING:
                int index = data.getIntExtra("meeting_index", -1);
                cellMeeting = CellMeeting.creator.fromGson(data.getStringExtra( "cell_meeting" ));
                if ( index != -1 )
                {
                    cellMeetings.remove( index );
                    cellMeetings.add( index, cellMeeting );
                }
                adapter.notifyItemChanged( index );
        }
    }

    public void beginMeeting( View view )
    {
        beginMeeting();
    }

    public void beginMeeting()
    {
        CellDialogFragment cellDialog = new CellDialogFragment();
        cellDialog.show( getSupportFragmentManager() );
    }

    @Override
    public void select(Cell cell) {
        beginMeeting( cell );
    }

    public void beginMeeting(Cell cell)
    {
        CellMeeting meeting = new CellMeeting( cell.id );
        Call<CellMeeting> begin = ceService.getCeApi().beginCellMeeting(meeting);
        begin.enqueue(new Callback<CellMeeting>() {
            @Override
            public void onResponse(Call<CellMeeting> call, Response<CellMeeting> response) {
                if ( response.isSuccessful() )
                {
                    CellMeeting newMeeting = response.body();
                    if  ( newMeeting != null )
                    {
                        newMeeting.cell = cell;
                        takeAttendance( newMeeting );
                    }
                }
                else
                {
                    Log.d( "Service_Fragment", "meeting response error " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<CellMeeting> call, Throwable t) {
                Log.d("Service_Fragment", "Church Service not started " + t.getMessage() );
            }
        });

    }

    private void takeAttendance( CellMeeting service )
    {
        Intent intent = new Intent( this, CellAttendanceActivity.class );
        intent.putExtra( "cell_meeting", service.toGson() );
        startActivityForResult( intent, ADD_MEETING );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        if ( weekUpdate )
            intent.putExtra( "week_meetings", cellWeekInfo.toGson() );
        setResult( RESULT_OK, intent );
        return super.onKeyDown(keyCode, event);
    }

    public void finishNewMeeting()
    {
        Intent intent = new Intent();
        if ( weekUpdate )
            intent.putExtra( "week_meetings", cellWeekInfo.toGson() );
        intent.putExtra( "cell_meeting", newWeekMeeting.toGson() );
        setResult( RESULT_OK, intent );
        finish();
    }

    public void search(View view) {
        Intent intent = new Intent( this, MainSearchActivity.class );
        intent.setAction( MainSearchActivity.SEARCH_CELL_MEETINGS );
        intent.putExtra("week_meetings", cellWeekInfo.toGson() );
        startActivity( intent );
    }
}

