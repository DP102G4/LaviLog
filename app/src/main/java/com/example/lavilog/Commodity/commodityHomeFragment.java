package com.example.lavilog.Commodity;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavilog.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;


public class commodityHomeFragment extends Fragment {
    private static final String TAG = "TAG_commodityHomeFragment";
    private Activity activity;
    private RecyclerView rvCommidity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commodity_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCommidity = view.findViewById(R.id.rvCommidity);
        rvCommidity.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        listenToCommodity();
    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
        registration = null;
    }

    private void showAll() {
//        db.collection("Commodities").get()
//                .addOnCanceledListener(new OnCanceledListener<QuerySnapshot>() {
//                    @Override
//                    public void onCanceled(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful())
//                    }
//                });
    }

    private void listenToCommodity() {
    }
}
