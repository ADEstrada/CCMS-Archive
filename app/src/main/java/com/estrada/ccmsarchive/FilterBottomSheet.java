package com.estrada.ccmsarchive;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.w3c.dom.Text;

import java.util.Optional;

public class FilterBottomSheet extends BottomSheetDialogFragment {
    private String[] options;
    private String title;
    private OnFilterSelectedListener listener;

    public FilterBottomSheet(String title, String[] options, OnFilterSelectedListener listener) {
        this.title = title;
        this.options = options;
        this.listener = listener;
    }

    @Nullable
    @Override
    public  View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_filter_sheet, container, false);
        TextView titleView = view.findViewById(R.id.filterTitle);
        titleView.setText(title);

        LinearLayout optionsContainer = view.findViewById(R.id.optionsContainer);

        for (String option : options) {
            View row = inflater.inflate(R.layout.item_filter, optionsContainer, false);
            TextView text = row.findViewById(R.id.optionText);
            text.setText(option);

            row.setOnClickListener(v -> {
                listener.onFilterSelected(option);
                dismiss();
            });

            optionsContainer.addView(row);
        }

        return view;
    }
}
