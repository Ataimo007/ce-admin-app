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
import com.global.tech.space.cechurchadmin.attendance.AttendanceActivity;
import com.global.tech.space.cechurchadmin.models.ChurchService;
import com.global.tech.space.cechurchadmin.models.States;

import java.util.List;
import java.util.Random;

import static com.global.tech.space.cechurchadmin.adapters.ServiceAdapter.*;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceViewHolder> implements Filterable,
        AdapterFilterFactory.FilterableAdapter< ChurchService >
{
    private final Random random = new Random();
    private final int[] avatars = { R.drawable.avatars_1, R.drawable.avatars_2, R.drawable.avatars_3 };
    private List<ChurchService> churchServices;
    private final List<ChurchService> copy;
    private final FragmentActivity context;
    private final int requestCode;

    public ServiceAdapter(FragmentActivity context, List<ChurchService> churchServices) {
        this( context, churchServices, -1 );
    }

    public ServiceAdapter(FragmentActivity context, List<ChurchService> churchServices, int requestCode) {
        this.churchServices = churchServices;
        copy = churchServices;
        this.context = context;
        this.requestCode = requestCode;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_card, parent, false);
        return new ServiceViewHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ChurchService churchService = churchServices.get(position);
        holder.name.setText( churchService.name );
        holder.attendance.setText( "Attendee " + churchService.attendance );
        holder.photo.setImageResource( avatars[ random.nextInt( 3 ) ] );

        if  ( churchService.status.equals( States.Status.ONGOING ) )
            holder.status.setText( churchService.status.name() );

        holder.itemView.setOnClickListener(v -> {
            Intent service = new Intent( context, AttendanceActivity.class );
            service.putExtra( "service", churchService.toGson() );
            service.putExtra( "service_index", position );
            context.startActivityForResult( service, requestCode );
        });
    }

    @Override
    public int getItemCount() {
        if ( churchServices == null )
            return 0;
        return churchServices.size();
    }

    @Override
    public Filter getFilter() {
        AdapterFilterFactory<ChurchService> factory = new AdapterFilterFactory<>( this );
        return factory.getFilter();
    }

    @Override
    public void setCurrent(List<ChurchService> list) {
        churchServices = list;
        notifyDataSetChanged();
    }

    @Override
    public List<ChurchService> getInitial() {
        return copy;
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView attendance;
        private final TextView status;
        private final ImageView photo;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.service_name );
            attendance = itemView.findViewById( R.id.service_attendance );
            status = itemView.findViewById(R.id.service_status);
            photo = itemView.findViewById(R.id.service_photo);
        }
    }
}