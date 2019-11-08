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
import com.global.tech.space.cechurchadmin.dialogs.MultiOptionDialogFragment;
import com.global.tech.space.cechurchadmin.interactive.InteractiveNameActivity;
import com.global.tech.space.cechurchadmin.adapters.AttendeeAdapter;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.States;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity extends AppCompatActivity implements MultiOptionDialogFragment.MultiOptionListener
{
    private static final int ADD_MEMBERS = 1001;
    private static final int ADD_ATTENDANCE = 1002;

    private AttendeeAdapter adapter;
    private RecyclerView list;
    private CEService ceService;
    private ChurchService current;
    private ChurchService service;
    private List<Attendee> attendees = new ArrayList<>();

    private final String attendanceMethods[] = {"Interactive Method", "Selection Of Member"};

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
        getSupportActionBar().setTitle( service.name );
        getSupportActionBar().setSubtitle( "Attendee - " + 0 );
    }

    private void initControls()
    {
        initView();
        initRecyclerView();
        ifClosed();
    }

    public void editService(View view) {

    }

    public void absentees(View view) {
        Intent intent = new Intent( this, AnalysisActivity.class );
        intent.putExtra( "service", service.toGson() );
        startActivity( intent );
    }

    private void ifClosed() {
        if  ( service.status.equals( States.Status.CLOSED ) )
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
        adapter = new AttendeeAdapter( this, attendees );
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
        service = ChurchService.creator.fromGson(getIntent().getStringExtra("service"));
        getAttendance();
    }

    private void getAttendance() {
        Log.d( "Attendance_Activity", "getting churchServices from api"  );
        Call<List<Attendee>> attendeesCall = ceService.getCeApi().serviceAttendees(service.id);
        attendeesCall.enqueue(new Callback<List<Attendee>>() {
            @Override
            public void onResponse(Call<List<Attendee>> call, Response<List<Attendee>> response) {
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
            public void onFailure(Call<List<Attendee>> call, Throwable t) {
                Log.d( "Attendance_Activity", "churchServices failed " + t.getMessage() );
            }
        });
    }

    public void addMembers() {
        Intent search = new Intent( this, SearchMembersActivity.class );
        search.putExtra( "service", getIntent().getStringExtra("service" ) );
        search.setAction( SearchMembersActivity.ATTEND_SERVICE );
        startActivityForResult( search, ADD_MEMBERS );
    }

    public void addInteract() {
        Intent interact = new Intent( this, InteractiveNameActivity.class );
        interact.putExtra( "service", getIntent().getStringExtra("service" ) );
        startActivityForResult( interact, ADD_ATTENDANCE );
    }

    public void addMembers(View view) {
        MultiOptionDialogFragment dialog = new MultiOptionDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray( "options", attendanceMethods );
        dialog.setArguments( args );
        dialog.show( getSupportFragmentManager() );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK )
        {
            switch  ( requestCode )
            {
                case ADD_MEMBERS:
                    if  ( data != null )
                        updateInsert( data.getIntExtra("insert_size", 0 ) );
                break;

                case ADD_ATTENDANCE:
                    if ( data != null )
                        updateInsert( data );
            }
        }
    }

    private void updateAttendance()
    {
        getSupportActionBar().setSubtitle( "Attendee - " + service.attendance );
    }

    private void updateInsert( Intent data ) {
        Attendee[] attends = Attendee.creator.fromGsonArray(data.getStringArrayExtra("attend"));
        int first = attendees.size();
        attendees.addAll(Arrays.asList(attends));
        adapter.notifyItemRangeInserted( first, attends.length );
        list.invalidateItemDecorations();
        service.attendance = attendees.size();
        updateAttendance();
    }

    private void updateInsert(int size) {
        Call<List<Attendee>> recent = ceService.getCeApi().serviceRecentAttendees(size, service.id);
        recent.enqueue(new Callback<List<Attendee>>() {
            @Override
            public void onResponse(Call<List<Attendee>> call, Response<List<Attendee>> response) {
                if ( response.isSuccessful() )
                {
                    List<Attendee> recents = response.body();
                    int first = attendees.size();
                    attendees.addAll( recents );
                    adapter.notifyItemRangeInserted( first, size );
                    list.invalidateItemDecorations();
                    service.attendance = attendees.size();
                    update();
                }
            }

            @Override
            public void onFailure(Call<List<Attendee>> call, Throwable t) {

            }
        });
    }

    public void beginService()
    {
        ChurchService service = new ChurchService();

        Call<ChurchService> begin = ceService.getCeApi().beginService(current);
        begin.enqueue(new Callback<ChurchService>() {
            @Override
            public void onResponse(Call<ChurchService> call, Response<ChurchService> response) {
                if ( response.isSuccessful() )
                {
                    current = response.body();

                    if  ( current != null )
                    {
//                        ServiceCoordinator.this.notify( current.name );
                        Log.d( "Service_Coordinator", "Coordinating the service" );
//                        takeAttendance();
                    }
                }
                else
                {

                }
            }

            @Override
            public void onFailure(Call<ChurchService> call, Throwable t) {
                Log.d("Attendance_Activity", "ChurchService not started" );
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ( service.status.equals( States.Status.ONGOING ) )
        {
            MenuInflater inflater = new MenuInflater(this);
            inflater.inflate( R.menu.attendance_top, menu );
//            BottomAppBar bottom = findViewById(R.id.attendance_bottom_bar);
//            inflater.inflate( R.menu.attendance_bottom, bottom.getMenu() );
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
        Call<ChurchService> update = ceService.getCeApi().updateAttendance(service);
        update.enqueue(new Callback<ChurchService>() {
            @Override
            public void onResponse(Call<ChurchService> call, Response<ChurchService> response) {
                if ( response.isSuccessful() )
                {
                    ChurchService service = response.body();
                    if ( service != null )
                        updateAttendance();
                }
            }

            @Override
            public void onFailure(Call<ChurchService> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent();
        intent.putExtra( "service", service.toGson() );
        intent.putExtra( "service_index", getIntent().getIntExtra( "service_index", -1 ) );
        setResult( RESULT_OK, intent );
        return super.onKeyDown(keyCode, event);
    }

    private void close() {
        service.endTime = new LocalTime();
        Call<ChurchService> close = ceService.getCeApi().closeService(service);
        close.enqueue(new Callback<ChurchService>() {
            @Override
            public void onResponse(Call<ChurchService> call, Response<ChurchService> response) {
                if ( response.isSuccessful() )
                {
                    ChurchService service = response.body();
                    if ( service.status.equals( States.Status.CLOSED ) )
                    {
                        Intent intent = new Intent();
                        intent.putExtra( "service", service.toGson() );
                        intent.putExtra( "service_index", getIntent().getIntExtra( "service_index", -1 ) );
                        setResult( RESULT_OK, intent );
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ChurchService> call, Throwable t) {

            }
        });
    }

    public void actions(View view) {
    }

    @Override
    public void option(int position, String name) {
        switch ( position )
        {
            case 0:
                addInteract();
                break;

            case 1:
                addMembers();
                break;
        }
    }

}

