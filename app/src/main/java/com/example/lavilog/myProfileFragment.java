package com.example.lavilog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.SearchFriend.Friend;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.okhttp.internal.DiskLruCache;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Calendar;


public class myProfileFragment extends Fragment {
    private static final String TAG = "TAG_myProfileFragment";
    Activity activity;
    ImageView ivAccountPhoto;
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;
    TextView tvEditPhoto,tvAccount;
    Button btChangeName,btChangeGender,btChangeBirthDay,btChangePhone,btChangePassword;
    final String arror="     >";//點選欄位內的右邊箭頭
    String id,account,days;
    User user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        activity.setTitle("我的檔案");
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivAccountPhoto=view.findViewById(R.id.ivAccountPhoto);
        tvEditPhoto=view.findViewById(R.id.tvEditPhoto);
        tvAccount=view.findViewById(R.id.tvAccount);
        btChangeName=view.findViewById(R.id.btChangeName);
        btChangeBirthDay=view.findViewById(R.id.btChangeBirthDay);
        btChangePassword=view.findViewById(R.id.btChangePassword);
        btChangePhone=view.findViewById(R.id.btChangePhone);
        btChangeGender=view.findViewById(R.id.btChangeGender);
        account=auth.getCurrentUser().getEmail();
        Query query=db.collection("users");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    final User FBUser = documentSnapshot.toObject(User.class);
                    String accountFB = FBUser.getAccount();
                    if (account.equals(accountFB)) {
                        user=FBUser;//將for-each內符合條件的帳號抓下來，就是該使用者的user物件
                        id=user.getId();//取得該會員存於資料庫內的id,方便後續更換資料使用
                        tvAccount.setText(account);
                        final String name=user.getName();
                        String path=user.getImagePath();
                        if(path!=null){
                            showImage(ivAccountPhoto, user.getImagePath());
                        }else{
                            ivAccountPhoto.setImageResource(R.drawable.no_image);
                        }
                        btChangeName.setText(name+arror);
                        btChangePhone.setText(user.getPhone()+arror);
                        String gender=user.getGender();
                        if(gender==null){
                            btChangeGender.setText("尚未設定"+arror);
                        }else{
                            btChangeGender.setText(gender+arror);
                        }
                        String birthDay=user.getBirthDay();
                        if(birthDay==null){
                            btChangeBirthDay.setText("尚未設定"+arror);
                        }else{
                            btChangeBirthDay.setText(birthDay+arror);
                        }
                        String password=user.getPassword();
                        int passwordLength=password.length();
                        String text="";
                        for(int i=0;i<passwordLength;i++){
                            text+="*";
                        }
                        btChangePassword.setText(text+arror);
                        Query query1=db.collection("users");
//                        query1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                QuerySnapshot querySnapshot1 = (task.isSuccessful()) ? task.getResult() : null;
//                                for (DocumentSnapshot documentSnapshot1 : querySnapshot1.getDocuments()) {
//                                    User User =documentSnapshot1.toObject(User.class);
//                                    String nameFB=user.getName();
//                                    if(name.equals(nameFB)){
//                                        String path=user.getImagePath();
//                                        if(path!=null){
//                                        showImage(ivAccountPhoto, user.getImagePath());
//                                        }else{
//                                            ivAccountPhoto.setImageResource(R.drawable.no_image);
//                                        }
//                                    }
//                                }
//                            }
//                        });
                    }else{Exception exception = task.getException();
                        String message = exception == null ? "" : exception.getMessage();
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();}
                }
            }
        });
        btChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// 使用LayoutInflater來載入dialog_setname.xml佈局
                LayoutInflater layoutInflater = LayoutInflater.from(activity);
                View nameView = layoutInflater.inflate(R.layout.changename_edit, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
//// 使用setView()方法將佈局顯示到dialog
                alertDialogBuilder.setView(nameView);
                final EditText userInput = (EditText) nameView.findViewById(R.id.changename_edit);
//                final TextView name = (TextView) findViewById(R.id.changename_textview);
//// 設定Dialog按鈕
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("確認修改",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int selectId) {
//// 獲取edittext的內容,顯示到textview
                                        String newName=userInput.getText().toString();
                                        btChangeName.setText(newName+arror);
                                        user.setName(newName);
                                        db.collection("users").document(id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(activity,"姓名變更完成",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int selectId) {
                                        dialog.cancel();
                                    }
                                });
//// create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
//// show it
                alertDialog.show();
            }
        });
//
        tvEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(tvEditPhoto).navigate(R.id.action_myProfileFragment_to_editPhotoFragment);
            }
        });

        btChangeGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] genderArry = new String[]{"男","女","其他"};// 性別選擇
                AlertDialog.Builder builder3=new AlertDialog.Builder(activity);
                builder3.setSingleChoiceItems(genderArry, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        final String selectGender = genderArry[which];
                        btChangeGender.setText(selectGender+arror);
                        dialogInterface.dismiss();
                        Toast.makeText(activity,"性別變更完成",Toast.LENGTH_SHORT).show();
//                        String mail=auth.getCurrentUser().getEmail();
//                        Query query=db.collection("users").whereEqualTo("account",mail);
//                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
//                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                                    User userChangeGender = documentSnapshot.toObject(User.class);
//                                    userChangeGender.setGender(selectGender);
//                                    db.collection("users").document(id).set(userChangeGender);
//                                }
//                            }
//                        });
                        user.setGender(selectGender);
                        db.collection("users").document(id).set(user);
                    }
                });
                builder3.show();
            }
        });
        btChangeBirthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Calendar nowdate = Calendar.getInstance();
//                int mYear = nowdate.get(Calendar.YEAR);
//                int mMonth = nowdate.get(Calendar.MONTH);
//                int mDay = nowdate.get(Calendar.DAY_OF_MONTH);
                int mYear=1980;
                int mMonth=0;// 1月是從0開始..
                int mDay=1;
                DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        int mYear = year;
                        int mMonth = monthOfYear+1;//月份從0開始
                        int mDay = dayOfMonth;
                        days = new StringBuffer().append(mYear).append("年").append(mMonth).append("月").append(mDay).append("日").toString();
                        btChangeBirthDay.setText(days+arror);
                        user.setBirthDay(days);
                        db.collection("users").document(id).set(user);
                        Toast.makeText(activity,"出生日期更新成功"+days,Toast.LENGTH_SHORT).show();

//                        db.collection("users").whereEqualTo("id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful() && task.getResult() != null){
//                                    QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
//                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                                        User userChangeBirthDay = documentSnapshot.toObject(User.class);
//                                        userChangeBirthDay.setBirthDay(days);
//                                        db.collection("users").document(id).set(userChangeBirthDay);
//                                        Toast.makeText(activity,"出生日期更新成功"+days,Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            }
//                        });
                    }
                };
                new DatePickerDialog(activity, onDateSetListener, mYear, mMonth, mDay).show();
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
