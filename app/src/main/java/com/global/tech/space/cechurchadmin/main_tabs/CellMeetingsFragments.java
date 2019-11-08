package com.global.tech.space.cechurchadmin.main_tabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.MainActivity;
import com.global.tech.space.cechurchadmin.MainSearchActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.CellAttendanceActivity;
import com.global.tech.space.cechurchadmin.dialogs.CellDialogFragment;
import com.global.tech.space.cechurchadmin.adapters.CellWeeksAdapter;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.CellWeekInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.global.tech.space.cechurchadmin.MainActivity.*;

public class CellMeetingsFragments extends Fragment implements PageProvider, CellDialogFragment.CellDialogListener
{
    private static final int UPDATE_WEEK = 1001;

    private final Random random = new Random();
    private final int[] avatars = { R.drawable.avatars_1, R.drawable.avatars_2, R.drawable.avatars_3 };
    private List<CellWeekInfo> cellWeekInfos = new ArrayList<>();
    private CellWeeksAdapter adapter;
    private RecyclerView list;
    private MainActivity main;
    private CEService ceService;
    private SparseIntArray weekIndex;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        list = (RecyclerView) inflater.inflate(R.layout.list_layout, container, false );
        init();
        initRecyclerView();
        return list;
    }

    private void initRecyclerView() {
        adapter = new CellWeeksAdapter( this.getActivity(), cellWeekInfos, UPDATE_WEEK );
        LinearLayoutManager layout = new LinearLayoutManager( getContext() );
        list.setLayoutManager( layout );
        list.setAdapter(adapter);

        list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 5;
                outRect.bottom = 5;
                outRect.left = 5;
                outRect.right = 5;

                if ( parent.getChildAdapterPosition( view ) == 0 )
                    outRect.top = 5;

                if ( parent.getChildAdapterPosition( view ) == parent.getAdapter().getItemCount() - 1 )
                    outRect.bottom = 200;
            }
        });
    }

    private void init() {
        ceService = CEService.getInstance();
        getServices();
    }

    private void initWeekIndex() {
        weekIndex = new SparseIntArray();
        for ( int i = 0; i < cellWeekInfos.size(); ++i )
        {
            CellWeekInfo info = cellWeekInfos.get( i );
            weekIndex.put( info.week, i );
        }
    }

    private void getServices() {
        Log.d( "Attendance_Activity", "getting cellWeekInfos from api"  );
        main.startLoadAnimation();
        Call<List<CellWeekInfo>> membersCall = ceService.getCeApi().getCellMeetingsWeek();
        membersCall.enqueue(new Callback<List<CellWeekInfo>>() {
            @Override
            public void onResponse(Call<List<CellWeekInfo>> call, Response<List<CellWeekInfo>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        cellWeekInfos.clear();
                        cellWeekInfos.addAll( response.body() );
                        if ( adapter != null )
                        {
                            adapter.notifyDataSetChanged();
                            initWeekIndex();
                            main.endLoadAnimation();
                        }
                    }
                    Log.d( "Attendance_Activity", "cellWeekInfos " + cellWeekInfos);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof MainActivity )
        {
            main = ( MainActivity ) context;
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if  ( childFragment instanceof CellDialogFragment )
        {
            CellDialogFragment fragment = (CellDialogFragment) childFragment;
            fragment.setListener(this);
        }
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void performAction() {
        beginMeeting();
    }

    @Override
    public String getSearchAction() {
        return MainSearchActivity.SEARCH_CELL_WEEKS;
    }

    public void beginMeeting()
    {
        CellDialogFragment cellDialog = new CellDialogFragment();
        cellDialog.show( getChildFragmentManager() );
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
        Intent intent = new Intent( getContext(), CellAttendanceActivity.class );
        intent.putExtra( "cell_meeting", service.toGson() );
        startActivityForResult( intent, UPDATE_WEEK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d( "Activity_Result", "The data is " + data.getExtras() );
        Log.d( "Activity_Result", "The cell meeting is " + data.getStringExtra("cell_meeting") );
        Log.d( "Activity_Result", "The week meeting is " + data.getStringExtra("week_meetings") );

        switch ( requestCode )
        {
            case UPDATE_WEEK:
                if ( data.hasExtra( "week_meetings") )
                {
                    CellWeekInfo week = CellWeekInfo.creator.fromGson(data.getStringExtra("week_meetings"));
                    int position = weekIndex.get(week.week);
                    cellWeekInfos.remove( position );
                    cellWeekInfos.add( position, week );
                    adapter.notifyItemChanged( position );
                }

                if ( data.hasExtra( "cell_meeting") )
                {
                    CellMeeting meeting = CellMeeting.creator.fromGson(data.getStringExtra("cell_meeting"));
                    int position = weekIndex.indexOfKey(meeting.week);
                    if ( position < 0 )
                    {
                        CellWeekInfo newWeek = new CellWeekInfo(meeting.week, 1);
                        cellWeekInfos.add( 0, newWeek );
                        adapter.notifyItemInserted( 0 );
                    }
                    else
                    {
                        int index = weekIndex.get(weekIndex.keyAt(position));
                        CellWeekInfo cellWeekInfo = cellWeekInfos.get(index);
                        ++cellWeekInfo.meetingsHeld;
                        adapter.notifyItemChanged( index );
                    }
                }
        }
    }
}
