package com.example.lavilog;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class editPhotoFragment extends Fragment {
    String TAG="TAG_editPhoto";
    Activity activity;
    ImageView ivPhotoEdit;
    Button btTakePicture,btPickPicture;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;

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
}
