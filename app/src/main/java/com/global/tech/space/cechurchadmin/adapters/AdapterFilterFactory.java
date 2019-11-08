package com.global.tech.space.cechurchadmin.adapters;

import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.models.Searchable;

import java.util.ArrayList;
import java.util.List;

public class AdapterFilterFactory< T extends Searchable >
{
    private final FilterableAdapter< T > adapter;

    public AdapterFilterFactory(FilterableAdapter<T> adapter) {
        this.adapter = adapter;
    }

    public Filter getFilter()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String query = constraint.toString().trim().toLowerCase();
                if ( query.isEmpty() )
                    results.values = adapter.getInitial();
                else
                {
                    List<Object> filtered = new ArrayList<>();
                    for ( T item : adapter.getInitial())
                    {
                        try
                        {
                            if ( item.search( query ) )
                                filtered.add( item );
                        }
                        catch ( Exception ex )
                        {
                            Log.d("Searching_List", String.format( "Exception at member %s, query %s, cause %s",
                                    item, query, ex.getMessage() ) );
                        }
                    }
                    results.values = filtered;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapter.setCurrent((List<T>) results.values);
            }
        };
    }

    public static interface FilterableAdapter<  T extends Searchable  >
    {
        /**
         * Please don't forget to notifyChange
         * @param list
         */
        void setCurrent( List< T > list );
        List< T > getInitial();
    }
}
