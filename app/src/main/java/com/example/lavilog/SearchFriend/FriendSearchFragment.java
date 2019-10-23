package com.example.lavilog.SearchFriend;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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

public class FriendSearchFragment extends Fragment {
    private static final String TAG = "TAG_FriendSearchF";
    private Activity activity;
//    private ImageView ivFriend;
//    private TextView tvFriendName;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private static List<Friend> friends;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("好友管理");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_search, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        friends = getFriends();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvCommodityDel);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        
        // recyclerView.setAdapter(new FriendAdapter(activity, friends));

        searchView = view.findViewById(R.id.svCommodityDel);
   }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        // 加上異動監聽器
        listenToSpots();
//        friends = firebase的值
        FriendAdapter adapter = (FriendAdapter) recyclerView.getAdapter();
        if (adapter == null) { // 如果適配器等於空值就建立新的
            recyclerView.setAdapter(new FriendAdapter(activity, friends));
        } else { // 如果適配器不等於空值就用原本的
            adapter.setFriends(friends);
            adapter.notifyDataSetChanged();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 必須能監聽到searchView內文字的改變

            // onQueryTextSubmit 打完才搜尋
            @Override
            public boolean onQueryTextSubmit(String query) { // 打完才搜尋
                return false;
            }

            // onQueryTextChange 打字就搜尋
            @Override
            public boolean onQueryTextChange(String newText) { //打字就搜尋
                // 當user輸入東西,searchＶiew會傳回內容（newText)

                FriendSearchFragment.FriendAdapter adapter = (FriendSearchFragment.FriendAdapter) recyclerView.getAdapter();
                // 要做SearchView時,須先做好SearchView的內容,本文為recyclerView
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setFriends(friends);
                    } else {
                        List<Friend> searchFriends = new ArrayList<>();
                        // 為了搜集依照使用者提供的關鍵字,須設定一個新的List
                        // 將符合條件的data匯入
                        // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                        for (Friend friend : friends) {
                            if (friend.getName().toUpperCase().contains(newText.toUpperCase())) {
                                // contain為比對內容的動作,是否包含關鍵字
                                searchFriends.add(friend);
                            }
                        }
                        adapter.setFriends(searchFriends);

                    }
                    adapter.notifyDataSetChanged();
                    // return true;
                    // 有處理就可以return True,終止動作;
                    // data改變,但View不會自動變,要用notifyDataSetChanged
                    // 會再去呼叫getItemCount,若得到1,下方的bindViewHolder及onCreateViewHolder
                    // 就會只執行一次
                }
                return false;
                // return false代表事件沒有被處理到,需要往下走,不要終止
                // webView的onKeyDown同理,返回上一頁是該返回網頁還是widget
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        // 解除異動監聽器
        registration.remove();
        registration = null;
        // 沒用到就不用再繼續監聽,移除並設成空值
    }

    /** 顯示所有好友資訊 */
    private void showAll() {
        db.collection("friends").get() // 把friends裡面所有的每一筆資料都取出
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // snapshot 螢幕截圖,複製品,快照
                        if (task.isSuccessful() && task.getResult() != null) {
                            // 拿到資料 ,就去跑for each取得每一筆資料
                            List<Friend> friends = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // result內含QuerySnapshot
                                friends.add(document.toObject(Friend.class));
                                // 類似gson.fromJson,原本是要給key取值,提供我們認為document的類別,spot.class
                                // 讓系統去依照finders的格式去解析document

//                                測試
//                                Friend friend1=(document.toObject(Friend.class));
//                                friends.add(friend1);
//                                if (friends!=null){
//                                    textView123.setText(friends.toString());
//                                }
                            }
                            recyclerView.setAdapter(new FriendAdapter(activity, friends));
                        }  else {
                            String message = task.getException() == null ?
                                    getString(R.string.textNo) :
                                    task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /** 監聽資料是否發生異動，有則同步更新 */
    private void listenToSpots() {
        if (registration == null) { // 確認註冊監聽器物件是否空值
            registration = db.collection("friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    Log.d(TAG, "Friends change.");

                    if (e == null) { // 沒有錯誤的話
                        if (snapshots != null && snapshots.size() > 0) {
                            List<Friend> friends = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                friends.add(document.toObject(Friend.class));
                            }
                            FriendAdapter friendAdapter = (FriendAdapter) recyclerView.getAdapter();
                            if (friendAdapter != null) {
                                friendAdapter.setFriends(friends);
                                FriendSearchFragment.friends = friends;
                                friendAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            });
        }
    }

    private class FriendAdapter extends RecyclerView.Adapter<FriendSearchFragment.FriendAdapter.MyViewHolder> {
        Context context;
        List<Friend> friends;

        FriendAdapter(Context context, List<Friend> friendList) {
            this.context = context;
            this.friends = friendList;
        }

        public void setFriends(List<Friend> friends) {
            this.friends = friends;
        }
        // 須依照SearchView的選擇調整friends

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFriend;
            TextView tvFriendName;

            MyViewHolder(View itemView) {
                super(itemView);
                ivFriend = itemView.findViewById(R.id.ivAccountPhoto);
                tvFriendName = itemView.findViewById(R.id.tvAccountName);

            }
        }

        @Override
        public int getItemCount() {
            if(friends == null){
                return 0;
            }
            return friends.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.friend_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Friend friend = friends.get(position);
            if (friend.getImagePath() == null) { // 因為有分文字資料跟圖檔,所以先確認圖檔路徑是不是空值
                                                 // 有值就要去抓圖,沒值就show沒檔的預設圖片
                holder.ivFriend.setImageResource(R.drawable.no_image);
            } else {
                showImage(holder.ivFriend, friend.getImagePath());
            }
            holder.tvFriendName.setText(friend.getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("friend", friend);
                    Navigation.findNavController(v)
                            .navigate(R.id.action_friendSearchFragment_to_friendSearchResultFragment, bundle);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                    builder1.setMessage("確定刪除好友 ?")
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    delete(friend);
                                    Toast.makeText(activity, "已刪除好友", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder1.show();
                    return false;
                }
            });
        }
    }

    // 假資料
//    private List<Friend> getFriends() {
//        List<Friend> friends = new ArrayList<>();
//        friends.add(new Friend("Friend1","1"));
//
//        return friends;
//    }

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
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void delete(final Friend friend) {
        // 刪除Firestore內的好友資料
        db.collection("friends").document(friend.getId()).delete()//哪個friend就是哪個document
                .addOnCompleteListener(new OnCompleteListener<Void>() {//監聽上面的圖檔有沒有被刪除了,若完成,執行下方的刪除路徑
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(activity, "好友已刪除", Toast.LENGTH_SHORT).show();
                            // 刪除該通知在Firebase storage對應的圖檔
                            if (friend.getImagePath() != null) { // 上面是刪掉圖檔而已,這邊要來刪路徑
                                storage.getReference().child(friend.getImagePath()).delete() // 刪除firestore完整路徑
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, getString(R.string.textImageDeleted));
                                                }
                                            }
                                        });
                            }
                            showAll();
                        } else {
                            Toast.makeText(activity, R.string.textDeleteFail, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
