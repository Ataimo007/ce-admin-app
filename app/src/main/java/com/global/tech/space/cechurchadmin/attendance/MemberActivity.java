package com.global.tech.space.cechurchadmin.attendance;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.CEService;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.helpers.Helper;
import com.global.tech.space.cechurchadmin.models.Member;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.global.tech.space.cechurchadmin.helpers.Helper.toWordCase;

public class MemberActivity extends AppCompatActivity {

    public static final int REQUEST_MEMBER = 4001;
    private static final int UPDATE_MEMBER = 4002;
    private CEService.CEApi ceApi;
    private Member member;
    private RecyclerView list;
    private ListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        getMember();
    }

    private void getMember() {
        Call<Member> memberCall = ceApi.getMember(getIntent().getIntExtra("id", -1));
        memberCall.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if ( response.isSuccessful() )
                {
                    member = response.body();
                    initViews();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {

            }
        });
    }

    private void init() {
        ceApi = CEService.getInstance().getCeApi();
        initRecyclerView();
    }

    private void initViews() {
        initToolbar();
        initMember();
    }

    private void initMember() {
        adapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {
        list = findViewById( R.id.props_list);
        adapter = new ListAdapter();
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
            }
        });

        DividerItemDecoration divider = new DividerItemDecoration(this, layout.getOrientation());
        divider.setDrawable( getResources().getDrawable( R.drawable.member_divider, null ) );
        list.addItemDecoration( divider );
    }

    private void initToolbar() {
        TextView name = findViewById(R.id.member_name);
        CollapsingToolbarLayout toolbar = findViewById(R.id.collapse_member_name);
        String fullname = String.format( "%s %s %s",
                member.firstName != null ? toWordCase( member.firstName ) : "",
                member.surname != null ? toWordCase( member.surname ) : "",
                member.otherNames != null ? toWordCase( member.otherNames ) : ""
        );
        name.setText( fullname );

        toolbar.setCollapsedTitleTextColor(ResourcesCompat.getColor( getResources(), R.color.white, null ));
        toolbar.setTitle( fullname );
        getSupportActionBar().setTitle( fullname );
        setTitle( fullname );

    }

    public void callMember(View view) {
        if ( ( member.phoneNumber != null && !member.phoneNumber.isEmpty() ) || ( member.kingsChatNo != null && !member.kingsChatNo.isEmpty() ) )
        {
            Intent intent = new Intent( Intent.ACTION_DIAL, Uri.fromParts( "tel",
                    ( member.phoneNumber != null && !member.phoneNumber.isEmpty() ) ? member.phoneNumber : member.kingsChatNo, null ) );
            startActivity( intent );
        }
        else
        {
            String msg = "The Member Doesn't have a phone number";
            Helper.toast( MemberActivity.this, msg );
        }
    }

    public void messageMember(View view) {
        if ( ( member.phoneNumber != null && !member.phoneNumber.isEmpty() ) || ( member.kingsChatNo != null && !member.kingsChatNo.isEmpty() ) )
        {
            String number = member.phoneNumber != null && !member.phoneNumber.isEmpty() ? member.phoneNumber : member.kingsChatNo;
            Intent smsIntent = new Intent( Intent.ACTION_SEND );
            Intent chooser = Intent.createChooser(smsIntent, "Please Edit Your Content for " + member.getFullName());
            chooser.putExtra(Intent.EXTRA_TEXT, "Please Edit Your Content for " + member.getFullName() );
            startActivity( chooser );
        }
    }

    public void callMember(String number) {
        if ( number != null && !number.isEmpty() )
        {
            Intent intent = new Intent( Intent.ACTION_DIAL, Uri.fromParts( "tel", number, null ) );
            startActivity( intent );
        }
        else
        {
            String msg = "The Member Doesn't have a phone number";
            Helper.toast( MemberActivity.this, msg );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK )
        {
            if ( requestCode == UPDATE_MEMBER )
            {
                member = Member.creator.fromGson( data.getStringExtra("member" ) );
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void editMember(View view) {
        Intent intent = new Intent( this, RegisterMemberActivity.class );
        intent.setAction( RegisterMemberActivity.EDIT_MEMBER );
        intent.putExtra( "member", member.toGson() );
        startActivityForResult( intent, UPDATE_MEMBER );
    }

    public void chatMember(View view) {
    }

    private class ListAdapter extends RecyclerView.Adapter<InfoHolder>
    {
        private static final int propCount = 10;

        @NonNull
        @Override
        public InfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MemberActivity.this).inflate(R.layout.prop_info, parent, false);
            return new InfoHolder( view );
        }

        @Override
        public void onBindViewHolder(@NonNull InfoHolder holder, int position) {
            switch ( position )
            {
                case 0:
                    holder.name.setText( "First Name" );
                    holder.value.setText( member.firstName != null ? toWordCase( member.firstName ) : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_person_black_24dp );
                    break;

                case 1:
                    holder.name.setText( "Surname" );
                    holder.value.setText( member.surname != null ? toWordCase( member.surname ) : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_person_black_24dp );
                    break;

                case 2:
                    holder.name.setText( "Other Names" );
                    holder.value.setText( member.otherNames != null ? toWordCase( member.otherNames ) : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_person_black_24dp );
                    break;

                case 3:
                    holder.name.setText( "Gender" );
                    holder.value.setText( member.gender != null ? toWordCase( member.gender.name() ) : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_info_black_24dp );
                    break;

                case 4:
                    holder.name.setText( "Phone Number" );
                    holder.value.setText( member.phoneNumber != null ? member.phoneNumber  : "Don't Have Phone" );
                    holder.icon.setImageResource( R.drawable.ic_phone_black_24dp );

                    holder.itemView.setOnClickListener(v -> {
                        callMember( member.phoneNumber );
                    });
                    break;

                case 5:
                    holder.name.setText( "Kings Chat Phone Number" );
                    holder.value.setText( member.kingsChatNo != null ? member.kingsChatNo  : "Don't Have Kings Chat" );
                    holder.icon.setImageResource( R.drawable.ic_phone_gold_24dp );

                    holder.itemView.setOnClickListener(v -> {
                        callMember( member.kingsChatNo );
                    });
                    break;

                case 6:
                    holder.name.setText( "Email Address" );
                    holder.value.setText( member.emailAddress != null ? member.emailAddress  : "Don't Have Email" );
                    holder.icon.setImageResource( R.drawable.ic_email_black_24dp );
                    break;

                case 7:
                    holder.name.setText( "Date Of Birth" );
                    holder.value.setText( member.dateOfBirth != null ? member.dateOfBirth.toString() : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_date_range_black_24dp );
                    break;

                case 8:
                    holder.name.setText( "Home Address" );
                    holder.value.setText( member.homeAddress != null ? toWordCase( member.homeAddress )  : "Not Assigned Yet" );
                    holder.icon.setImageResource( R.drawable.ic_location_on_black_24dp );
                    break;

                case 9:
                    holder.name.setText( "Cell" );
                    holder.value.setText( member.cell != null ? toWordCase( member.cell.name ) : "Doesn't Belong to a Cell");
                    holder.icon.setImageResource( R.drawable.ic_info_black_24dp );
            }
        }

        @Override
        public int getItemCount() {
            if  ( member != null )
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
