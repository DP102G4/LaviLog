package com.example.lavilog.Daily;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lavilog.R;


public class SearchFragment extends Fragment {
    private Activity activity;
    private TextView tvYear,tvDate;
    private static int year,month,day;
    private Menu m;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.add(0,Menu.FIRST,0,"hahahahaha");
        menu.removeItem(1);
        menu.add(0,Menu.FIRST,0,"world");
        inflater.inflate(R.menu.menu_place, menu);
        menu.removeItem(2);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDate = view.findViewById(R.id.tvDate);
        tvYear = view.findViewById(R.id.tvYear);
        Bundle bundle = getArguments();
        if (bundle!=null){
            int year =bundle.getInt("year");
            int month=bundle.getInt("month");
            int day =bundle.getInt("day");
            tvYear.setText(year+"年");
            tvDate.setText(month+1+"月"+day+"day");
        }
    }
}