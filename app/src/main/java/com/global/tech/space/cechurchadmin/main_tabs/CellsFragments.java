package com.global.tech.space.cechurchadmin.main_tabs;

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

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.CellActivity;
import com.global.tech.space.cechurchadmin.MainSearchActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.RegisterCellActivity;
import com.global.tech.space.cechurchadmin.adapters.CellAdapter;
import com.global.tech.space.cechurchadmin.models.Cell;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.global.tech.space.cechurchadmin.MainActivity.*;

public class CellsFragments extends Fragment implements PageProvider
{
    private static final int REGISTER_CELL = 1001;
    public static final int UPDATE_CELL = 1002;

    private CellAdapter adapter;
    private RecyclerView list;
    private List<Cell> cells = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        list = (RecyclerView) inflater.inflate(R.layout.list_layout, container, false);
        init();
        initRecyclerView();
        return list;
    }

    private void initRecyclerView() {
        adapter = new CellAdapter( getActivity(), cells, UPDATE_CELL );
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
        getServices();
    }

    private void getServices() {
        Log.d( "Attendance_Activity", "getting cells from api"  );
        CEService ceService = CEService.getInstance();
        Call<List<Cell>> membersCall = ceService.getCeApi().getCells();
        membersCall.enqueue(new Callback<List<Cell>>() {
            @Override
            public void onResponse(Call<List<Cell>> call, Response<List<Cell>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        cells.clear();
                        cells.addAll( response.body() );
                        if ( adapter != null )
                            adapter.notifyDataSetChanged();
                    }
                    Log.d( "Attendance_Activity", "cells " + cells);
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

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void performAction() {
        Intent intent = new Intent( getContext(), RegisterCellActivity.class );
        startActivityForResult( intent, REGISTER_CELL );
    }

    @Override
    public String getSearchAction() {
        return MainSearchActivity.SEARCH_CELL;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK )
        {
            switch (requestCode)
            {
                case REGISTER_CELL:
                    Cell cell = Cell.creator.fromGson( data.getStringExtra("cell" ) );

                    cells.add( cell );
                    adapter.notifyItemInserted( cells.size() -1 );
                    list.invalidateItemDecorations();

                    Intent intent = new Intent( getContext(), CellActivity.class );
                    intent.setAction( CellActivity.NEW_CELL );
                    intent.putExtra( "id", cell.id );
                    startActivityForResult( intent, UPDATE_CELL );
                    break;
            }
        }
    }
}
