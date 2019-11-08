package com.global.tech.space.cechurchadmin.attendance;


import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.dialogs.DualOptionDialogFragment;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.CellAttendee;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchMembersActivity extends AppCompatActivity implements DualOptionDialogFragment.DualOptionListener
{
    private static final int ADD_CELL_MEMBERS = 1002;
    private static final int ADD_NEW_CELL_MEMBER = 2001;
    private static final int ADD_NEW_MEMBER = 3001;

    public static final String ATTEND_SERVICE = "attend_service";
    public static final String ATTEND_CELL_MEETING = "attend_cell_meeting";
    public static final String SELECT_MEMBERS = "select_members";
    public static final String SELECT_MEMBER_NO_CELL = "select_member";
    public static final String SELECT_MEMBERS_NO_CELL = "select_members_without_cell";

    private final int[] avatars = { R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4 };
    private final Random random = new Random();
    private List<Member> members;
    private ArrayList<Member> attendees = new ArrayList<>();
    private List<Member> membersCopy;
    private SearchAdapter adapter;
    private RecyclerView list;
    private TextInputEditText search;
    private ChipGroup attendants;
    private SparseArrayCompat< MembersViewHolder > selection = new SparseArrayCompat<>();
    private CEService ceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_register);

        init();
        initControls();
    }

    private void initControls()
    {
        initView();
        initRecyclerView();
        initSearch();
        initMisc();
    }

    private void initMisc() {
        if  ( getIntent().getAction().equals( SELECT_MEMBERS_NO_CELL ) )
            findViewById( R.id.search_add ).setVisibility( View.GONE );
    }

    private Chip addAttendants(Member member) {
        Chip memberView = (Chip) LayoutInflater.from(this).inflate(R.layout.member_check, null );
        memberView.setText( member.firstName );
        attendants.addView( memberView );

        memberView.setOnCloseIconClickListener( v -> {
            checkMember( member );
            attendants.removeView( memberView );
            adapter.notifyItemChanged( members.indexOf( member ) );
        });

        return memberView;
    }

    private void removeAttendants(Chip member ) {
        attendants.removeView( member );
    }

    private void removeAttendants() {
        attendants.removeAllViews();
    }

    private void initSearch() {
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
                adapter.getFilter().filter( query );
            }
        });
    }

    private void initView()
    {
        list = findViewById( R.id.members_list );
        search = findViewById(R.id.member_search);
        attendants = findViewById(R.id.service_attendees);
    }

    private void checkMember(Member member, MembersViewHolder holder)
    {
        if ( attendees.contains( member ) )
        {
            int index = attendees.indexOf(member);
            attendees.remove( member );
            removeAttendants( holder.entry );
            selection.remove( index );
            holder.entry = null;
        }
        else
        {
            if ( attendees.size() > 0 && getIntent().getAction().equals(SELECT_MEMBER_NO_CELL))
            {
                MembersViewHolder hold = selection.get(0);

                hold.entry = null;
                hold.setCheck( false );

                attendees.clear();
                selection.clear();
                removeAttendants();
            }
            attendees.add( member );
            selection.put( attendees.size() - 1, holder );
            holder.entry = addAttendants( member );
        }
    }

    private void checkMember(Member member)
    {
        if ( attendees.contains( member ) )
            attendees.remove( member );
        else
            attendees.add( member );
    }

    private void initRecyclerView() {
        adapter = new SearchAdapter();
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

//                if ( parent.getChildAdapterPosition( view ) == 0 )
//                    outRect.top = 20;

                if ( parent.getChildAdapterPosition( view ) == parent.getAdapter().getItemCount() - 1 )
                    outRect.bottom = 200;
            }
        });
    }

    private void init() {
        ceService = CEService.getInstance();
        processAction();
    }

    private void processAction()
    {
        String action = getIntent().getAction();
        switch ( action )
        {
            case ATTEND_SERVICE:
                getMembers( ceService.getCeApi().serviceAbsentees( getService().id ) );
                break;

            case ATTEND_CELL_MEETING:
                getMembers( ceService.getCeApi().cellMeetingsAbsentees( getCellMeeting().cellId, getCellMeeting().id ) );
                break;

            case SELECT_MEMBERS:
                getMembers( ceService.getCeApi().getMembers() );
                break;

            case SELECT_MEMBERS_NO_CELL:
            case SELECT_MEMBER_NO_CELL:
                getMembers( ceService.getCeApi().getMembersNotInCell() );
        }
    }

    private ChurchService getService()
    {
        String serviceGson = getIntent().getStringExtra("service");
        return ChurchService.creator.fromGson(serviceGson);
    }

    private CellMeeting getCellMeeting()
    {
        String cellMeeting = getIntent().getStringExtra("cell_meeting");
        return CellMeeting.creator.fromGson(cellMeeting);
    }

    private void getMembers( Call<List<Member>> membersCall ) {
        Log.d( "Attendance_Activity", "getting members from api"  );
        membersCall.enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                if ( response.isSuccessful() )
                {
                    members = response.body();
                    membersCopy = response.body();
                    if ( adapter != null )
                        adapter.notifyDataSetChanged();
                    Log.d( "Attendance_Activity", "members " + members );
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

    public void tickMembers(View view) {
        String action = getIntent().getAction();
        switch ( action )
        {
            case ATTEND_SERVICE:
                List<Attendee> attends = getService().attends(attendees);
                tickMembers( ceService.getCeApi().attendsService(attends) );
                break;

            case ATTEND_CELL_MEETING:
                List<CellAttendee> cellAttends = getCellMeeting().attends(attendees);
                tickMembers( ceService.getCeApi().attendsCellMeeting(cellAttends) );
                break;

            case SELECT_MEMBERS:
            case SELECT_MEMBER_NO_CELL:
            case SELECT_MEMBERS_NO_CELL:
                tickMembers( attendees );
        }
    }

    public void tickMembers( Call<Integer> attendsCall )
    {
        attendsCall.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                Log.d( "Search_Members", "members are " + response.body() );

                if  ( response.isSuccessful() )
                {
                    Intent data = new Intent();
                    data.putExtra( "insert_size", response.body() );
                    setResult( RESULT_OK, data );
                    finish();
                }
                else
                {
                    Log.d( "Search_Members", "members are " + response.message());
                    Log.d( "Search_Members", "members are " + response.toString());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Log.d( "Search_Members", "members are " + t.getMessage());
            }
        });
    }

    public void tickMembers( ArrayList<Member> attendees )
    {
        Intent data = new Intent();

        if ( getIntent().getAction().equals(SELECT_MEMBER_NO_CELL) )
        {
            data.putExtra( "member", attendees.get( 0 ).toGson() );
        }
        else
        {
            String members[] = new String[ attendees.size() ];
            for ( int i = 0; i < attendees.size(); ++i )
                members[ i ] = attendees.get( i ).toGson();
            data.putExtra( "members", members );
        }

        setResult( RESULT_OK, data );
        finish();
    }

    @Override
    public void option1() {
        registerCellNewMember();
    }

    @Override
    public void option2() {
        Intent intent = new Intent( this, SearchMembersActivity.class );
        intent.setAction( SearchMembersActivity.SELECT_MEMBERS_NO_CELL );
        startActivityForResult( intent, ADD_CELL_MEMBERS);
    }

    public void registerMember(View view) {
        String action = getIntent().getAction();
        if ( action.equals( ATTEND_CELL_MEETING ) )
        {
            DualOptionDialogFragment option = new DualOptionDialogFragment();
            option.show( getSupportFragmentManager() );
        }
        else
        {
            registerNewMember();
        }
    }

    private void registerCellNewMember() {
        Intent intent = new Intent( this, RegisterMemberActivity.class );
        startActivityForResult( intent, ADD_NEW_CELL_MEMBER);
    }

    private void registerNewMember() {
        Intent intent = new Intent( this, RegisterMemberActivity.class );
        startActivityForResult( intent, ADD_NEW_MEMBER );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Member member;
        if ( resultCode == RESULT_OK )
        {
            switch ( requestCode )
            {
                case ADD_NEW_CELL_MEMBER:
                    member = Member.creator.fromGson(data.getStringExtra("member"));
                    if   ( member.cellId == getCellMeeting().cellId )
                    {
                        members.add( member );
                        adapter.notifyItemInserted( members.size() - 1 );
                        list.invalidateItemDecorations();
                    }
                    break;


                case ADD_NEW_MEMBER:
                    member = Member.creator.fromGson(data.getStringExtra("member"));
                    members.add( member );
                    adapter.notifyItemInserted( members.size() - 1 );
                    list.invalidateItemDecorations();
                    break;

                case ADD_CELL_MEMBERS:
                    String[] members = data.getStringArrayExtra("members");
                    Member[] newMembers = new Member[ members.length ];
                    for ( int i = 0; i < members.length; ++i )
                    {
                        Member newMember = Member.creator.fromGson(members[i]);
                        newMember.cellId = getCellMeeting().cellId;
                        newMembers[ i ] = newMember;
                    }
                    assignCell(newMembers);
            }

        }
    }

    private void assignCell(Member[] newMembers) {
        ArrayList<Member> members = Lists.newArrayList(newMembers);
        Call<Integer> cellMembersCall = ceService.getCeApi().addCellMembers(members);
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

    private class SearchAdapter extends RecyclerView.Adapter< MembersViewHolder > implements Filterable
    {

        @NonNull
        @Override
        public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(SearchMembersActivity.this).inflate(R.layout.user_brief_card, parent, false);
            return new MembersViewHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {
            Member member = members.get(position);
            holder.name.setText( member.firstName + " " + member.surname);
            holder.number.setText( member.phoneNumber == null || member.phoneNumber.isEmpty() ? "No Phone Number" : member.phoneNumber );
            holder.photo.setImageResource( avatars[ random.nextInt( 4 ) ] );

            holder.setCheck( attendees.contains( member ) );

            holder.itemView.setOnClickListener( v -> {
                checkMember( member, holder );
                holder.setCheck( attendees.contains( member ) );
            });
        }

        @Override
        public int getItemCount() {
            if ( members == null )
                return 0;
            return members.size();
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    String query = constraint.toString();
                    if ( query.isEmpty() )
                        results.values = membersCopy;
                    else
                    {
                        List<Object> filtered = new ArrayList<>();
                        for ( Member item : membersCopy )
                            if ( item.firstName.toLowerCase().contains( query.toLowerCase() )
                                    || item.surname.toLowerCase().contains( query.toLowerCase() ) )
                                filtered.add( item );
                        results.values = filtered;
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    members = (List<Member>) results.values;
                    adapter.notifyDataSetChanged();
                }
            };
        }
    }

    private class MembersViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView number;
        private final ImageView photo;
        private final ImageView check;

        private Chip entry;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.user_name );
            number = itemView.findViewById( R.id.user_number );
            photo = itemView.findViewById(R.id.user_photo);
            check = itemView.findViewById(R.id.member_check);
        }

        private void setCheck( boolean check )
        {
            if ( check )
                this.check.setVisibility(View.VISIBLE);
            else
                this.check.setVisibility(View.GONE);

        }
    }

    public void actions(View view)
    {

    }
}

