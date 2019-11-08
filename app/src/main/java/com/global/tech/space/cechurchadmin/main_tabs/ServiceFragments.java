package com.global.tech.space.cechurchadmin.main_tabs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.MainSearchActivity;
import com.global.tech.space.cechurchadmin.attendance.AttendanceActivity;
import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.MainActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.adapters.ServiceAdapter;
import com.global.tech.space.cechurchadmin.models.ChurchService;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.global.tech.space.cechurchadmin.MainActivity.*;

public class ServiceFragments extends Fragment implements PageProvider
{
    public static final int ADD_SERVICE = 1001;
    public static final int UPDATE_SERVICE = 1002;

    private List<ChurchService> churchServices = new ArrayList<>();
    private ServiceAdapter adapter;
    private RecyclerView list;
    private MainActivity main;
    private CEService ceService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        list = (RecyclerView) inflater.inflate(R.layout.list_layout, container, false );
        init();
        initRecyclerView();
        return list;
    }

    private void initRecyclerView() {
        adapter = new ServiceAdapter( getActivity(), churchServices, UPDATE_SERVICE );
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

    private void getServices() {
        Log.d( "Attendance_Activity", "getting churchServices from api"  );
        main.startLoadAnimation();
        Call<List<ChurchService>> membersCall = ceService.getCeApi().getServices();
        membersCall.enqueue(new Callback<List<ChurchService>>() {
            @Override
            public void onResponse(Call<List<ChurchService>> call, Response<List<ChurchService>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        churchServices.clear();
                        churchServices.addAll(response.body());
                        if ( adapter != null )
                        {
                            adapter.notifyDataSetChanged();
                            main.endLoadAnimation();
                        }
                    }
                    Log.d( "Attendance_Activity", "churchServices " + churchServices);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof MainActivity )
        {
            main = ( MainActivity ) context;
        }
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void performAction() {
//        ServiceCoordinator.beginService( getContext(), getServiceName() );
        beginService();
    }

    @Override
    public String getSearchAction() {
        return MainSearchActivity.SEARCH_SERVICE;
    }

    private String getServiceName() {
        return new LocalDate().toString("EEEE" );
    }

    public void beginService()
    {
        ChurchService service = new ChurchService();
        Call<ChurchService> begin = ceService.getCeApi().beginService(service);
        begin.enqueue(new Callback<ChurchService>() {
            @Override
            public void onResponse(Call<ChurchService> call, Response<ChurchService> response) {
                if ( response.isSuccessful() )
                {
                    ChurchService newService = response.body();
                    if  ( newService != null )
                    {
                        takeAttendance( newService );
                    }
                }
                else
                {
                    Log.d( "Service_Fragment", "service response error " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<ChurchService> call, Throwable t) {
                Log.d("Service_Fragment", "Church Service not started " + t.getMessage() );
            }
        });

    }

    private void takeAttendance( ChurchService service )
    {
        Intent intent = new Intent( getContext(), AttendanceActivity.class );
        intent.putExtra( "service", service.toGson() );
        startActivityForResult( intent, ADD_SERVICE );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ChurchService service;
        switch ( requestCode )
        {
            case ADD_SERVICE:
                service = ChurchService.creator.fromGson(data.getStringExtra( "service" ));
                churchServices.add( 0, service );
                adapter.notifyItemInserted( 0 );
//                list.invalidateItemDecorations();
                break;

            case UPDATE_SERVICE:
                int index = data.getIntExtra("service_index", -1);
                service = ChurchService.creator.fromGson(data.getStringExtra( "service" ));
                if ( index != -1 )
                {
                    churchServices.remove( index );
                    churchServices.add( index, service );
                }
                adapter.notifyItemChanged( index );
        }
    }


}
