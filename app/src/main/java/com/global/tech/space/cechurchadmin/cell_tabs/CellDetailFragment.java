package com.global.tech.space.cechurchadmin.cell_tabs;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.CellActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.MemberActivity;
import com.global.tech.space.cechurchadmin.models.Cell;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.global.tech.space.cechurchadmin.helpers.Helper.toWordCase;

public class CellDetailFragment extends Fragment implements CellActivity.PageProvider
{

    public static final int REQUEST_MEMBER = 4001;
    private CEService.CEApi ceApi;
    private Cell cell;
    private RecyclerView list;
    private ListAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        getInfo();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View props = inflater.inflate(R.layout.card_props, container, false);
        list = props.findViewById(R.id.props_list);
        initRecyclerView();
        return props;
    }

    private void getInfo() {
        Call<Cell> memberCall = ceApi.getCell(getArguments().getInt("id" ));
        memberCall.enqueue(new Callback<Cell>() {
            @Override
            public void onResponse(Call<Cell> call, Response<Cell> response) {
                if ( response.isSuccessful() )
                {
                    cell = response.body();
                    initViews();
                }
            }

            @Override
            public void onFailure(Call<Cell> call, Throwable t) {

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
        adapter = new ListAdapter();
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

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), layout.getOrientation());
        divider.setDrawable( getResources().getDrawable( R.drawable.member_divider, null ) );
        list.addItemDecoration( divider );

    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void performAction() {

    }

    @Override
    public void setupPage(AppCompatActivity activity) {
        FloatingActionButton floatButton = activity.findViewById(R.id.cell_action);
        floatButton.setImageResource( R.drawable.ic_edit_black_24dp);
    }

    private class ListAdapter extends RecyclerView.Adapter<InfoHolder>
    {
        private static final int propCount = 6;

        @NonNull
        @Override
        public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CellDetailFragment.this.getContext()).inflate(R.layout.prop_info, parent, false);
            return new InfoHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull InfoHolder holder, int position) {
            switch ( position )
            {
                case 0:
                    holder.name.setText( "Cell Name" );
                    holder.value.setText( cell.name != null ? toWordCase( cell.name ) : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_info_black_24dp );
                    break;

                case 1:
                    holder.name.setText( "Leader" );
                    holder.value.setText( cell.leader.getFullName() );
                    holder.icon.setImageResource( R.drawable.ic_person_black_24dp );

                    holder.itemView.setOnClickListener( v -> {
                        Intent intent = new Intent( getContext(), MemberActivity.class );
                        intent.putExtra( "id", cell.leader.id );
                        startActivityForResult( intent, MemberActivity.REQUEST_MEMBER );
                    });
                    break;

                case 2:
                    holder.name.setText( "Assistant" );
                    holder.value.setText( cell.assistant != null ? cell.assistant.getFullName() : "Doesn't Have Assistant Yet" );
                    holder.icon.setImageResource( R.drawable.ic_person_black_24dp );

                    if  ( cell.assistant != null )
                    {
                        holder.itemView.setOnClickListener( v -> {
                            Intent intent = new Intent( getContext(), MemberActivity.class );
                            intent.putExtra( "id", cell.assistant.id );
                            startActivityForResult( intent, MemberActivity.REQUEST_MEMBER );
                        });
                    }
                    break;

                case 3:
                    holder.name.setText( "Membership Strength" );
                    holder.value.setText( String.valueOf( cell.membershipStrength ) );
                    holder.icon.setImageResource( R.drawable.ic_info_black_24dp );
                    break;

                case 4:
                    holder.name.setText( "Location of Meetings" );
                    holder.value.setText( cell.venue != null ? cell.venue  : "Don't Have Phone" );
                    holder.icon.setImageResource( R.drawable.ic_home_black_24dp );
                    break;

                case 5:
                    holder.name.setText( "Subject or Interest of Reach" );
                    holder.value.setText( cell.subject != null ? cell.subject  : "Don't Have Kings Chat" );
                    holder.icon.setImageResource( R.drawable.ic_info_black_24dp );
                    break;

            }
        }

        @Override
        public int getItemCount() {
            if  ( cell != null )
                return propCount;
            else
                return 0;
        }


    }

    private class InfoHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView value;
        private final ImageView icon;

        public InfoHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.prop_name );
            value = itemView.findViewById( R.id.prop_value );
            icon = itemView.findViewById(R.id.prop_icon);
        }
    }
}
