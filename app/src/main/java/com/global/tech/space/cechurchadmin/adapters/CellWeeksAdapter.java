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
import com.global.tech.space.cechurchadmin.attendance.WeekCellActivity;
import com.global.tech.space.cechurchadmin.models.CellWeekInfo;

import java.util.List;
import java.util.Random;

import static com.global.tech.space.cechurchadmin.adapters.CellWeeksAdapter.*;

public class CellWeeksAdapter extends RecyclerView.Adapter<CellWeeksHolder> implements Filterable,
        AdapterFilterFactory.FilterableAdapter< CellWeekInfo >
{
    private final Random random = new Random();
    private final int[] avatars = { R.drawable.avatars_1, R.drawable.avatars_2, R.drawable.avatars_3 };
    private final FragmentActivity context;
    private List<CellWeekInfo> cellWeekInfos;
    private final List<CellWeekInfo> copy;
    private final int requestCode;

    public CellWeeksAdapter(FragmentActivity context, List<CellWeekInfo> cellWeekInfos) {
        this( context, cellWeekInfos, -1 );
    }

    public CellWeeksAdapter(FragmentActivity context, List<CellWeekInfo> cellWeekInfos, int requestCode) {
        this.context = context;
        this.cellWeekInfos = cellWeekInfos;
        copy = cellWeekInfos;
        this.requestCode = requestCode;
    }

    @NonNull
    @Override
    public CellWeeksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.service_card, parent, false);
        return new CellWeeksHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull CellWeeksHolder holder, int position) {
        CellWeekInfo cellWeekInfo = cellWeekInfos.get(position);
        holder.name.setText( "Week " + cellWeekInfo.week );
        holder.attendance.setText( "Meetings Held : " + cellWeekInfo.meetingsHeld );
        holder.photo.setImageResource( avatars[ random.nextInt( 3 ) ] );

        holder.itemView.setOnClickListener(v -> {
            Intent service = new Intent( context, WeekCellActivity.class );
            service.putExtra( "week_meetings", cellWeekInfo.toGson() );
            context.startActivityForResult( service, requestCode);
        });
    }

    @Override
    public int getItemCount() {
        if ( cellWeekInfos == null )
            return 0;
        return cellWeekInfos.size();
    }

    @Override
    public Filter getFilter() {
        AdapterFilterFactory<CellWeekInfo> factory = new AdapterFilterFactory<>(this);
        return factory.getFilter();
    }

    @Override
    public void setCurrent(List<CellWeekInfo> list) {
        cellWeekInfos = list;
        notifyDataSetChanged();
    }

    @Override
    public List<CellWeekInfo> getInitial() {
        return copy;
    }

    public static class CellWeeksHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView attendance;
        private final ImageView photo;

        public CellWeeksHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById( R.id.service_name );
            attendance = itemView.findViewById( R.id.service_attendance );
            photo = itemView.findViewById(R.id.service_photo);
        }
    }
}