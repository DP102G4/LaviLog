package com.example.lavilog.Daily;


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


public class AlbumFragment extends Fragment {
    private  static  final String TAG = "TAG_Mainfragment";
    private Activity activity;
    private RecyclerView rvDailyAlbum;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_album, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvDailyAlbum = view.findViewById(R.id.rvDailyAlbum);
        rvDailyAlbum.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        listenToDaily();

    }



    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
        registration = null;
    }

    private void showAll() {
        db.collection("article").orderBy("textClock", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    List<Answer> answers = new ArrayList<>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        answers.add(document.toObject(Answer.class));
                    }
                    rvDailyAlbum.setAdapter(new DailyAlbumAdapter(activity,answers));
                }else {
                    String message = task.getException() == null?
                            getString(R.string.textNoDailyFound):
                            task.getException().getMessage();
                    Log.e(TAG,message);
                    Toast.makeText(activity,"找不到日誌",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private void listenToDaily(){
        if (registration == null){
            registration = db.collection("article").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    Log.e(TAG, "article change");
                    if (e == null){
                       if(snapshots != null && snapshots.size() > 0){
                           List<Answer>answers = new ArrayList<>();
                           for (DocumentSnapshot document: snapshots.getDocuments()){
                               answers.add(document.toObject(Answer.class));
                           }
                           DailyAlbumAdapter dailyAlbumAdapter = (DailyAlbumAdapter)rvDailyAlbum.getAdapter();
                           if (dailyAlbumAdapter != null){
                               dailyAlbumAdapter.setAnswers(answers);
                               dailyAlbumAdapter.notifyDataSetChanged();
                           }
                       }
                    }else {
                        Log.e(TAG,e.getMessage(),e);
                    }
                }
            });
        }

    }
    private class DailyAlbumAdapter extends RecyclerView.Adapter<DailyAlbumAdapter.AnswerViewHolder>{
        Context context;
        List<Answer>answers;
        DailyAlbumAdapter(Context context,List<Answer>answerList){
            this.context = context;
            this.answers = answerList;
        }
        class AnswerViewHolder extends RecyclerView.ViewHolder{
            ImageView ivDailyPicture;
            TextView tvAlbumDate;
            AnswerViewHolder(View itemview){
                super(itemview);
                ivDailyPicture = itemview.findViewById(R.id.ivDailyPicture);
                tvAlbumDate = itemview.findViewById(R.id.tvAlbumDate);
            }
        }

        @Override
        public int getItemCount() { return answers.size(); }
        public void setAnswers(List<Answer>answers){this.answers = answers;}

        @NonNull
        @Override
        public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.daily_album_item_view,parent,false);
            return new AnswerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
            final Answer answer = answers.get(position);
            if (answer.getImagePath() == null){
                holder.ivDailyPicture.setImageResource(R.drawable.no_image);
            }else {
                showImage(holder.ivDailyPicture,answer.getImagePath());
            }
            holder.tvAlbumDate.setText(answer.getTextClock());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("answer", answer);
                    Navigation.findNavController(v)
                            .navigate(R.id.action_albumFragment_to_daily_Album_ImageFragment, bundle);
                }
            });
        }
        private void showImage(final ImageView imageView,final String path){
            final int ONE_MEGABYTE = 1024*1024;
            StorageReference imageRef = storage.getReference().child(path);
            imageRef.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if (task.isSuccessful() && task.getResult() != null){
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }else {
                        String message = task.getException() ==null?
                                getString(R.string.textImageDownloadFail)+":"+path :
                                task.getException().getMessage()+":"+path;
                        Log.e(TAG,message);
                        Toast.makeText(activity,"找不到日誌",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }


}
