package com.example.lavilog.Account;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.Account.Order;
import com.example.lavilog.Commodity.Commodity;
import com.example.lavilog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class order_1_Fragment extends Fragment {
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    RecyclerView rvOrder;
    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        storage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        activity.setTitle("購物紀錄");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_1_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        rvOrder = view.findViewById(R.id.rvOrder);
        String account = (String)bundle.getSerializable("account");
        Query query = db.collection("order").whereEqualTo("account",account);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Order> orders = new ArrayList<>();
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    Order order = documentSnapshot.toObject(Order.class);
                    orders.add(order);
                }
                rvOrder.setLayoutManager(new LinearLayoutManager(activity));
                rvOrder.setAdapter(new orderAdapter(activity, orders));
            }
        });
    }
    private class orderAdapter extends RecyclerView.Adapter<orderAdapter.MyViewHolder>{
        Context context;
        List<Order> orders;

        orderAdapter(Context context,List<Order> orders){
            this.context=context;
            this.orders=orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.order_record, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            final Order order = orders.get(position);
            if (order.getImageUrl() != null){
                showImage(holder.ivOrderImage, order.getImageUrl());
            }else{
                holder.ivOrderImage.setImageResource(R.drawable.no_image);
            }
            String productId = order.getProductId();
            Query query = db.collection("commodity").whereEqualTo("productId",productId);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful())? task.getResult() : null;
                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                        Commodity commodity = documentSnapshot.toObject(Commodity.class);
                        holder.tvProductName.setText(commodity.getProductName());
//                        Toast.makeText(activity,"ss"+commodity.getProductPrice(),Toast.LENGTH_SHORT).show();
                        holder.tvProductPrice.setText(String.valueOf(commodity.getProductPrice()));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{
            ImageView ivOrderImage;
            TextView tvProductName;
            TextView tvProductPrice;

            MyViewHolder(View itemView){
                super(itemView);
                ivOrderImage = itemView.findViewById(R.id.ivOrderImage);
                tvProductName = itemView.findViewById(R.id.tvProductName);
                tvProductPrice =itemView.findViewById(R.id.tvProductPrice);
            }
        }

    }
    /** 下載Firebase storage的照片並顯示在ImageView上 */
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path); // 完整路徑
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) { // 若拿到完整的圖,回傳byte陣列
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imageView.setImageBitmap(bitmap);
                        } else {
                            String message = task.getException() == null ?
                                    getString(R.string.textImageDownloadFail) + ": " + path :
                                    task.getException().getMessage() + ": " + path;
                            Log.e("TAG", message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
