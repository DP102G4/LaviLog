package com.example.lavilog;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.RED;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class userStatusListFragment extends Fragment {
    Activity activity;
    FirebaseFirestore db;
    FirebaseStorage storage;
    RecyclerView rvUserList;
    SearchView svUserStatus;
    Spinner spStatus;
    Context context;
    ArrayList<User> users = new ArrayList<>();
    ArrayList<User> usersNoBlock = new ArrayList<>();
    ArrayList<User> usersBlock = new ArrayList<>();
    List<User> searchUsers = new ArrayList<>();
    String searchViewText = "";
    //    userAdapter userAdapter;
    int position = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        activity.setTitle("使用者列表");
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        context = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_status_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvUserList = view.findViewById(R.id.rvUserList);
        svUserStatus = view.findViewById(R.id.svUserStatus);
        spStatus = view.findViewById(R.id.spStatus);
        ArrayAdapter<CharSequence> nAdapter = ArrayAdapter.createFromResource(
                context, R.array.blockage_spinner, android.R.layout.simple_spinner_dropdown_item);
//        nAdapter.setDropDownViewResource(
//                android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(nAdapter);

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (!user.getStatus().equals("1")) {
                        users.add(user);
                    }
                }
                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                rvUserList.setAdapter(new userAdapter(activity, users));
            }
        });

        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(activity,"i="+i,Toast.LENGTH_SHORT).show();
                switch (i) {
                    case 0://下拉選單選擇全部使用者
                        if (position != 0 ) {
                            users = new ArrayList<>();
                            if (searchViewText.isEmpty()) {

                                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            User user = documentSnapshot.toObject(User.class);
                                            if (!user.getStatus().equals("1")) {
                                                users.add(user);
                                            }
                                        }
                                        rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                        rvUserList.setAdapter(new userAdapter(activity, users));
                                    }
                                });
//                                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
//                                rvUserList.setAdapter(new userAdapter(activity, users));
                            } else {

                                db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        users = new ArrayList<>();
                                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            User user = documentSnapshot.toObject(User.class);
                                            if (!user.getStatus().equals("1")) {
                                                for (User sruser : searchUsers) {
                                                    if(user.getAccount().equals(sruser.getAccount())){
                                                        users.add(sruser);
                                                    }
                                                }
                                            }
                                        }
                                        rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                        rvUserList.setAdapter(new userAdapter(activity, users));
                                    }
                                });
//                                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
//                                rvUserList.setAdapter(new userAdapter(activity, searchUsers));
                            }
                            position = i;
                        }
                        break;
                    case 1://下拉選單選擇未封鎖使用者
                        if (position != 1) {
                            if (searchViewText.isEmpty()) {
                                db.collection("users").whereEqualTo("status", "0").
                                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                        usersNoBlock = new ArrayList<>();
                                        userAdapter adapter = (userAdapter) rvUserList.getAdapter();
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            User userNoBlock = documentSnapshot.toObject(User.class);
                                            usersNoBlock.add(userNoBlock);
                                        }
                                        adapter.setUsers(usersNoBlock);
                                        rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                        rvUserList.setAdapter(new userAdapter(activity, usersNoBlock));
                                    }
                                });
                            } else {
                                db.collection("users").whereEqualTo("status", "0").
                                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                        usersNoBlock = new ArrayList<>();
                                        userAdapter adapter = (userAdapter) rvUserList.getAdapter();
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            User userNoBlock = documentSnapshot.toObject(User.class);
                                            for (User user : searchUsers) {
                                                if (user.getAccount().equals(userNoBlock.getAccount())) {
                                                    usersNoBlock.add(userNoBlock);
                                                }
                                            }
                                        }
                                        adapter.setUsers(usersNoBlock);
                                        rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                        rvUserList.setAdapter(new userAdapter(activity, usersNoBlock));
                                    }
                                });
                            }
                            position = i;
                        }
                        break;
                    case 2://下拉選單選擇封鎖使用者
                        if (position != 2) {
                            if (searchViewText.isEmpty()) {
                                db.collection("users").whereEqualTo("status", "2").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                usersBlock = new ArrayList<>();
                                                userAdapter adapter = (userAdapter) rvUserList.getAdapter();
                                                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                    User userBlock = documentSnapshot.toObject(User.class);
                                                    usersBlock.add(userBlock);
                                                }
                                                adapter.setUsers(usersBlock);
                                                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                                rvUserList.setAdapter(new userAdapter(activity, usersBlock));
                                            }
                                        });
                            } else {
                                db.collection("users").whereEqualTo("status", "2").
                                        get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                        usersBlock = new ArrayList<>();
                                        userAdapter adapter = (userAdapter) rvUserList.getAdapter();
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            User userBlock = documentSnapshot.toObject(User.class);
                                            for (User user : searchUsers) {
                                                if (user.getAccount().equals(userBlock.getAccount())) {
                                                    usersBlock.add(userBlock);
                                                }
                                            }
                                        }
                                        adapter.setUsers(usersBlock);
                                        rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                        rvUserList.setAdapter(new userAdapter(activity, usersBlock));
                                    }
                                });
                            }
                            position = i;
                        }
                        break;
                    default:
                        Toast.makeText(activity, "資料庫設錯status了,快檢查", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        svUserStatus.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 必須能監聽到searchView內文字的改變

            // onQueryTextSubmit 打完才搜尋
            @Override
            public boolean onQueryTextSubmit(String newText) { // 打完才搜尋
                // 當user輸入東西,searchＶiew會傳回內容（newText)
//                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
//                rvUserList.setAdapter(new userAdapter(activity, users));
//                userAdapter userAdapter = (userAdapter) rvUserList.getAdapter();
//                // 要做SearchView時,須先做好SearchView的內容,本文為recyclerView
//                List<User> searchUsers = new ArrayList<>();
//                // 為了搜集依照使用者提供的關鍵字,須設定一個新的List
//                // 將符合條件的data匯入
//                // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
//                if (userAdapter != null) {                  // 如果適配器 不等於 空值
//                    // 如果搜尋條件為空字串，就顯示原始資料(User原始為空白)；否則就顯示搜尋後結果
//                    if (newText.isEmpty()) {            // 如果搜尋條件為空字串
//                        if (users != null) {               // 如果user資料 不等於 空值
//                            userAdapter.setUsers(users);
//                        }
//                    } else {
//                        // users = getUsers();
//                        // users= getusers();
//                        // 搜尋原始資料 equals
//                        for (User user : users) {
//                            // 搜尋名字
////                            if (user.getName().toUpperCase().equals(newText.toUpperCase())) {
////                                // contain為比對內容的動作,是否包含關鍵字
////                                searchUserIds.add(user);
////                            }
//
//                            // 搜尋帳號
//                            if (user.getAccount().toUpperCase().contains(newText.toUpperCase())) {
//                                // contain為比對內容的動作,是否包含關鍵字
//                                searchUsers.add(user);
//                            }
//                        }
//                        userAdapter.setUsers(searchUsers);
//                    }
//                }
                return false;
                // return false代表事件沒有被處理到,需要往下走,不要終止
                // webView的onKeyDown同理,返回上一頁是該返回網頁還是widget
            }

            // onQueryTextChange 打字就搜尋
            @Override
            public boolean onQueryTextChange(String newText) { // 打字就搜尋
                searchViewText = newText;
                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                rvUserList.setAdapter(new userAdapter(activity, users));
                userAdapter adapter = (userAdapter) rvUserList.getAdapter();
                searchUsers = new ArrayList<>();
                if (adapter != null) {                  // 如果適配器 不等於 空值
                    // 如果搜尋條件為空字串，就顯示原始資料(User原始為空白)；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {            // 如果搜尋條件為空字串
                        if (users != null) {               // 如果user資料 不等於 空值
                            switch (position) {
                                case 0:
                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            users = new ArrayList<>();
                                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                User user = documentSnapshot.toObject(User.class);
                                                if (!user.getStatus().equals("1")) {
                                                    users.add(user);
                                                }
                                            }
                                            rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                            rvUserList.setAdapter(new userAdapter(activity, users));
                                        }
                                    });
//                                    adapter.setUsers(users);
                                    break;
                                case 1:
                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            users = new ArrayList<>();
                                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                User user = documentSnapshot.toObject(User.class);
                                                if ((!user.getStatus().equals("1") && (!user.getStatus().equals("2")))){
                                                    users.add(user);
                                                }
                                            }
                                            rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                            rvUserList.setAdapter(new userAdapter(activity, users));
                                        }
                                    });
//                                    adapter.setUsers(usersNoBlock);
                                    break;
                                case 2:
                                    db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            users = new ArrayList<>();
                                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                User user = documentSnapshot.toObject(User.class);
                                                if ((!user.getStatus().equals("1") && (!user.getStatus().equals("0")))){
                                                    users.add(user);
                                                }
                                            }
                                            rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                                            rvUserList.setAdapter(new userAdapter(activity, users));
                                        }
                                    });
//                                    adapter.setUsers(usersBlock);
                                    break;
                                default:
                                    Toast.makeText(activity, "資料庫設錯了,快檢查", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        // users = getUsers();
                        // users= getusers();
                        // 搜尋原始資料 equals
                        switch (position) {
                            case 0:
                                for (User user : users) {
                                    // 搜尋帳號
                                    if (user.getAccount().toUpperCase().contains(newText.toUpperCase())) {
                                        // contain為比對內容的動作,是否包含關鍵字
                                        searchUsers.add(user);
                                    }
                                }
                                adapter.setUsers(searchUsers);
                                break;
                            case 1:
                                for (User user : usersNoBlock) {
                                    // 搜尋帳號
                                    if (user.getAccount().toUpperCase().contains(newText.toUpperCase())) {
                                        // contain為比對內容的動作,是否包含關鍵字
                                        searchUsers.add(user);
                                    }
                                }
                                adapter.setUsers(searchUsers);
                                break;
                            case 2:
                                for (User user : usersBlock) {
                                    // 搜尋帳號
                                    if (user.getAccount().toUpperCase().contains(newText.toUpperCase())) {
                                        // contain為比對內容的動作,是否包含關鍵字
                                        searchUsers.add(user);
                                    }
                                }
                                adapter.setUsers(searchUsers);
                                break;
                            default:
                                Toast.makeText(activity, "資料庫設錯了,快檢查", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                return false;
                // return false代表事件沒有被處理到,需要往下走,不要終止
                // webView的onKeyDown同理,返回上一頁是該返回網頁還是widget
            }
        });
    }

    private class userAdapter extends RecyclerView.Adapter<userAdapter.userViewHolder> {
        Context context;
        List<User> users;

        userAdapter(Context context, List<User> users) {
            this.context = context;
            this.users = users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        @NonNull
        @Override
        public userAdapter.userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.user_list_item, parent, false);
            return new userAdapter.userViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final userAdapter.userViewHolder holder, final int position) {
            final User user = users.get(position);
            String imagePath = user.getImagePath();
            showImage(holder.ivUserList, imagePath);
            holder.tvUserName.setText(user.getName());
            holder.tvUserAccount.setText(user.getAccount());
            if (user.getStatus().equals("0")) {
                holder.tvUserStatus.setText("狀態 : 正常");
                holder.btBlockadeCancel.setVisibility(View.GONE);
                holder.btBlockade.setVisibility(View.VISIBLE);
                holder.CLUser.setBackgroundColor(Color.rgb(213, 213, 213));
            } else {
                holder.tvUserStatus.setText("狀態 : 封鎖");
                holder.CLUser.setBackgroundColor(RED);
                holder.btBlockade.setVisibility(View.GONE);
                holder.btBlockadeCancel.setVisibility(View.VISIBLE);
            }

            holder.btBlockade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String account = holder.tvUserAccount.getText().toString();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                    builder1.setMessage("確定封鎖使用者" + account)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    User userBlock = users.get(position);
                                    userBlock.setStatus("2");
                                    db.collection("users").document(userBlock.getId()).set(userBlock);
                                    notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
// User cancelled the dialog
                                }
                            });
// Create the AlertDialog object and return it
                    builder1.show();
                }
            });

            holder.btBlockadeCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String account = holder.tvUserAccount.getText().toString();
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(activity);
                    builder2.setMessage("確定解除封鎖使用者" + account)
                            .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    User userBlockCancel = users.get(position);
                                    userBlockCancel.setStatus("0");
                                    db.collection("users").document(userBlockCancel.getId()).set(userBlockCancel);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
// User cancelled the dialog
                                }
                            });
                    builder2.show();
                }
            });


        }

        class userViewHolder extends RecyclerView.ViewHolder {
            ImageView ivUserList;
            TextView tvUserName, tvUserAccount, tvUserStatus;
            Button btBlockadeCancel, btBlockade;
            CardView cardViewUser;
            ConstraintLayout CLUser;

            userViewHolder(View itemView) {
                super(itemView);
                ivUserList = itemView.findViewById(R.id.ivUserList);
                tvUserName = itemView.findViewById(R.id.tvUserName);
                tvUserAccount = itemView.findViewById(R.id.tvUserAccount);
                tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
                btBlockade = itemView.findViewById(R.id.btBlockade);
                btBlockadeCancel = itemView.findViewById(R.id.btBlockadeCancel);
                cardViewUser = itemView.findViewById(R.id.cardViewUser);
                CLUser = itemView.findViewById(R.id.CLUser);
            }
        }
    }

    private void showImage(final ImageView imageView, final String imagePath) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(imagePath);
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
                                    getString(R.string.textImageDownloadFail) + ": " + imagePath :
                                    task.getException().getMessage() + ": " + imagePath;
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}