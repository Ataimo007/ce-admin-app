package com.global.tech.space.cechurchadmin.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class MultiOptionBottomSheet<T> extends BottomSheetDialogFragment
{
    public final static String tag = "CELL_DIALOG_SHEET";
    private RecyclerView list;


    private MultiOptionListener<T> listener;

    public interface MultiOptionListener<T> {
        void option(int position, T choice);
    }

    public void show(FragmentManager manager)
    {
        show( manager, tag );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (MultiOptionListener) context;
        } catch ( ClassCastException ex )
        {
            throw new ClassCastException( getActivity().toString() + " must Implement CellSelectListener" );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multi_option_list, container, false);
        list = view.findViewById(R.id.multi_options);
        list.setLayoutManager( new LinearLayoutManager( this.getContext()));
        TextView title = view.findViewById(R.id.dialog_title);
        title.setText( getArguments().getString( "title", "Select Method Taking Attendance" ) );
        list.setAdapter( new MultiAdapter() );
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private class MultiAdapter extends RecyclerView.Adapter< CellHolder >
    {
        private final T[] options;

        public MultiAdapter()
        {
            options = (T[]) getArguments().getStringArray("options");
        }

        @NonNull
        @Override
        public CellHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View info = getLayoutInflater().inflate(R.layout.single_option, parent, false);
            return new CellHolder( info );
        }

        @Override
        public void onBindViewHolder(@NonNull CellHolder holder, int position) {
            holder.name.setText( options[ position ].toString() );

            holder.itemView.setOnClickListener( v -> {
                MultiOptionBottomSheet.this.dismiss();
                listener.option( position, options[ position ] );
            });
        }

        @Override
        public int getItemCount() {
            return options.length;
        }
    }

    private class CellHolder extends RecyclerView.ViewHolder
    {
        private final TextView name;

        public CellHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.option_name);
        }
    }
}
