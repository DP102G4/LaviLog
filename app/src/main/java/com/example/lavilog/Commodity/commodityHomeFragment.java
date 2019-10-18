package com.example.lavilog.Commodity;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavilog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class commodityHomeFragment extends Fragment {
    private static final String TAG = "TAG_Commodity";
    private Activity activity;
    private RecyclerView rvCommidity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity.setTitle("商城");
        return inflater.inflate(R.layout.fragment_commodity_home, container, false);
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
        rvCommidity = view.findViewById(R.id.rvCommidity);
        rvCommidity.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        listenToCommodities();
    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
        registration = null;
    }

    private void showAll() {
        db.collection("commodity").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            List<Commodity> commodities = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                commodities.add(document.toObject(Commodity.class));
                            }
                            rvCommidity.setAdapter(new CommodityAdapter(activity, commodities));
                        } else {
                            String message = task.getException() == null ?
                                    "No commodity found!" : task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void listenToCommodities(){
        if(registration == null){
            registration = db.collection("commodity").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    Log.d(TAG, "Commodities change.");
                    if (e == null){
                        List<Commodity> commodities = new ArrayList<>();
                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                            commodities.add(document.toObject(Commodity.class));
                        }
                        CommodityAdapter commodityAdapter = (CommodityAdapter) rvCommidity.getAdapter();
                        if(commodityAdapter != null){
                            commodityAdapter.setCommodities(commodities);
                            commodityAdapter.notifyDataSetChanged();
                        }
                    }else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            });
        }
    }
    private class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.CommodityViewHolder>{
        Context context;
        List<Commodity> commodities;

        CommodityAdapter(Context context, List<Commodity> commodityList){
            this.context = context;
            this.commodities = commodityList;
        }

        public void setCommodities(List<Commodity> commodities){
            this.commodities = commodities;
        }

        class CommodityViewHolder extends RecyclerView.ViewHolder{
            ImageView ivCommodity;
            TextView tvName, tvPrice, tvInfo;

            CommodityViewHolder(View itemView){
                super(itemView);
                ivCommodity = itemView.findViewById(R.id.ivProduct);
                tvName = itemView.findViewById(R.id.tvProductName);
                tvPrice = itemView.findViewById(R.id.tvProductPrice);
                tvInfo = itemView.findViewById(R.id.tvProductInfo);
            }
        }

        @Override
        public int getItemCount() { return commodities.size(); }

        @NonNull
        @Override
        public CommodityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.product_item_view, parent, false);
            return new CommodityViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull CommodityViewHolder holder, int position) {
            final Commodity commodity = commodities.get(position);
            if(commodity.getImageUrl() == null){
                holder.ivCommodity.setImageResource(R.drawable.no_image);
            } else {
                showImage(holder.ivCommodity, commodity.getImageUrl());
            }
            holder.tvName.setText(commodity.getProductName());
            holder.tvPrice.setText(String.valueOf(commodity.getProductPrice()));
            holder.tvInfo.setText(commodity.getProductInfo());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("ProductID", commodity.getProductId());
                    Navigation.findNavController(v).navigate(R.id.action_commodityHomeFragment_to_commodityDetailFragment, bundle);
                }
            });
        }
    }

    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        } else {
                            String message = task.getException() == null ?
                                    getString(R.string.textImageDownloadFail) + ": " + path :
                                    task.getException().getMessage() + ": " + path;
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
