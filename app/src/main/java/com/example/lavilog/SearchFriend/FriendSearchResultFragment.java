package com.example.lavilog.SearchFriend;


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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FriendSearchResultFragment extends Fragment {
    private static final String TAG = "TAGFriendSearchResultF";
    private Activity activity;
    private ImageView ivFriend;
    private TextView tvFriendName;
    private Button btFriendDiary;

    private Friend friend;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("LaviLog");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_search_result, container, false);
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
        ivFriend = view.findViewById(R.id.ivFriend);
        tvFriendName = view.findViewById(R.id.tvFriendName);
        btFriendDiary = view.findViewById(R.id.btAddFriend);

        if (getArguments() != null) {
            friend = (Friend) getArguments().getSerializable("friend");
            if (friend != null) {
                tvFriendName.setText(friend.getName());

                // 如果存有圖片路徑，取得圖片後顯示
                if (friend.getImagePath() != null) {
                    showImage(ivFriend, friend.getImagePath());
                }
            }
        }


        // 改成目的地頁面 進入好友日誌主題內

//        btFriendDiary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view)
//                        .navigate(R.id.改成目的地頁面 進入好友日誌主題內);
//            }
//        });
    }

    /**
     * 下載Firebase storage的照片並顯示在ImageView上
     */
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
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

