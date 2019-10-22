package com.example.lavilog.Daily;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.R;
import com.example.lavilog.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class InsertFragment extends Fragment {

    private static final String TAG = "TAG_InsertFragment";
    private Activity activity;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private File file;
    private Answer answer;
    private ImageView ivDailyPicture;
    private Button btUpdate, btPickPicture, btTakePicture, btCancle;
    private EditText etArticle;
    private boolean pictureTaken = false;
    private TextClock textClock;
    private TextView tvQuestion;
    private static final int REQ_CROP_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_TAKE_PICTURE = 2;
    private Uri contentUri;
    private FirebaseAuth auth;
    private DailyQuestion dailyQuestion;
    private ListenerRegistration registration;
    String imagePath;
    String question;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        answer = new Answer();
        auth = FirebaseAuth.getInstance();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("新增每日Q&A日誌");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textClock = view.findViewById(R.id.textClock);
        final String time = textClock.getText().toString();
        final String year = time.substring(0, 4);
        final String month = time.substring(5, 7);
        final String day = time.substring(8, 10);

        textClock.setFormat12Hour("yyyy年MM月dd日");
        ivDailyPicture = view.findViewById(R.id.ivDailyPicture);
        etArticle = view.findViewById(R.id.etArticle);
        tvQuestion = view.findViewById(R.id.tvQuestion);


        db.collection("dailyQuestion").whereEqualTo("day", day).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    DailyQuestion dailyQuestion = documentSnapshot.toObject(DailyQuestion.class);
                    question = dailyQuestion.getQuestion();
                    tvQuestion.setText(question);

                }
            }
        });


        btTakePicture = view.findViewById(R.id.btTakePicture);
        final String account = auth.getCurrentUser().getEmail();


        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (dir != null && !dir.exists()) {
                    if (!dir.mkdir()) {
                        Log.e(TAG, "無法新增照片");
                        return;
                    }
                }
                file = new File(dir, "picture.jpg");
                contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Toast.makeText(activity, "No Camera App Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btPickPicture = view.findViewById(R.id.btPickPicture);
        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_PICK_PICTURE);
                } else {
                    Toast.makeText(activity, "開啟相簿權限", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btCancle = view.findViewById(R.id.btCancel);
        btCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        btUpdate = view.findViewById(R.id.btUpdate);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先取得插入document的ID
                final String id = db.collection("article").document().getId();
                answer.setAnswer(id);
                //測試取得時間放入firebase
                answer.setTextClock(time);
                answer.setId(id);
                answer.setYear(year);
                answer.setMonth(month);
                answer.setDay(day);
                answer.setQuestion(question);
                answer.setAccount(account);


                String article = etArticle.getText().toString();
                if (article.length() <= 0) {
                    Toast.makeText(activity, "請回答問題", Toast.LENGTH_SHORT).show();
                    return;
                }
                answer.setAnswer(article);
                //如果有拍照，上傳至Firebase storage
                if (pictureTaken) {
                    Toast.makeText(activity, "圖片上傳成功", Toast.LENGTH_SHORT).show();

                    answer.setImagePath(imagePath);
                    addOrReplace(answer);
                } else {
                    Toast.makeText(activity, "圖片上傳失敗", Toast.LENGTH_SHORT).show();
                    addOrReplace(answer);

                }


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        showAll();
        listenToDaily();
    }

    private void listenToDaily() {
        if (registration == null) {
            registration = db.collection("dailyQuestion").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                    Log.e(TAG, "article change");
                    if (e == null) {
                        if (snapshots != null && snapshots.size() > 0) {
                            List<Answer> answers = new ArrayList<>();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                answers.add(document.toObject(Answer.class));
                            }


                        }
                    }
                }
            });
        }

    }

    private void showAll() {
        db.collection("dailyQuestion").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<DailyQuestion> questions = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        questions.add(document.toObject(DailyQuestion.class));
                    }

                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    crop(contentUri);
                    break;

                case REQ_PICK_PICTURE:
                    crop(intent.getData());
                    break;

                case REQ_CROP_PICTURE:
                    pictureTaken = true;
                    Uri uri = intent.getData();//getData為得到intent所含資料的路徑
                    // 取得storage根目錄位置
                    StorageReference rootRef = storage.getReference();
                    imagePath = "/images_daily/" + System.currentTimeMillis();
                    // 建立當下目錄的子路徑
                    final StorageReference imageRef = rootRef.child(imagePath);
                    // 將儲存在uri的照片上傳
                    imageRef.putFile(uri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Query query = db.collection("article").whereEqualTo("account", auth.getCurrentUser().getEmail());
                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                    Answer userAnswer = documentSnapshot.toObject(Answer.class);
                                                    Calendar mCal = Calendar.getInstance();
                                                    String nowTime = "yyyy/MM/dd";
                                                    SimpleDateFormat df = new SimpleDateFormat(nowTime);
                                                    String today = df.format(mCal.getTime());

                                                    showImage(ivDailyPicture, imagePath);
                                                    if (textClock.equals(today)) {
                                                        userAnswer.setImagePath(imagePath);
                                                        db.collection("article").document(userAnswer.getId()).set(userAnswer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    // 下載剛上傳好的照片
                                                                    downloadImage(imagePath);
                                                                }
                                                            }
                                                        });


                                                    }


                                                }
                                            }
                                        });
                                    } else {
                                        String message = task.getException() == null ?
                                                "上傳失敗" :
                                                task.getException().getMessage();
                                        Log.e(TAG, message);
                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
            }

        }


    }

    private void downloadImage(String imagePath) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(imagePath);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            ivDailyPicture.setImageBitmap(bitmap);
                            Toast.makeText(activity, "照片修改完成", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException() == null ?
                                    "下載失敗" : task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void crop(Uri contentUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(contentUri, "image/*");
        // 設定要截圖
        intent.putExtra("crop", "true");
        // 設定截圖框大小，0代表user任意調整大小
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 700);
        intent.putExtra("outputY", 700);
        // 是否保持原圖比例
        intent.putExtra("scale", true);
        // 設定截圖後圖片位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 設定是否要回傳值
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 開啟截圖activity
            startActivityForResult(intent, REQ_CROP_PICTURE);
        } else {
            Toast.makeText(activity, "找不到截圖APP",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void showImage(final ImageView ivAccountPhoto, final String imagePath) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(imagePath);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) { // 若拿到完整的圖,回傳byte陣列
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ivAccountPhoto.setImageBitmap(bitmap);
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


    private void addOrReplace(final Answer answer) {
        // 如果Firestore沒有該ID的Document就建立新的，已經有就更新內容
        db.collection("article").document(answer.getId()).set(answer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            String message = getString(R.string.textInserted)
                                    + " with ID: " + answer.getAnswer();
                            Log.d(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            // 新增完畢回上頁
                            Navigation.findNavController(ivDailyPicture).popBackStack();
                        } else {
                            String message = task.getException() == null ?
                                    getString(R.string.textInsertFail) :
                                    task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }


}

//