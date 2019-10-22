package com.example.lavilog.SearchUserId;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class SearchUserIdFragment extends Fragment {
    private static final String TAG = "TAG_UserSearchF";
    private Activity activity;
//    private ImageView ivUser;
//    private TextView tvUserName;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private static List<User> users;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("加入帳號");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user_id, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvCommodityDel);
        searchView = view.findViewById(R.id.svCommodityDel);
    }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        // 加上異動監聽器
        listenToSpots();
//        users = firebase的值
        UserAdapter adapter = (UserAdapter) recyclerView.getAdapter();
        if (adapter == null) { // 如果適配器等於空值就建立新的
            recyclerView.setAdapter(new UserAdapter(activity, users));
        } else { // 如果適配器不等於空值就用原本的
            adapter.setUsers(users);
            adapter.notifyDataSetChanged();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 必須能監聽到searchView內文字的改變

            // onQueryTextSubmit 打完才搜尋
            @Override
            public boolean onQueryTextSubmit(String newText) { // 打完才搜尋
                // 當user輸入東西,searchＶiew會傳回內容（newText)
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerView.setAdapter(new UserAdapter(activity, users));
                SearchUserIdFragment.UserAdapter adapter = (SearchUserIdFragment.UserAdapter) recyclerView.getAdapter();
                // 要做SearchView時,須先做好SearchView的內容,本文為recyclerView
                List<User> searchUserIds = new ArrayList<>();
                // 為了搜集依照使用者提供的關鍵字,須設定一個新的List
                // 將符合條件的data匯入
                // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                if (adapter != null) {                  // 如果適配器 不等於 空值
                    // 如果搜尋條件為空字串，就顯示原始資料(User原始為空白)；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {            // 如果搜尋條件為空字串
                        if(users!=null) {               // 如果user資料 不等於 空值
                            users.removeAll(users);     // 全部刪掉
                            adapter.setUsers(users);
                        }
                    } else {
                        // users = getUsers();
                        // users= getusers();
                        // 搜尋原始資料 equals
                        for (User user : users) {

                            // 搜尋名字
//                            if (user.getName().toUpperCase().equals(newText.toUpperCase())) {
//                                // contain為比對內容的動作,是否包含關鍵字
//                                searchUserIds.add(user);
//                            }

                            // 搜尋帳號
                            if (user.getAccount().toUpperCase().equals(newText.toUpperCase())) {
                                // contain為比對內容的動作,是否包含關鍵字
                                searchUserIds.add(user);
                            }
                        }
                        adapter.setUsers(searchUserIds);
                    }
                }
                return false;
                // return false代表事件沒有被處理到,需要往下走,不要終止
                // webView的onKeyDown同理,返回上一頁是該返回網頁還是widget
            }

            // onQueryTextChange 打字就搜尋
            @Override
            public boolean onQueryTextChange(String newText) { // 打字就搜尋
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerView.setAdapter(new UserAdapter(activity, users));
                SearchUserIdFragment.UserAdapter adapter = (SearchUserIdFragment.UserAdapter) recyclerView.getAdapter();
                List<User> searchUserIds = new ArrayList<>();
                if (adapter != null) {                  // 如果適配器 不等於 空值
                    // 如果搜尋條件為空字串，就顯示原始資料(User原始為空白)；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {            // 如果搜尋條件為空字串
                        if (users != null) {            // 如果user資料 不等於 空值
                            users.removeAll(users);     // 清空資料
                            adapter.setUsers(users);
                        }
                    } else {
                        adapter.setUsers(searchUserIds);

                    }
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
        db.collection("users").get() // 把users裡面所有的每一筆資料都取出
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // snapshot 螢幕截圖,複製品,快照
                        if (task.isSuccessful() && task.getResult() != null) {
                            // 拿到資料 ,就去跑for each取得每一筆資料
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // result內含QuerySnapshot
                                users.add(document.toObject(User.class));
                                // 類似gson.fromJson,原本是要給key取值,提供我們認為document的類別,spot.class
                                // 讓系統去依照finders的格式去解析document

                            }
                            recyclerView.setAdapter(new UserAdapter(activity, users));
                        }  else {
                            String message = task.getException() == null ?
                                    getString(R.string.textNo2) :
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
            registration = db.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    Log.d(TAG, "Users change.");

                    if (e == null) { // 沒有錯誤的話
                        if (snapshots != null && snapshots.size() > 0) {
                            List<User> users = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                users.add(document.toObject(User.class));
                            }
                            UserAdapter userAdapter = (UserAdapter) recyclerView.getAdapter();
                            if (userAdapter != null) {
                                userAdapter.setUsers(users);
                                SearchUserIdFragment.users = users;
                                userAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            });
        }
    }

    private class UserAdapter extends RecyclerView.Adapter<SearchUserIdFragment.UserAdapter.MyViewHolder> {
        Context context;
        List<User> users;

        UserAdapter(Context context,List<User> users) {
            this.context = context;
            this.users = users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
        // 須依照SearchView的選擇調整users


        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivUser;
            TextView tvUserName;

            MyViewHolder(View itemView) {
                super(itemView);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvUserName = itemView.findViewById(R.id.tvAdmName);
            }
        }

        @Override
        public int getItemCount() {
            if(users == null){
                return 0;
            }
            return users.size();
        }

        @NonNull
        @Override
        public SearchUserIdFragment.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.from(context).inflate(R.layout.user_id_item_view, parent, false);
            return new SearchUserIdFragment.UserAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final User user = users.get(position);
            if (user.getImagePath() == null) { // 因為有分文字資料跟圖檔,所以先確認圖檔路徑是不是空值
                                               // 有值就要去抓圖,沒值就show沒檔的預設圖片
                holder.ivUser.setImageResource(R.drawable.no_image);
            } else {
                showImage(holder.ivUser, user.getImagePath());
            }
            holder.tvUserName.setText(user.getName());

            // 點選會開啟好友頁面
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    Navigation.findNavController(v) //searchView
                            .navigate(R.id.action_searchUserIdFragment_to_searchUserIdResultFragment, bundle);

                }
            });
        }
    }

    // 假資料
//    private List<User> getUsers() {
//        List<User> users = new ArrayList<>();
//        users.add(new User(R.drawable.mothersoup1, "user1"));
//        users.add(new User(R.drawable.mothersoup1, "user2"));
//        users.add(new User(R.drawable.mothersoup1, "user3"));
//        users.add(new User(R.drawable.mothersoup1, "user33"));
//        return users;
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
}
