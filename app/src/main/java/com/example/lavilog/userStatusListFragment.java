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
    ArrayList<User> users = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        activity.setTitle("使用者列表");
        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
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
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                    User user = documentSnapshot.toObject(User.class);
                    if (!user.getStatus().equals("1")) {
                        users.add(user);
                    }
                }
                rvUserList.setLayoutManager(new LinearLayoutManager(activity));
                rvUserList.setAdapter(new userAdapter(activity, users));
            }
        });
    }
    private class userAdapter extends RecyclerView.Adapter<userAdapter.userViewHolder>{
        Context context;
        List<User> users;

        userAdapter(Context context,List<User> users){
            this.context=context;
            this.users=users;
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
            showImage(holder.ivUserList,imagePath);
            holder.tvUserName.setText(user.getName());
            holder.tvUserAccount.setText(user.getAccount());
            if (user.getStatus().equals("0")){
                holder.tvUserStatus.setText("狀態 : 正常");
                holder.btBlockadeCancel.setVisibility(View.GONE);
                holder.btBlockade.setVisibility(View.VISIBLE);
                holder.CLUser.setBackgroundColor(Color.rgb(213, 213, 213));
            }else{
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
                    builder1.setMessage("確定封鎖使用者"+account)
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
                    builder2.setMessage("確定解除封鎖使用者"+account)
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

        class userViewHolder extends RecyclerView.ViewHolder{
            ImageView ivUserList;
            TextView tvUserName,tvUserAccount,tvUserStatus;
            Button btBlockadeCancel,btBlockade;
            CardView cardViewUser;
            ConstraintLayout CLUser;

            userViewHolder(View itemView){
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
