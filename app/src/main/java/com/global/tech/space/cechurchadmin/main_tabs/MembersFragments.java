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

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.MainActivity;
import com.global.tech.space.cechurchadmin.MainSearchActivity;
import com.global.tech.space.cechurchadmin.attendance.RegisterMemberActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.adapters.MemberAdapter;
import com.global.tech.space.cechurchadmin.models.Member;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.global.tech.space.cechurchadmin.MainActivity.*;

public class MembersFragments extends Fragment implements PageProvider
{
    private static final int ADD_MEMBER = 1001;
    private List<Member> members = new ArrayList<>();
    private MemberAdapter adapter;
    private RecyclerView list;
    private MainActivity main;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        list = (RecyclerView) inflater.inflate(R.layout.list_layout, container, false );
        init();
        initRecyclerView();
        return list;
    }

    private void initRecyclerView() {
        adapter = new MemberAdapter( this.getActivity(), members );
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
        getMembers();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof MainActivity)
        {
            main = ( MainActivity ) context;
        }
    }

    private void getMembers() {
        Log.d( "Attendance_Activity", "getting members from api"  );
        main.startLoadAnimation();
        CEService ceService = CEService.getInstance();
        Call<List<Member>> membersCall = ceService.getCeApi().getMembers();
        membersCall.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if ( response.isSuccessful() )
                {
                    if (response.body() != null) {
                        members.clear();
                        members.addAll(response.body());
                        if ( adapter != null )
                        {
                            adapter.notifyDataSetChanged();
                            main.endLoadAnimation();
                        }
                    }
                    Log.d( "Attendance_Activity", "members " + members);
                }
                else {
                    Log.d( "Attendance_Activity", "members unsuccessful " + response.message() );
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {
                Log.d( "Attendance_Activity", "members failed " + t.getMessage() );
            }
        });
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void performAction() {
        Intent intent = new Intent( getContext(), RegisterMemberActivity.class );
        startActivityForResult( intent, ADD_MEMBER );
    }

    @Override
    public String getSearchAction() {
        return MainSearchActivity.SEARCH_MEMBERS;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == ADD_MEMBER )
        {
            if ( resultCode == RESULT_OK )
            {
                Member member = Member.creator.fromGson(data.getStringExtra("member"));
                members.add( member );
                adapter.notifyItemInserted( members.size() - 1 );
            }
        }
    }


}
