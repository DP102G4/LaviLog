package com.example.lavilog.Account;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lavilog.R;
import com.example.lavilog.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class editPhotoFragment extends Fragment {
    String TAG="TAG_editPhoto";
    Activity activity;
    ImageView ivPhotoEdit;
    Button btTakePicture,btPickPicture;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    private Uri contentUri;
    private static final int REQ_TAKE_PICTURE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_CROP_PICTURE = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        auth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        storage=FirebaseStorage.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivPhotoEdit=view.findViewById(R.id.ivPhotoEdit);
        btPickPicture=view.findViewById(R.id.btPickPicture);
        btTakePicture=view.findViewById(R.id.btTakePicture);
        Bundle bundle=getArguments();
        String account = (String)bundle.getSerializable("account");
        Query query = db.collection("users").whereEqualTo("account",account);
        Toast.makeText(activity,account,Toast.LENGTH_SHORT).show();
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
           if (task.isSuccessful()){
               QuerySnapshot querySnapshot = task.getResult();
               for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                   User userFB = documentSnapshot.toObject(User.class);
                   String imagePath = userFB.getImagePath();
                   showImage(ivPhotoEdit,imagePath);
               }
            }
            }
        });
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File dir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if (dir != null && !dir.exists()) {
                    if (!dir.mkdirs()) {
                        Log.e(TAG, getString(R.string.textDirNotCreated));
                        return;
                    }
                }
                File file = new File(dir, "picture.jpg");
                contentUri = FileProvider.getUriForFile(
                        activity, activity.getPackageName() + ".provider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_TAKE_PICTURE);
                } else {
                    Toast.makeText(activity, R.string.textNoCameraAppFound,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_PICK_PICTURE);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
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
                    Uri uri = intent.getData();//getData為得到intent所含資料的路徑
                    // 取得storage根目錄位置
                    StorageReference rootRef = storage.getReference();
                    final String imagePath = "/images_users/" + auth.getCurrentUser().getEmail();
                    // 建立當下目錄的子路徑
                    final StorageReference imageRef = rootRef.child(imagePath);
                    // 將儲存在uri的照片上傳
                    imageRef.putFile(uri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Query query = db.collection("users").whereEqualTo("account",auth.getCurrentUser().getEmail());
                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                    User userFB = documentSnapshot.toObject(User.class);
                                                    userFB.setImagePath(imagePath);
                                                    db.collection("users").document(userFB.getId()).set(userFB).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                // 下載剛上傳好的照片
                                                                downloadImage(imagePath);
                                                            }
                                                        }
                                                    });
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
    private void downloadImage(String path) {
        final int ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference().child(path);
        imageRef.getBytes(ONE_MEGABYTE)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            byte[] bytes = task.getResult();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap = toRoundBitmap(bitmap);
                            ivPhotoEdit.setImageBitmap(bitmap);
                            Toast.makeText(activity,"照片修改完成", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = task.getException() == null ?
                                    "下載失敗": task.getException().getMessage();
                            Log.e(TAG, message);
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                            Bitmap roundBitmap=toRoundBitmap(bitmap);
                            ivAccountPhoto.setImageBitmap(roundBitmap);
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

    private Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        //取照片的寬跟高，並將較短的一邊當做基準邊
        if(width > height) {
            r = height;
        } else {
            r = width;
        }
        //建構一個bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，並在backgroundBmp上畫圈
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint = new Paint();
        //設置邊緣光滑，去掉鋸齒狀
        paint.setAntiAlias(true);

        //要抓寬高相等，就是取正方形，再畫成圓形
        RectF rect = new RectF(0, 0, r, r);
        //透過制定的rect畫一個圓角矩形，當圓角X軸方向的半徑等於Y軸方向的半徑，
        //且都等于r/2时，畫出來的圓角矩形就是圓形
        canvas.drawRoundRect(rect, r/2, r/2, paint);
        //設置當兩個圖形相交時的模式，SRC_IN為取SRC圖形相交的部分，多餘的將被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas將bitmap畫在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //回傳製作完成的backgroundBmp
        return backgroundBmp;
    }
    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(sourceImageUri, "image/*");
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
}
