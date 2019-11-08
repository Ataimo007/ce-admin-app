package com.global.tech.space.cechurchadmin.attendance;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Enums;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnalysisActivity extends AppCompatActivity {

    private List< Member > items = new ArrayList<>();
    private final List< Member > copy = items;
    private final EnumSet<Member.Rank> filters = EnumSet.noneOf(Member.Rank.class);

    private RecyclerView list;
    private MemberAdapter adapter;
    private CEService.CEApi ceApi;
    private TextInputEditText search;
    private Filterable filter;
    private ChurchService service;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_analysis);

        init();
        initViews();
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
        service = ChurchService.creator.fromGson( getIntent().getStringExtra("service") );
        getMembers();
    }

    private void initViews() {
        initRecyclerView();
        initFilter();
        initSearch();
        initCheckers();
    }

    private void initCheckers() {
        ChipGroup group = findViewById(R.id.attendance_filters);
        for ( Member.Rank rank : Member.Rank.values() )
        {
            Chip filter = (Chip) LayoutInflater.from(this).inflate(R.layout.attendance_filter, null );
            filter.setText( rank.name );
            group.addView( filter );

            filter.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if ( isChecked )
                    filters.add( rank );
                else
                    filters.remove( rank );
                filter();
            });
        }
    }

    private void filter()
    {
        List<Member> filtered;
        if ( filters.isEmpty() )
            filtered = copy;
        else
        {
            filtered = new ArrayList<>();
            for ( Member member : copy )
            {
                if ( member.rank != null && filters.contains( member.rank ) )
                    filtered.add( member );
            }
        }

        adapter.setCurrent( filtered );

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
        adapter = new MemberAdapter( this, items );
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

                if ( parent.getChildAdapterPosition( view ) == 0 )
                    outRect.top = 5;

                if ( parent.getChildAdapterPosition( view ) == parent.getAdapter().getItemCount() - 1 )
                    outRect.bottom = 200;
            }
        });
    }

    private void getMembers() {

        Call<List<Member>> memberCall = ceApi.serviceAbsentees( service.id );
        memberCall.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if ( response.isSuccessful() )
                {
                    List<Member> body = response.body();
                    if ( body != null )
                    {
                        items.clear();
                        items.addAll( body );
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {

            }
        });
    }

}
