package com.example.lavilog.SearchUserId;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.R;
//import com.example.lavilog.SearchFriend.Friend;
//import com.example.lavilog.SearchUserId.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SearchUserIdResultFragment extends Fragment {
    private static final String TAG = "TAGFSearchUserIdResultF";
    private Activity activity;
    private ImageView ivUser;
    private TextView tvUserName, tvAccount;
    private Button btAddFriend;

    private User user;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("LaviLog");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user_id_result, container, false);
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
        ivUser = view.findViewById(R.id.ivUser);
        tvUserName = view.findViewById(R.id.tvAdmName);
        tvAccount = view.findViewById(R.id.tvAccount);
        btAddFriend = view.findViewById(R.id.btAddFriend);

        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
            if (user != null) {
                Log.e(TAG, "" + user.getAccount());
                tvUserName.setText(user.getName());
                tvAccount.setText(user.getAccount());

                // 如果存有圖片路徑，取得圖片後顯示
                if (user.getImagePath() != null) {
                    showImage(ivUser, user.getImagePath());
                }
            }
        }

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAddFriend.setText("已發送邀請");
                btAddFriend.setEnabled(false);
            }
        });
    }

    /** 下載Firebase storage的照片並顯示在ImageView上 */
    private void showImage(final ImageView imageView, final String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);//完整路徑
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {//若拿到完整的圖,回傳byte陣列
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
