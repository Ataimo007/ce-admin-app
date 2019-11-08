package com.global.tech.space.cechurchadmin.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.global.tech.space.cechurchadmin.R;
import com.global.tech.space.cechurchadmin.models.Cell;

import java.util.List;
import java.util.Locale;

public class DualOptionDialogFragment extends DialogFragment
{
    public final static String tag = "CELL_DIALOG_SHEET";


    public void setListener(DualOptionListener listener) {
        this.listener = listener;
    }

    private DualOptionListener listener;

    public interface DualOptionListener {
        public void option1();
        public void option2();
    }

    public void show(FragmentManager manager)
    {
        show( manager, tag );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if ( context instanceof  DualOptionListener )
            listener = (DualOptionListener) context;
        else
            Log.d("Dual_Option_Dialog", getActivity().toString() + " Doesn't Implement CellSelectListener");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dual_option_list, container, false);
        ((Button) view.findViewById( R.id.option_1 )).setOnClickListener( v -> {
            dismiss();
            listener.option1();
        });
        ((Button) view.findViewById( R.id.option_2 )).setOnClickListener( v -> {
            dismiss();
            listener.option2();
        });
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
