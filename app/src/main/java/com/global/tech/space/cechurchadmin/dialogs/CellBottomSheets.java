package com.global.tech.space.cechurchadmin.dialogs;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CellBottomSheets extends BottomSheetDialogFragment
{
    public final static String tag = "CELL_DIALOG_SHEET";
    private List<Cell> infos;
    private RecyclerView list;
    private CellSellectionHandler handler;

    public void show(FragmentManager manager)
    {
        show( manager, tag );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CEService.CEApi ceApi = CEService.getInstance().getCeApi();
        Call<List<Cell>> cells = ceApi.getCells();
        cells.enqueue(new Callback<List<Cell>>() {
            @Override
            public void onResponse(Call<List<Cell>> call, Response<List<Cell>> response) {
                if ( response.isSuccessful() )
                {
                    infos = response.body();
                    adapt();
                }
            }

            @Override
            public void onFailure(Call<List<Cell>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof CellSellectionHandler )
            handler = (CellSellectionHandler) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cell_list, container, false);
        list = view.findViewById( R.id.cell_list );
        list.setLayoutManager( new LinearLayoutManager( this.getContext()));

        list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 3;
                outRect.bottom = 3;

//                if ( parent.getChildAdapterPosition( view ) == 0 )
//                    outRect.top = 20;

                if ( parent.getChildAdapterPosition( view ) == parent.getAdapter().getItemCount() - 1 )
                    outRect.bottom = 20;
            }
        });
        return view;
    }

    private void adapt()
    {
        list.setAdapter( new CellAdapter() );
    }


    private class CellAdapter extends RecyclerView.Adapter< CellHolder >
    {

        @NonNull
        @Override
        public CellHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View info = getLayoutInflater().inflate(R.layout.cell_info, parent, false);
            return new CellHolder( info );
        }

        @Override
        public void onBindViewHolder(@NonNull CellHolder holder, int position) {
            Cell info = infos.get(position);
            holder.name.setText( info.name );
            holder.leader.setText( String.format( Locale.ENGLISH, "%s %s", info.leader.firstName, info.leader.surname) );

            holder.itemView.setOnClickListener( v -> {
                CellBottomSheets.this.dismiss();
                if ( handler != null )
                    handler.onSelectCell( info );
            });
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }
    }

    private class CellHolder extends RecyclerView.ViewHolder
    {
        private final TextView name;
        private final TextView leader;

        public CellHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cell_name);
            leader = itemView.findViewById(R.id.cell_leader);
        }
    }

    public static interface CellSellectionHandler
    {
        void onSelectCell( Cell info );
    }
}
