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

import com.global.tech.space.cechurchadmin.CellActivity;
import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.models.Cell;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.global.tech.space.cechurchadmin.adapters.CellAdapter.*;

public class CellAdapter extends RecyclerView.Adapter<CellHolder> implements Filterable, AdapterFilterFactory.FilterableAdapter< Cell >
{
    private final Random random = new Random();
    private final int[] avatars = { R.drawable.avatars_1, R.drawable.avatars_2, R.drawable.avatars_3 };

    private  List<Cell> cells;
    private final List<Cell> copy;
    private final FragmentActivity context;
    private final int requestCode;

    public CellAdapter( FragmentActivity context, List<Cell> cells )
    {
        this( context, cells, -1 );
    }

    public CellAdapter( FragmentActivity context, List<Cell> cells, int requestCode )
    {
        this.context = context;
        this.cells = cells;
        copy = cells;
        this.requestCode = requestCode;
    }

    @NonNull
    @Override
    public CellHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_info, parent, false);
        return new CellHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull CellHolder holder, int position) {
        Cell info = cells.get(position);
        holder.name.setText( info.name );
        holder.leader.setText( info.leader.getFullName() );
        holder.image.setImageResource( avatars[ random.nextInt( 3 ) ] );

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent( context, CellActivity.class );
            intent.putExtra( "id", info.id );
            context.startActivityForResult( intent, requestCode );
        });
    }

    @Override
    public int getItemCount() {
        if ( cells == null )
            return 0;
        return cells.size();
    }

    @Override
    public Filter getFilter() {
        AdapterFilterFactory<Cell> factory = new AdapterFilterFactory<>(this);
        return factory.getFilter();
    }

    @Override
    public void setCurrent(List<Cell> list) {
        cells = list;
        notifyDataSetChanged();
    }

    @Override
    public List<Cell> getInitial() {
        return copy;
    }

    public static class CellHolder extends RecyclerView.ViewHolder
    {
        private final TextView name;
        private final TextView leader;
        private final ImageView image;

        public CellHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cell_name);
            leader = itemView.findViewById(R.id.cell_leader);
            image = itemView.findViewById(R.id.cell_image);
        }
    }
}
