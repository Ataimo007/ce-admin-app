package com.global.tech.space.cechurchadmin.cell_tabs;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.CellActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.CellAttendanceActivity;
import com.global.tech.space.cechurchadmin.adapters.CellMeetingAdapter;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingFragment extends Fragment implements CellActivity.PageProvider
{
    public static final int ADD_MEETING = 1001;
    public static final int UPDATE_MEETING = 1001;

    public static final int REQUEST_MEMBER = 4001;
    private static final int ADD_CELL = 5001;
    private CEService.CEApi ceApi;
    private List<CellMeeting> meetings = new ArrayList<>();
    private RecyclerView list;
    private CellMeetingAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getMembers();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View props = inflater.inflate(R.layout.card_props, container, false);
        list = props.findViewById(R.id.props_list);
        initRecyclerView();
        return props;
    }

    @Override
    public void setupPage(AppCompatActivity activity) {
        FloatingActionButton floatButton = activity.findViewById(R.id.cell_action);
        floatButton.setImageResource( R.drawable.ic_white_add_24dp);
    }

    private void getMembers() {
        Call<List<CellMeeting>> memberCall = ceApi.getCellMeetings(getArguments().getInt("id" ));
        memberCall.enqueue(new Callback<List<CellMeeting>>() {
            @Override
            public void onResponse(Call<List<CellMeeting>> call, Response<List<CellMeeting>> response) {
                if ( response.isSuccessful() )
                {
                    meetings.clear();
                    meetings.addAll( response.body() );
                    initViews();
                }
            }

            @Override
            public void onFailure(Call<List<CellMeeting>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CellMeeting cellMeeting;
        switch ( requestCode )
        {
            case ADD_MEETING:
                cellMeeting = CellMeeting.creator.fromGson(data.getStringExtra( "cell_meeting" ));
                meetings.add( cellMeeting );
                adapter.notifyItemInserted( meetings.size() - 1 );
                list.invalidateItemDecorations();
                break;
        }
    }

    public void beginMeeting()
    {
        CellMeeting meeting = new CellMeeting( getArguments().getInt("id" ) );
        Call<CellMeeting> begin = ceApi.beginCellMeeting(meeting);
        begin.enqueue(new Callback<CellMeeting>() {
            @Override
            public void onResponse(Call<CellMeeting> call, Response<CellMeeting> response) {
                if ( response.isSuccessful() )
                {
                    CellMeeting newMeeting = response.body();
                    if  ( newMeeting != null )
                    {
                        Cell cell = Cell.creator.fromGson( getArguments().getString( "cell" ) );
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
        Intent intent = new Intent( getContext(), CellAttendanceActivity.class );
        intent.putExtra( "cell_meeting", service.toGson() );
        startActivityForResult( intent, ADD_CELL );
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
    }

    private void initViews() {
        initMember();
    }

    private void initMember() {
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        adapter = new CellMeetingAdapter( this.getActivity(), meetings, UPDATE_MEETING);
        LinearLayoutManager layout = new LinearLayoutManager( getContext() );
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

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void performAction() {
        beginMeeting();
    }
}
