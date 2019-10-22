package com.example.lavilog.BakeStage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lavilog.R;


public class commodityInsDelFragment extends Fragment {
    Button btProductOnsaleT, btProductOnsaleF;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commodity_ins_del, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btProductOnsaleT = view.findViewById(R.id.btProductOnsaleT);
        btProductOnsaleF = view.findViewById(R.id.btProductOnsaleF);

        btProductOnsaleT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(btProductOnsaleT).navigate(R.id.action_commodityInsDelFragment_to_commodityInsertFragment);
            }
        });

//        btProductOnsaleF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Navigation.findNavController(btProductOnsaleF).navigate(R.id.action_commodityBacksatgeFragment_to_commodityDelFragment);
//            }
//        });
    }
}
