package com.example.lavilog;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// 載入套件

public class FriendHomeFragment extends Fragment {
    private static final String TAG = "TAG_FriendHomeFragment";
    private Activity activity;
    private ImageView ivAccountPhoto;
    private TextView tvAccountName, tvAccount;
    private Button btSearchFriend, btSearchID, btQRcode;
    private Button btNotice;
    // 宣告全域變數
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String id,account;
    private User user;
    // 資料庫

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("好友管理"); // 設定標題
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 取得介面元件
        ivAccountPhoto = view.findViewById(R.id.ivAccountPhoto);
        tvAccountName = view.findViewById(R.id.tvAccountName);
        tvAccount = view.findViewById(R.id.tvAccount);
        btSearchFriend = view.findViewById(R.id.btAddFriend);
        btSearchID = view.findViewById(R.id.btSearchID);
        btQRcode = view.findViewById(R.id.btScanQR);

        btNotice = view.findViewById(R.id.btNotice);

        // 取得firebase user資料
        account=auth.getCurrentUser().getEmail();
        Query query=db.collection("users");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    final com.example.lavilog.User FBUser = documentSnapshot.toObject(com.example.lavilog.User.class);
                    String accountFB = FBUser.getAccount();
                    if (account.equals(accountFB)) {
                        user = FBUser;
                        id = user.getId();
                        tvAccount.setText(account);
                        final String name = user.getName();
                        String path = user.getImagePath();
                        if (path != null) {
                            showImage(ivAccountPhoto, user.getImagePath());
                        } else {
                            ivAccountPhoto.setImageResource(R.drawable.no_image);
                        }
                        tvAccountName.setText(name);
                    }
                }
            }
        });


        // 為 Button 元件加入 Click 事件的監聽器，觸發時執行自訂方法 new View.OnClickListener
        btSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_friendHomeFragment_to_friendSearchFragment);
            }
        }); // 搜尋好友日誌按鈕

        btSearchID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_friendHomeFragment_to_searchUserIdFragment);
            }
        }); // 加入帳號按鈕

        btQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_friendHomeFragment_to_QRcodeFragment);
            }
        }); // 行動條碼掃描按鈕

        btNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_friendHomeFragment_to_noticeFragment);
            }
        }); // 通知按鈕
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
