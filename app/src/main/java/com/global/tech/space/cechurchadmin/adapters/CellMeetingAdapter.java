package com.global.tech.space.cechurchadmin.adapters;

import android.content.Intent;
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
import com.global.tech.space.cechurchadmin.attendance.CellAttendanceActivity;
import com.global.tech.space.cechurchadmin.models.CellMeeting;
import com.global.tech.space.cechurchadmin.models.States;

import java.util.List;
import java.util.Random;

import static com.global.tech.space.cechurchadmin.adapters.CellMeetingAdapter.*;

public class CellMeetingAdapter extends RecyclerView.Adapter<CellMeetingHolder> implements Filterable,
        AdapterFilterFactory.FilterableAdapter< CellMeeting >
{
    private final int[] avatars = { R.drawable.avatars_1, R.drawable.avatars_2, R.drawable.avatars_3 };
    private final Random random = new Random();

    private List<CellMeeting> cellMeetings;
    private final List<CellMeeting> copy;
    private FragmentActivity activity;
    private final int requestCode;

    public CellMeetingAdapter(FragmentActivity activity, List<CellMeeting> cellMeetings) {
        this( activity, cellMeetings, -1 );
    }

    public CellMeetingAdapter(FragmentActivity activity, List<CellMeeting> cellMeetings, int requestCode) {
        this.cellMeetings = cellMeetings;
        copy = cellMeetings;
        this.activity = activity;
        this.requestCode = requestCode;
    }

    @NonNull
    @Override
    public CellMeetingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.service_card, parent, false);
        return new CellMeetingHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull CellMeetingHolder holder, int position) {
        CellMeeting cellMeeting = cellMeetings.get( position );
        holder.name.setText( cellMeeting.cell.name );
        holder.attendance.setText( "Attendees " + cellMeeting.attendance );
        holder.photo.setImageResource( avatars[ random.nextInt( 3 ) ] );

        if  ( cellMeeting.status.equals( States.Status.ONGOING ) )
            holder.status.setText( cellMeeting.status.name() );

        holder.itemView.setOnClickListener(v -> {
            Intent service = new Intent(activity, CellAttendanceActivity.class );
            service.putExtra( "cell_meeting", cellMeeting.toGson() );
            service.putExtra( "meeting_index", position );
            activity.startActivityForResult( service, requestCode );
        });
    }

    @Override
    public int getItemCount() {
        if ( cellMeetings == null )
            return 0;
        return cellMeetings.size();
    }

    @Override
    public Filter getFilter() {
        AdapterFilterFactory<CellMeeting> factory = new AdapterFilterFactory<>(this);
        return factory.getFilter();
    }

    @Override
    public void setCurrent(List<CellMeeting> list) {
        cellMeetings = list;
        notifyDataSetChanged();
    }

    @Override
    public List<CellMeeting> getInitial() {
        return copy;
    }

    public static class CellMeetingHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView attendance;
        private final TextView status;
        private final ImageView photo;

        public CellMeetingHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.service_name );
            attendance = itemView.findViewById( R.id.service_attendance );
            status = itemView.findViewById(R.id.service_status);
            photo = itemView.findViewById(R.id.service_photo);
        }
    }
}
