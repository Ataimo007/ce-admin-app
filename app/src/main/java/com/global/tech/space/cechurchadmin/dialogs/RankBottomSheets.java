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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankBottomSheets extends BottomSheetDialogFragment
{
    public final static String tag = "RANK_DIALOG_SHEET";
    private Member.Rank[] infos = Member.Rank.values();
    private RecyclerView list;
    private RankSellectionHandler handler;

    public void show(FragmentManager manager)
    {
        show( manager, tag );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof RankSellectionHandler )
            handler = (RankSellectionHandler) context;
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
        adapt();
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
            View info = getLayoutInflater().inflate(R.layout.rank_info, parent, false);
            return new CellHolder( info );
        }

        @Override
        public void onBindViewHolder(@NonNull CellHolder holder, int position) {
            Member.Rank info = infos[position];
            holder.name.setText( info.name );

            holder.itemView.setOnClickListener( v -> {
                RankBottomSheets.this.dismiss();
                if ( handler != null )
                    handler.onSelectRank( info );
            });
        }

        @Override
        public int getItemCount() {
            return infos.length;
        }
    }

    private class CellHolder extends RecyclerView.ViewHolder
    {
        private final TextView name;

        public CellHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.option_name);
        }
    }

    public static interface RankSellectionHandler
    {
        void onSelectRank(Member.Rank info);
    }
}
