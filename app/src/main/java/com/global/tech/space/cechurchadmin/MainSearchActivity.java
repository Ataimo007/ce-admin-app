package com.global.tech.space.cechurchadmin;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.adapters.CellAdapter;
import com.global.tech.space.cechurchadmin.adapters.CellMeetingAdapter;
import com.global.tech.space.cechurchadmin.adapters.CellWeeksAdapter;
import com.global.tech.space.cechurchadmin.adapters.MemberAdapter;
import com.global.tech.space.cechurchadmin.adapters.ServiceAdapter;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.CellWeekInfo;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainSearchActivity< T > extends AppCompatActivity {

    public static final String SEARCH_SERVICE = "search_service";
    public static final String SEARCH_CELL_WEEKS = "search_cell_weeks";
    public static final String SEARCH_CELL_MEETINGS = "search_cell_meetings";
    public static final String SEARCH_CELL = "search_cell";
    public static final String SEARCH_MEMBERS = "search_members";

    private List<T> items = new ArrayList<>();
    private RecyclerView list;
    private RecyclerView.Adapter adapter;
    private CEService.CEApi ceApi;
    private TextInputEditText search;
    private Filterable filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);

        init();
        initViews();
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
        getEntities();
    }

    private void initViews() {
        initRecyclerView();
        initFilter();
        initSearch();
    }

    private void initFilter() {
        if (adapter instanceof Filterable)
            filter = ( Filterable ) adapter;
    }

    private void initSearch() {
        search = findViewById( R.id.member_search );
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                if ( filter != null )
                    filter.getFilter().filter( query );
            }
        });
    }

    private void initRecyclerView() {
        list = findViewById(R.id.main_list);
        adapter = getAdapter();
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
            }
        });
    }

    private RecyclerView.Adapter getAdapter()
    {
        String action = getIntent().getAction();
        switch ( action )
        {
            case SEARCH_MEMBERS:
                return new MemberAdapter( this, (List<Member>) items );

            case SEARCH_CELL:
                return new CellAdapter( this, (List<Cell>) items );

            case SEARCH_CELL_WEEKS:
                return new CellWeeksAdapter( this, (List<CellWeekInfo>) items );

            case SEARCH_CELL_MEETINGS:
                return new CellMeetingAdapter( this, (List<CellMeeting>) items );

            case SEARCH_SERVICE:
                return new ServiceAdapter( this, (List<ChurchService>) items );
        }
        return null;
    }

    private void getEntities()
    {
        String action = getIntent().getAction();
        switch ( action )
        {
            case SEARCH_MEMBERS:
                getMembers();
                break;

            case SEARCH_CELL:
                getCells();
                break;

            case SEARCH_CELL_WEEKS:
                getCellWeeks();
                break;

            case SEARCH_CELL_MEETINGS:
                getWeekMeetings();
                break;

            case SEARCH_SERVICE:
                getServices();
        }
    }

    private void getWeekMeetings() {
        CellWeekInfo info = CellWeekInfo.creator.fromGson(getIntent().getStringExtra("week_meetings"));
        Log.d( "Attendance_Activity", "getting churchServices from api"  );
        Call<List<CellMeeting>> attendeesCall = ceApi.getCellMeetingsOfWeek( info.week );
        attendeesCall.enqueue(new Callback<List<CellMeeting>>() {
            @Override
            public void onResponse(Call<List<CellMeeting>> call, Response<List<CellMeeting>> response) {
                if ( response.isSuccessful() )
                {
                    items.clear();
                    items.addAll((Collection<? extends T>) response.body());
                    if ( adapter != null )
                    {
                        adapter.notifyDataSetChanged();
                    }
                    Log.d( "Attendance_Activity", "churchServices " + items);
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

    private void getCellWeeks() {
        Call<List<CellWeekInfo>> membersCall = ceApi.getCellMeetingsWeek();
        membersCall.enqueue(new Callback<List<CellWeekInfo>>() {
            @Override
            public void onResponse(Call<List<CellWeekInfo>> call, Response<List<CellWeekInfo>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        items.addAll((Collection<T>) response.body());
                        if ( adapter != null )
                        {
                            adapter.notifyDataSetChanged();
//                            initWeekIndex();
                        }
                    }
                    Log.d( "Attendance_Activity", "cellWeekInfos " + items);
                }
                else {
                    Log.d( "Attendance_Activity", "cellWeekInfos unsuccessful " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<List<CellWeekInfo>> call, Throwable t) {
                Log.d( "Attendance_Activity", "cellWeekInfos failed " + t.getMessage() );
            }
        });
    }

    private void getServices() {
        Call<List<ChurchService>> membersCall = ceApi.getServices();
        membersCall.enqueue(new Callback<List<ChurchService>>() {
            @Override
            public void onResponse(Call<List<ChurchService>> call, Response<List<ChurchService>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        items.addAll((Collection<? extends T>) response.body());
                        if ( adapter != null )
                        {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    Log.d( "Attendance_Activity", "churchServices " + items);
                }
                else {
                    Log.d( "Attendance_Activity", "churchServices unsuccessful " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<List<ChurchService>> call, Throwable t) {
                Log.d( "Attendance_Activity", "churchServices failed " + t.getMessage() );
            }
        });
    }

    private void getMembers() {
        Call<List<Member>> memberCall = ceApi.getMembers();
        memberCall.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if ( response.isSuccessful() )
                {
                    items.clear();
                    items.addAll((Collection<? extends T>) response.body());
                    if ( adapter != null )
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {

            }
        });
    }

    private void getCells() {
        Log.d( "Attendance_Activity", "getting cells from api"  );
        CEService ceService = CEService.getInstance();
        Call<List<Cell>> membersCall = ceService.getCeApi().getCells();
        membersCall.enqueue(new Callback<List<Cell>>() {
            @Override
            public void onResponse(Call<List<Cell>> call, Response<List<Cell>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        items.addAll((Collection<? extends T>) response.body());
                        if ( adapter != null )
                            adapter.notifyDataSetChanged();
                    }
                    Log.d( "Attendance_Activity", "cells " + items);
                }
                else {
                    Log.d( "Attendance_Activity", "cells unsuccessful " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<List<Cell>> call, Throwable t) {
                Log.d( "Attendance_Activity", "cells failed " + t.getMessage() );
            }
        });
    }
}
