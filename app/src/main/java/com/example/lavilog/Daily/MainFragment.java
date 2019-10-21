package com.example.lavilog.Daily;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.MainActivity;
import com.example.lavilog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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


public class MainFragment extends Fragment {

    private  static  final String TAG = "TAG_Mainfragment";
    private Activity activity;
    private ImageButton ibDaily;
    private RecyclerView logBookRecyclerView;
    private FloatingActionButton btAdd;
    private List<LogBook> logBookList;
    private Button btFirst,btLast;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private ListenerRegistration registration;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("LaviLog");
        return inflater.inflate(R.layout.fragment_main, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ibDaily = view.findViewById(R.id.ibDaily);
//        ibDaily.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_dailyFragment);
//            }
//        });
//        btAdd = view.findViewById(R.id.btAdd);

        btFirst = view.findViewById(R.id.btFirst);
        btFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logBookRecyclerView.smoothScrollToPosition(0);

            }
        });
        btLast = view.findViewById(R.id.btLast);
        btLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logBookRecyclerView.smoothScrollToPosition(+1);
            }
        });



        logBookRecyclerView = view.findViewById(R.id.logBookRecyclerView);
        logBookRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        /* 不處理捲動事件所以監聽器設為null */
        logBookRecyclerView.setOnFlingListener(null);
        /* 如果希望一次滑動一頁資料，要加上PagerSnapHelper物件 */
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(logBookRecyclerView);
    }



    @Override
    public void onStart() {
        super.onStart();
        showAll();
        listenToLogbook();
    }

    @Override
    public void onStop() {
        super.onStop();
        registration = null;

    }

    private void showAll() {
        db.collection("logBook").orderBy("image",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null){
                    List<LogBook> logBookList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()){
                        logBookList.add(document.toObject(LogBook.class));
                    }
                    logBookRecyclerView.setAdapter(new LogBookAdapter(activity,logBookList));
                }else {
                    String message = task.getException() == null? getString(R.string.textDeleteFail): task.getException().getMessage();
                    Log.e(TAG,message);
                    Toast.makeText(activity,"找不到日誌",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void listenToLogbook(){
        if(registration == null){
            db.collection("logBook").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    Log.e(TAG, "logbook change");
                    if (e == null) {
                        if (snapshots != null && snapshots.size() > 0) {
                            List<LogBook> logBookList = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                logBookList.add(document.toObject(LogBook.class));
                            }
                            LogBookAdapter logBookAdapter = (LogBookAdapter) logBookRecyclerView.getAdapter();
                            if (logBookAdapter != null) {
                                logBookAdapter.setLogBookList(logBookList);
                                logBookAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            });
        }
    }


    private class LogBookAdapter extends RecyclerView.Adapter<LogBookAdapter.LogBookViewHolder>{
        private Context context;
        private List<LogBook> logBookList;
        LogBookAdapter(Context context,List<LogBook> logBookList){
            this.context = context;
            this.logBookList = logBookList;
        }
        class LogBookViewHolder extends RecyclerView.ViewHolder {
            ImageView ivLogBook;
            TextView tvLogBookName;

            LogBookViewHolder(View itemview){
                super(itemview);
                ivLogBook = itemview.findViewById(R.id.ivLogBook);
                tvLogBookName = itemview.findViewById(R.id.tvLogBookName);
            }
        }

        @Override
        public int getItemCount() { return logBookList.size(); }
        public  void  setLogBookList(List<LogBook>logBookList){this.logBookList = logBookList;}

        @NonNull
        @Override
        public LogBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.item_logbook,parent,false);
            return new LogBookViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull LogBookViewHolder holder, int position) {
            final LogBook logBook = logBookList.get(position);
            if (logBook.getImage() == null){
                holder.ivLogBook.setImageResource(R.drawable.no_image);
            }else {
                showImage(holder.ivLogBook,logBook.getImage());
            }
            holder.tvLogBookName.setText(String.valueOf(logBook.getName()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_dailyFragment);
                }
            });

        }
        private void showImage(final  ImageView ivLogBook,final String path){
            final  int ONE_MEGABYTE = 1024 * 1024 ;
            StorageReference imageRef = storage.getReference().child(path);
            imageRef.getBytes((ONE_MEGABYTE)).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful() && task.getResult() != null){
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        ivLogBook.setImageBitmap(bitmap);
                    }else {
                        String message = task.getException() == null?
                                getString(R.string.textImageDownloadFail)+":"+path:task.getException().getMessage()+":"+path;
                        Log.e(TAG,message);
                        Toast.makeText(activity,"找不到日誌",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}