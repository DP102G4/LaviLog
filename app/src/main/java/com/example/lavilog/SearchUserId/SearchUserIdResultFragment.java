package com.example.lavilog.SearchUserId;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.Daily.DailyQuestion;
import com.example.lavilog.R;
//import com.example.lavilog.SearchFriend.Friend;
//import com.example.lavilog.SearchUserId.User;

import com.example.lavilog.SearchUserId.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class SearchUserIdResultFragment extends Fragment {
    private static final String TAG = "TAGFSearchUserIdResultF";
    private Activity activity;
    private ImageView ivUser;
    private TextView tvUserName, tvAccount;
    private TextView tvUserMessage;
    private TextView tvUserImagrPath;
    private TextView tvUserImagrPath2;
    private TextClock textClock2;
    private Button btAddFriend;

    private User user;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;

    private Uri filePath;
    private Notice notice;

    private Friend friend;
    private FirebaseAuth auth;
    private String account, id;


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
        notice = new Notice();
        friend = new Friend();
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivUser = view.findViewById(R.id.ivUser);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvAccount = view.findViewById(R.id.tvAccount);
        btAddFriend = view.findViewById(R.id.btAddFriend);

        tvUserMessage = view.findViewById(R.id.tvUserMessage);
        tvUserImagrPath = view.findViewById(R.id.tvUserImagePath);
        tvUserImagrPath2 = view.findViewById(R.id.tvUserImagePath2);

        account=auth.getCurrentUser().getEmail();
        //db.collection("friends").document(friend.getId()).set(friend);
//        Query query=db.collection("friends");
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
//                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                    final com.example.lavilog.User FBUser = documentSnapshot.toObject(com.example.lavilog.User.class);
//                    String accountFB = FBUser.getAccount();
//                    if (account.equals(accountFB)) {
//                        user = FBUser;//將for-each內符合條件的帳號抓下來，就是該使用者的user物件
//                        id = user.getId();//取得該會員存於資料庫內的id,方便後續更換資料使用
//                    }
//                }
//            }
//        });

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

        textClock2 = view.findViewById(R.id.textClock2);
        final String time = textClock2.getText().toString();

        textClock2.setFormat12Hour("yyyy年MM月dd日");

        db.collection("notices").whereEqualTo("time", time).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    DailyQuestion dailyQuestion = documentSnapshot.toObject(DailyQuestion.class);
                    //question = dailyQuestion.getQuestion();
                    //tvQuestion.setText(question);

                }
            }
        });

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAddFriend.setText("已發送邀請");
                btAddFriend.setEnabled(false);

                // 先取得插入document的ID
                final String id = db.collection("notices").document().getId();
                //指定集合為notices,若找不到notices會自己建立


                // document為建立一筆資料,自動生成id,getId去取得id
                notice.setId(id);//生成的notice物件,放入id  只是存圖檔的路徑（路徑字串）

                //tvUserName.setText(tvUserName + " 已和您成為朋友");

                tvUserMessage.setText(" 已和您成為好友");
                String noticeMessage = tvUserName.getText().toString().trim();
                String noticeTime = textClock2.getText().toString();
                String noticeMessage2 = tvUserMessage.getText().toString().trim();
                //String account = account.getText().toString();

                notice.setNoticeMessage(noticeMessage);
                notice.setNoticeTime(noticeTime);
                notice.setNoticeMessage2(noticeMessage2);
                notice.setAccount(account);


                // Get the data from an ImageView as bytes
                ivUser.setDrawingCacheEnabled(true);
                ivUser.buildDrawingCache();





                ////////// btAddFriend 以上Notice Firebase //////////
                ////////// btAddFriend 以下Friend Firebase //////////


                // 先取得插入document的ID
                final String id2 = db.collection("friends").document().getId();
                //指定集合為friends,若找不到friends會自己建立


                // document為建立一筆資料,自動生成id,getId去取得id
                friend.setId(id2);//生成的friend物件,放入id  只是存圖檔的路徑（路徑字串）

//                String imagePath = imagePath2.getText().toString;
                String name = tvUserName.getText().toString();
                //String account = tvAccount.getText().toString();
                String friend_account = tvAccount.getText().toString();

//                friend.setImagePath(imagePath);
                friend.setName(name);
                friend.setAccount(account);
                friend.setFriend_account(friend_account);

                // Get the data from an ImageView as bytes
                ivUser.setDrawingCacheEnabled(true);
                ivUser.buildDrawingCache();


                /// 以下Friend存圖片 以上Notice Firebase

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();
//
////               // Create a reference to "mountains.jpg"
//                StorageReference mountainsRef = storageRef.child(account+"jpg");
//
//              // Create a reference to 'images/mountains.jpg'
                final String imagePath2 = "/images_friends/" + friend_account + ".png";
                StorageReference friendImagesRef = storageRef.child(imagePath2);
                tvUserImagrPath.setText(imagePath2);

                String imagePath = tvUserImagrPath.getText().toString();

                friend.setImagePath(imagePath);

                ivUser.setDrawingCacheEnabled(true);
                ivUser.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) ivUser.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = friendImagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });

                /// 以上Friend存圖片 以下Notice存圖片

                final String imagePath3 = "/images_notices/" + account + ".png";
                StorageReference noticeImagesRef = storageRef.child(imagePath3);
                tvUserImagrPath2.setText(imagePath3);

                String nImagePath = tvUserImagrPath2.getText().toString();

                notice.setnImagePath(nImagePath);

                ivUser.setDrawingCacheEnabled(true);
                ivUser.buildDrawingCache();
                Bitmap bitmap2 = ((BitmapDrawable) ivUser.getDrawable()).getBitmap();
                ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                byte[] data2 = baos.toByteArray();

                UploadTask uploadTask2 = noticeImagesRef.putBytes(data2);
                uploadTask2.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot2) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
                });
                addOrReplace(notice);
                addOrReplace(friend);
            }
        });
    }




    // 新增或修改Firestore上的通知 // Notice
    private void addOrReplace(final Notice notice) {
        // 如果Firestore沒有該ID的Document就建立新的，已經有就更新內容。
        // 先新增空的資料document,取得firebase給的ID,用id來擷取圖片資料路徑後,再回去修改空的物件
        db.collection("notices").document(notice.getId()).set(notice)//先產生document產生id,再依照id去指定哪個document
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String message = getString(R.string.textInserted2);
//                                    + " with ID: " + notice.getId();
                            Log.d(TAG, message);
//                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                            // 新增完畢回上頁
//                            Navigation.findNavController(ivUser).popBackStack();
                        } else {
                            String message = task.getException() == null ?
                                    getString(R.string.textInsertFail2) :
                                    task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 新增或修改Firestore上的好友 // Friend
    private void addOrReplace(final Friend friend) {
        // 如果Firestore沒有該ID的Document就建立新的，已經有就更新內容。
        // 先新增空的資料document,取得firebase給的ID,用id來擷取圖片資料路徑後,再回去修改空的物件
        db.collection("friends").document(friend.getId()).set(friend)//先產生document產生id,再依照id去指定哪個document
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //String message = getString(R.string.textInserted2);
//                                    + " with ID: " + notice.getId();
                            //Log.d(TAG, message);
//                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                            // 新增完畢回上頁
//                            Navigation.findNavController(ivUser).popBackStack();
                        } else {
                            String message = task.getException() == null ?
                                    getString(R.string.textInsertFail2) :
                                    task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * 下載Firebase storage的照片並顯示在ImageView上
     */
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
