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
import com.global.tech.space.cechurchadmin.attendance.RegisterMemberActivity;
import com.global.tech.space.cechurchadmin.attendance.SearchMembersActivity;
import com.global.tech.space.cechurchadmin.dialogs.DualOptionDialogFragment;
import com.global.tech.space.cechurchadmin.adapters.MemberAdapter;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class MemberFragment extends Fragment implements CellActivity.PageProvider, DualOptionDialogFragment.DualOptionListener
{

    public static final int REQUEST_MEMBER = 4001;
    private static final int ADD_MEMBERS = 5001;
    private static final int ADD_NEW_CELL_MEMBER = 1001;
    private static final int ADD_CELL_MEMBERS = 2001;


    private CEService.CEApi ceApi;
    private List<Member> members = new ArrayList<>();
    private RecyclerView list;
    private MemberAdapter adapter;

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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK )
        {
            switch ( requestCode )
            {
                case ADD_MEMBERS:
                    String[] members = data.getStringArrayExtra("members");
                    Member[] newMembers = new Member[ members.length ];
                    for ( int i = 0; i < members.length; ++i )
                    {
                        Member newMember = Member.creator.fromGson(members[i]);
                        newMember.cellId = getArguments().getInt("id" );
                        newMembers[ i ] = newMember;
                    }
                    assignCell(newMembers);
                    break;

                case ADD_NEW_CELL_MEMBER:
                    Member member = Member.creator.fromGson(data.getStringExtra("member"));
                    if   ( member.cellId == getArguments().getInt("id" ) )
                    {
                        this.members.add(member);
                        adapter.notifyItemInserted( this.members.size() - 1 );
                        list.invalidateItemDecorations();
                    }
                    break;
            }

        }
    }

    private void assignCell(Member[] newMembers) {
        ArrayList<Member> members = Lists.newArrayList(newMembers);
        Call<Integer> cellMembersCall = ceApi.addCellMembers(members);
        List<Member> list = this.members;
        RecyclerView listView = this.list;
        cellMembersCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if  ( response.isSuccessful() )
                {
                    Integer size = response.body();
                    if ( size == members.size() )
                    {
                        list.addAll(members);
                        adapter.notifyItemRangeInserted( list.size() - 1, newMembers.length );
                        listView.invalidateItemDecorations();
                    }
                }
                else
                {
                    Log.d( "Search_Members", "members are " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d( "Search_Members", "members are " + t.getMessage());
            }
        });
    }

    @Override
    public void setupPage(AppCompatActivity activity) {
        FloatingActionButton floatButton = activity.findViewById(R.id.cell_action);
        floatButton.setImageResource( R.drawable.ic_white_add_24dp);
    }

    private void getMembers() {
        Call<List<Member>> memberCall = ceApi.getCellMembers(getArguments().getInt("id" ));
        memberCall.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if ( response.isSuccessful() )
                {
                    members.clear();
                    members.addAll( response.body() );
                    initViews();
                }
            }

            @Override
            public void onFailure(Call<List<Member>> call, Throwable t) {

            }
        });
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
//        Intent intent = new Intent( getContext(), SearchMembersActivity.class );
//        intent.setAction( SearchMembersActivity.SELECT_MEMBERS_NO_CELL );
//        startActivityForResult( intent, ADD_MEMBERS  );

        DualOptionDialogFragment option = new DualOptionDialogFragment();
        option.show( getChildFragmentManager() );
    }

    @Override
    public void option1() {
        Intent intent = new Intent( getContext(), RegisterMemberActivity.class );
        startActivityForResult( intent, ADD_NEW_CELL_MEMBER);
    }

    @Override
    public void option2() {
        Intent intent = new Intent( getContext(), SearchMembersActivity.class );
        intent.setAction( SearchMembersActivity.SELECT_MEMBERS_NO_CELL );
        startActivityForResult( intent, ADD_CELL_MEMBERS);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
        if ( childFragment instanceof DualOptionDialogFragment )
        {
            DualOptionDialogFragment options = (DualOptionDialogFragment) childFragment;
            options.setListener( this );
        }
    }
}
