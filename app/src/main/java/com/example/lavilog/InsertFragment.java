package com.example.lavilog;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class InsertFragment extends Fragment {

    private static final String TAG = "TAG_InsertFragment";
    private Activity activity;
    //private FirebaseFirestor db;
//    private FirebaseStorage storage;
    private File file;
    private Answer answer;
    private ImageView imageView;
//    private TextView tvYear, tvDate, tvQuestion;
    private Button btUpdate, btPickPicture,btTakePicture,btCancle;
    private EditText etArticle;
    private boolean pictureTaken;
    private TextClock textClock;
    private TextView textView;
    private static final int PER_EXTERNAL_STORAGE = 0;
    private static final int REQ_PICK_PICTURE = 1;
    private static final int REQ_TAKE_PICTURE = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        db = FirebaseFirstore.getInstance();
//        storage = FirebaseStorage.getInstance();
        answer = new Answer();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("新增每日Q&A");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insert, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textClock = view.findViewById(R.id.textClock);
        textClock.setFormat12Hour("yyyy年MM月dd日");
        imageView = view.findViewById(R.id.imageView);
        etArticle = view.findViewById(R.id.etArticle);
        textView = view.findViewById(R.id.textView);
        btTakePicture = view.findViewById(R.id.btTakePicture);
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
                Uri contentUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
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
                    Toast.makeText(activity, "請選取照片", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            Uri uri = intent.getData();
            switch (requestCode) {
                case REQ_TAKE_PICTURE:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                        imageView.setImageBitmap(bitmap);
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        String text = String.format(Locale.getDefault(), "%s照片尺寸 = %d x %d", width, height);
                        textView.setText(text);
                    } else {
                        ImageDecoder.OnHeaderDecodedListener listener = new ImageDecoder.OnHeaderDecodedListener() {
                            @Override
                            public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {
                                String mimeType = imageInfo.getMimeType();
                                int width = imageInfo.getSize().getWidth();
                                int height = imageInfo.getSize().getHeight();
                                String text = String.format(Locale.getDefault(), "%s照片尺寸 = %d x %d", mimeType, width, height);
                                textView.setText(text);

                            }
                        };
                        ImageDecoder.Source source = ImageDecoder.createSource(file);
                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source, listener);
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    break;


                case REQ_PICK_PICTURE:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                        try {
                            if (uri != null) {
                                Bitmap bitmap = BitmapFactory.decodeStream(
                                        activity.getContentResolver().openInputStream(uri));
                                imageView.setImageBitmap(bitmap);
                                int width = bitmap.getWidth();
                                int height = bitmap.getHeight();
                                String text = String.format(Locale.getDefault(), "%s 照片尺寸 = %d x %d", width, height);
                                textView.setText(text);
                            }
                        } catch (IOException e) {
                            Log.e(TAG, toString());
                        }
                    } else {
                        ImageDecoder.OnHeaderDecodedListener listener = new ImageDecoder.OnHeaderDecodedListener() {
                            @Override
                            public void onHeaderDecoded(ImageDecoder decoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                                String mimeType = imageInfo.getMimeType();
                                int width = imageInfo.getSize().getWidth();
                                int height = imageInfo.getSize().getHeight();
                                String text = String.format(Locale.getDefault(), "%s 照片尺寸 = %d x %d", mimeType, width, height);
                                textView.setText(text);


                            }
                        };
                        ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), uri);
                        try {
                            Bitmap bitmap = ImageDecoder.decodeBitmap(source, listener);
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }

                    }
                    break;
            }


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        askExtrnalStoragePermission();//請求外部儲存體的存取權限
    }
    private void askExtrnalStoragePermission() {
        String[] Permission = {  //要求的權限
                Manifest.permission.READ_EXTERNAL_STORAGE};  //外部儲存公開目錄

        int result = ContextCompat.checkSelfPermission(activity, Permission[0]);
        if (result == PackageManager.PERMISSION_DENIED) { //如果使用者不同意存取
            requestPermissions(Permission, PER_EXTERNAL_STORAGE); //呼叫API請求是否允許此app存取外部公開目錄才會到OPR

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PER_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(activity, "請授權權限", Toast.LENGTH_SHORT).show();
                btPickPicture.setEnabled(false);
            } else {
                btPickPicture.setEnabled(true);
            }
        }
    }

    //    btUpdate = view.findViewById(R.id.btUpdate);
//        btUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //先取得插入document的ID
//                final String id = db.collection("article").document().getID();
//                answer.setArticle(id);
//
//                String article = etArticle.getText().toString();
//                if(article.length()<=0){
//                    Toast.makeText(activity,R.string.textarticleIsInvalid,Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                answer.setArticle(article);
//                //如果有拍照，上傳至Firebase storage
//                if (pictureTaken) {
//                    // document ID成為image path一部分，避免與其他圖檔的檔名重複
//                    final String imagepath = getString(R.string.app_name) + "/images/" + answer.getArticle();
//                    storage.getReference().child(imagepath).putFile(contentUri)
//
//
//                }
//
//            }
//        });

//    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == RESULT_OK){
//            if(requestCode == REQ_TAKE_PICTURE){
//                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath()); //拿到拍的照片
//                if (bitmap != null){
//                    imageView.setImageBitmap(bitmap);
//                    pictureTaken = true; //當user點擊insert且沒拍照片，上傳的資料可以沒照片
//                    return;
//                }
//            }
//        }
//        pictureTaken = false;
//    }


    }

