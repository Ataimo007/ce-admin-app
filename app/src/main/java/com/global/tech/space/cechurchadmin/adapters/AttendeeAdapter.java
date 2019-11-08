package com.global.tech.space.cechurchadmin.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.attendance.MemberActivity;
import com.global.tech.space.cechurchadmin.models.Attendee;
import com.global.tech.space.cechurchadmin.models.Member;

import java.util.List;
import java.util.Random;

import static com.global.tech.space.cechurchadmin.adapters.AttendeeAdapter.*;

public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeHolder>
{
    private final int[] avatars = { R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4 };
    private final Random random = new Random();

    private final List<Attendee> attendees;
    private AppCompatActivity context = null;

    public AttendeeAdapter( AppCompatActivity context, List<Attendee> attendees ) {
        this.attendees = attendees;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendeeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false);
        return new AttendeeHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull AttendeeHolder holder, int position) {
        Member member = attendees.get(position).member;
        holder.name.setText( member.firstName + " " + member.surname);
        holder.number.setText( member.phoneNumber == null || member.phoneNumber.isEmpty() ? "No Phone Number" : member.phoneNumber );
        holder.email.setText( member.emailAddress == null || member.emailAddress.isEmpty() ? "No Email Address" : member.emailAddress );
        holder.photo.setImageResource( avatars[ random.nextInt( 4 ) ] );

        holder.itemView.setOnClickListener( v -> {
            Intent intent = new Intent( context, MemberActivity.class );
            intent.putExtra( "id", member.id );
            context.startActivityForResult( intent, MemberActivity.REQUEST_MEMBER );
        });
    }

    @Override
    public int getItemCount() {
        if ( attendees == null )
            return 0;
        return attendees.size();
    }

    public static class AttendeeHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView number;
        private final TextView email;
        private final ImageView photo;

        public AttendeeHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.user_name );
            number = itemView.findViewById( R.id.user_number );
            email = itemView.findViewById(R.id.user_email );
            photo = itemView.findViewById(R.id.user_photo);
        }
    }
}
