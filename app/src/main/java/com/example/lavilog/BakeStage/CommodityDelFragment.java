package com.example.lavilog.BakeStage;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.lavilog.Commodity.Commodity;
import com.example.lavilog.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class CommodityDelFragment extends Fragment {
    private static final String TAG = "TAG_CommodityDel";
    private Activity activity;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<Commodity> commodities;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("商品下架");
        return inflater.inflate(R.layout.fragment_commodity_del, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvCommodityDel);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        searchView = view.findViewById(R.id.svCommodityDel);
    }


}
