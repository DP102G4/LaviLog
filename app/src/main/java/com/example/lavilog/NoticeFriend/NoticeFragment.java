package com.example.lavilog.NoticeFriend;


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

import com.example.lavilog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class NoticeFragment extends Fragment {
    private static final String TAG = "TAG_NoticeFragment";
    private Activity activity;
    private RecyclerView rvNotice;
    //private static List<Notice> notices;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;

    private ImageView ivNotice;
    private TextView tvMessage, tvTime, textView21;
    private TextView tvMessage2, tvNoticeNotice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("通知");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notice, container, false);
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
        rvNotice = view.findViewById(R.id.rvNotice);
        rvNotice.setLayoutManager(new LinearLayoutManager(activity));

        tvNoticeNotice = view.findViewById(R.id.tvNoticeNotice);
    }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        // 加上異動監聽器
        listenToSpots();
    }

    @Override
    public void onStop() {
        super.onStop();
        // 解除異動監聽器
        registration.remove();
        registration = null;
        // 沒用到就不用再繼續監聽,移除並設成空值
    }

    /** 顯示所有通知資訊 */
    private void showAll() {
        db.collection("notices").orderBy("noticeTime", Query.Direction.DESCENDING).get() // 把裡面所有的每一筆資料都取出
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {       // snapshot 螢幕截圖,複製品,快照
                        if (task.isSuccessful() && task.getResult() != null) {        // 拿到資料 ,就去跑for each取得每一筆資料
                            List<Notice> notices = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) { // result內含QuerySnapshot
                                notices.add(document.toObject(Notice.class)); // 類似gson.fromJson,原本是要給key取值,提供我們認為document的類別,spot.class
                            }                                                         // 讓系統去依照finders的格式去解析document
                            rvNotice.setAdapter(new NoticeFragment.NoticeAdapter(activity, notices));
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
            registration = db.collection("notices").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    Log.d(TAG, "Friends change.");

                    if (e == null) { // 沒有錯誤的話
                        if (snapshots != null && snapshots.size() > 0) {
                            List<Notice> notices = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                notices.add(document.toObject(Notice.class));
                            }
                            NoticeFragment.NoticeAdapter spotAdapter = (NoticeFragment.NoticeAdapter) rvNotice.getAdapter();
                            if (spotAdapter != null) {
                                spotAdapter.setNotices(notices);
                                spotAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            });
        }
    }

    private class NoticeAdapter extends RecyclerView.Adapter<NoticeFragment.NoticeAdapter.MyViewHolder> {
        Context context;
        List<Notice> notices;

        NoticeAdapter(Context context, List<Notice> noticeList) {
            this.context = context;
            this.notices = noticeList;
        }

        public void setNotices(List<Notice> notices) {
            this.notices = notices;
        }
        // 須依照SearchView的選擇調整friends

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivNotice;
            TextView tvMessage, tvTime, tvMessage2;

            MyViewHolder(View itemView) {
                super(itemView);
                ivNotice = itemView.findViewById(R.id.ivNotice);
                tvMessage = itemView.findViewById(R.id.tvMessage);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvMessage2 = itemView.findViewById(R.id.tvMessage2);

            }
        }

        @Override
        public int getItemCount() {
            return notices.size();
        }

        @NonNull
        @Override
        public NoticeFragment.NoticeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.notice_item_view, parent, false);
            return new NoticeFragment.NoticeAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NoticeFragment.NoticeAdapter.MyViewHolder holder, int position) {
            final Notice notice = notices.get(position);
            if (notice.getnImagePath() == null) { // 因為有分文字資料跟圖檔,所以先確認圖檔路徑是不是空值
//                 有值就要去抓圖,沒值就show沒檔的預設圖片
                holder.ivNotice.setImageResource(R.drawable.no_image);
            }
            else {
                showImage(holder.ivNotice, notice.getnImagePath());
            }
            holder.tvMessage.setText(notice.getNoticeMessage());
            holder.tvTime.setText(notice.getNoticeTime());
            holder.tvMessage2.setText(notice.getNoticeMessage2());

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    delete(notice);
                    return false;
                }
            });

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("notice", notice);
//                    Navigation.findNavController(v)
//                            .navigate(R.id.action_friendSearchFragment_to_friendSearchResultFragment, bundle);
//                }
//            });
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
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void delete(final Notice notice) {
        // 刪除Firestore內的通知資料
        db.collection("notices").document(notice.getId()).delete()//哪個notice就是哪個document
                .addOnCompleteListener(new OnCompleteListener<Void>() {//監聽上面的圖檔有沒有被刪除了,若完成,執行下方的刪除路徑
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, R.string.textDeletedNotice, Toast.LENGTH_SHORT).show();
                            // 刪除該通知在Firebase storage對應的圖檔
                            if (notice.getnImagePath() != null) { // 上面是刪掉圖檔而已,這邊要來刪路徑
                                storage.getReference().child(notice.getnImagePath()).delete() // 刪除firestore完整路徑
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