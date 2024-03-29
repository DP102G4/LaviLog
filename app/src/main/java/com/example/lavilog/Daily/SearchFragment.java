package com.example.lavilog.Daily;


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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private  static  final String TAG = "TAG_Mainfragment";
    private Activity activity;
    private RecyclerView rvAnswer;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private ListenerRegistration registration;
    private int Day,Month;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvAnswer = view.findViewById(R.id.rvAnswer);
        rvAnswer.setLayoutManager(new LinearLayoutManager(activity));
        Bundle bundle = getArguments();
        if(bundle!=null){
             Day = bundle.getInt("day");
             Month=bundle.getInt("month");

            Log.i("day!!!!!",""+Day);
            Log.i("month!!!!",""+Month);
            SearchData(Day,Month);

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SearchData(Day,Month);
        listenToDaily(Day,Month);
    }

    @Override
    public void onStop() {
        super.onStop();
        registration.remove();
        registration = null;
    }
    private void SearchData (int Day,int Month)
    {

        db.collection("article").whereEqualTo("day",""+Day).whereEqualTo("month",""+Month).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.i("Mike ans",""+queryDocumentSnapshots.size());
                List<DocumentSnapshot> buffer =queryDocumentSnapshots.getDocuments();

                List<Answer> answers = new ArrayList<>();
                for (int i =0;i<buffer.size();i++)
                {

                    String ID = buffer.get(i).get("id").toString();

                    Log.i("ahan ans",queryDocumentSnapshots.size()+""+ID);
                    answers.add(buffer.get(i).toObject(Answer.class));

                }
                rvAnswer.setAdapter(new AnswerAdapter(activity,answers));

                Log.i("ahan ans","--------------------------------------------------------------");
                // ...
            }
        });
    }
    private void showAll() {

        /*db.collection("article").orderBy("month",Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() !=null){
                    List<Answer> answers = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()){
                        answers.add(document.toObject(Answer.class));
                        //Log.i("Mike",document.get("id").toString());
                    }
                    rvAnswer.setAdapter(new AnswerAdapter(activity,answers));
                }else {
                    String message = task.getException() == null?
                    getString(R.string.textNoDailyFound):
                    task.getException().getMessage();
                    Log.e(TAG,message);
                    Toast.makeText(activity,"找不到日誌",Toast.LENGTH_SHORT).show();
                }
            }
        });
        */

    }

    private void listenToDaily(int Day , int Month) {
        if (registration == null){
            registration = db.collection("article").whereEqualTo("day",""+Day).whereEqualTo("month",""+Month).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    Log.d(TAG,"article change.");
                    if (e == null){
                        if (snapshots != null && snapshots.size() >0 ){
                            List<Answer> answers = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()){
                                answers.add(document.toObject(Answer.class));
                            }
                            AnswerAdapter answerAdapter = (AnswerAdapter)rvAnswer.getAdapter();
                            if (answerAdapter != null){
                                answerAdapter.setAnswers(answers);
                                answerAdapter.notifyDataSetChanged();
                            }
                        }
                    }else {
                        Log.e(TAG,e.getMessage(),e);
                    }
                }
            });
        }
    }
    private class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder> {
        Context context;
        List<Answer> answers;

        AnswerAdapter(Context context, List<Answer> answerList) {
            this.context = context;
            this.answers = answerList;
        }

        class AnswerViewHolder extends RecyclerView.ViewHolder {
            ImageView ivDaily;
            TextView tvDate, tvQuestion, tvAnswer;

            AnswerViewHolder(View itemview) {
                super(itemview);
                ivDaily = itemview.findViewById(R.id.ivDaily);
                tvDate = itemview.findViewById(R.id.tvDate);
                tvQuestion = itemview.findViewById(R.id.tvQuestion);
                tvAnswer = itemview.findViewById(R.id.tvAnswer);
                System.out.println("heloooooooooooooooooooooooooooooooooo");
            }
        }

        @Override
        public int getItemCount() {
            return answers.size();
        }

        public void setAnswers(List<Answer> answers) {
            this.answers = answers;
        }

        @NonNull
        @Override
        public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.daily_item_view, parent, false);
            return new AnswerViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
            final Answer answer = answers.get(position);
            System.out.println("HHHHHHHHHHHHHHHHHHHHH");
            if (answer.getImagePath() == null) {
                holder.ivDaily.setImageResource(R.drawable.no_image);
            } else {
                showImage(holder.ivDaily, answer.getImagePath());
            }
            holder.tvDate.setText(answer.getTextClock());
            holder.tvQuestion.setText(answer.getQuestion());
            holder.tvAnswer.setText(answer.getAnswer());

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    delete(answer);
                    return true;
                }
            });

        }




        private void showImage(final ImageView imageView,final String path){
            final int ONE_MEGABYTE = 1024* 1024;
            StorageReference imageRef = storage.getReference().child(path);
            imageRef.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if (task.isSuccessful() && task.getResult() != null){
                        byte[] bytes = task.getResult();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        imageView.setImageBitmap(bitmap);
                    }else {
                        String message = task.getException() == null ?
                                getString(R.string.textImageDownloadFail) + ": " + path :
                                task.getException().getMessage() +":"+path;
                        Log.e(TAG,message);
                        Toast.makeText(activity,"找不到日誌",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }


    }

    private void delete(final Answer answer) {
        // 刪除Firestore內的文章資料
        db.collection("article").document(answer.getId()).delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "已刪除文章", Toast.LENGTH_SHORT).show();
                            // 刪除該景點在Firebase storage對應的圖檔
                            if (answer.getImagePath() != null) {
                                storage.getReference().child(answer.getImagePath()).delete()
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

//