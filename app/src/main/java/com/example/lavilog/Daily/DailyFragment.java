package com.example.lavilog.Daily;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.lavilog.R;

import java.util.Calendar;


public class DailyFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener{
    private Activity activity;
    private ImageView ivSearch;
    private ImageButton ibInsert,ibSearch,ibAlbum;
    private static int year,month,day;
    private View myview;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("每日日誌");
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_daily, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ibAlbum = view.findViewById(R.id.ibAlbum);
        ibAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_dailyFragment_to_albumFragment);
            }
        });
        ibSearch = view.findViewById(R.id.ibSearch);
        showNow();
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(activity,DailyFragment.this,DailyFragment.year,DailyFragment.month,DailyFragment.day).show();
                System.out.println("hello "+DailyFragment.year+""+DailyFragment.month+""+DailyFragment.day);
                myview=view;
            }
        });
        ibInsert = view.findViewById(R.id.ibInsert);
        ibInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_dailyFragment_to_testFragment);
            }

        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        DailyFragment.year=year;
        DailyFragment.month=(month+1);
        DailyFragment.day=day;
        System.out.println(year+" "+month+" "+day);
        Bundle bundle=new Bundle();
        bundle.putInt("year",year);
        bundle.putInt("month",month+1);
        bundle.putInt("day",day);
        Log.i("harrison!!!!!",year+""+month+""+day);
        Navigation.findNavController(myview)
                .navigate(R.id.action_dailyFragment_to_searchFragment,bundle);

    }
    private void showNow() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }
}

