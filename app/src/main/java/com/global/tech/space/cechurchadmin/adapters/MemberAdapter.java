package com.global.tech.space.cechurchadmin.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.MemberActivity;
import com.global.tech.space.cechurchadmin.models.Member;

import java.util.List;
import java.util.Random;

import static com.global.tech.space.cechurchadmin.adapters.MemberAdapter.*;

public class MemberAdapter extends RecyclerView.Adapter<MemberHolder> implements Filterable, AdapterFilterFactory.FilterableAdapter< Member >
{
    private final int[] avatars = { R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4 };
    private final Random random = new Random();

    private final FragmentActivity context;
    private List<Member> members;
    private final List<Member> copy;

    public MemberAdapter( FragmentActivity context, List<Member> members)
    {
        this.context = context;
        this.copy = members;
        this.members = members;
    }

    @NonNull
    @Override
    public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        return new MemberHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull MemberHolder holder, int position) {
        Member member = members.get(position);
        holder.name.setText( member.getFullName() );
        holder.number.setText( member.phoneNumber == null || member.phoneNumber.isEmpty() ? "No Phone Number" : member.phoneNumber );
        holder.email.setText( member.emailAddress == null || member.emailAddress.isEmpty() ? "No Email Address" : member.emailAddress );
        holder.photo.setImageResource( avatars[ random.nextInt( 4 ) ] );

        holder.itemView.setOnClickListener( v -> {
            Log.d("Member_Fragments", "Pressing Member " + member.id );
            Intent intent = new Intent( context, MemberActivity.class );
            intent.putExtra( "id", member.id );
            context.startActivityForResult( intent, MemberActivity.REQUEST_MEMBER );
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
        AdapterFilterFactory<Member> factory = new AdapterFilterFactory<>(this);
        return factory.getFilter();
    }

    @Override
    public void setCurrent(List<Member> list) {
        members = list;
        notifyDataSetChanged();
    }

    @Override
    public List<Member> getInitial() {
        return copy;
    }

    public static class MemberHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView number;
        private final TextView email;
        private final ImageView photo;

        public MemberHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.user_name );
            number = itemView.findViewById( R.id.user_number );
            email = itemView.findViewById(R.id.user_email );
            photo = itemView.findViewById(R.id.user_photo);
        }
    }
}

