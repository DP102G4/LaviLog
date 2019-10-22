package com.example.lavilog.Commodity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lavilog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;


public class CommodityInsertFragment extends Fragment {
    private static final String TAG = "TAG_ProductInsert";
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ImageView ivProductIn;
    private EditText etProductNameIn, etProductInfoIn, etProductPriceIn;
    private File file;
    private Uri contentUri;
    private Commodity commodity;
    private static final int REQ_PICK_PICTURE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        commodity = new Commodity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("新增商品");
        return inflater.inflate(R.layout.fragment_commodity_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivProductIn = view.findViewById(R.id.ivProductImageInsert);
        etProductNameIn = view.findViewById(R.id.tvProductNameInsert);
        etProductInfoIn = view.findViewById(R.id.tvProductInfoInsert);
        etProductPriceIn = view.findViewById(R.id.tvProductPriceInsert);

        view.findViewById(R.id.btPickProdictPciture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_PICK_PICTURE);
                } else {
                    Toast.makeText(activity, "開啟相簿權限", Toast.LENGTH_SHORT).show();
                }
            }
        });

        view.findViewById(R.id.btProductInsert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = db.collection("commodity").document().getId();
                commodity.setProductId(id);
                commodity.setOnsale(true);
                String productName = etProductNameIn.getText().toString().trim();
                String productInfo = etProductInfoIn.getText().toString().trim();
                int productPrice = Integer.parseInt(etProductPriceIn.getText().toString().trim());

                commodity.setProductName(productName);
                commodity.setProductInfo(productInfo);
                commodity.setProductPrice(productPrice);
                addcheck(commodity);
//                Navigation.findNavController(v).navigate(R.id.action_commodityInsertFragment_to_backStageFragment);
            }
        });
    }

    private void addcheck (final Commodity commodity){
        db.collection("commodity").document(commodity.getProductId()).set(commodity)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String message = getString(R.string.textInserted)
                                    + " with ID: " + commodity.getProductId();
                            Log.d(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            // 新增完畢回上頁
                            Navigation.findNavController(ivProductIn).popBackStack();
                        } else {
                            String message = task.getException() == null ?
                                    getString(R.string.textInsertFail) :
                                    task.getException().getMessage();
//                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
