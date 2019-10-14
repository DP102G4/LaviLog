package com.example.lavilog.QRcode;


import android.app.Activity;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.TextView;

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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;

import com.example.lavilog.QRcode.Contents;
import com.example.lavilog.QRcode.QRCodeEncoder;

import static android.content.ContentValues.TAG;

public class DefaultQRcodeFragment extends Fragment {
    private static final String TAG = "DefaultQRcodeFragment";
    private Activity activity;
    private ImageView ivQRcode;
    private TextView tvQRtext;
    private Button btScanQR, btGenerateQrCode;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String id,account;
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("顯示行動條碼");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivQRcode = view.findViewById(R.id.ivQRcode);
        tvQRtext = view.findViewById(R.id.tvQRtext);
        btGenerateQrCode = view.findViewById(R.id.btGenerateQrCode);
        btScanQR = view.findViewById(R.id.btScanQR);

        account=auth.getCurrentUser().getEmail();
        Query query=db.collection("users");
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                    final com.example.lavilog.User FBUser = documentSnapshot.toObject(com.example.lavilog.User.class);
                    String accountFB = FBUser.getAccount();
                    if (account.equals(accountFB)) {
                        user = FBUser;
                        id = user.getId();
                    }
                }
            }
        });

        btGenerateQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qrCodeText = account;
                Log.d(TAG, qrCodeText);

                // QR code image's length is the same as the width of the window,
                int dimension = getResources().getDisplayMetrics().widthPixels;
                //取得螢幕寬度，當作ＱＲＣＯＤＥ寬度

                // Encode with a QR Code image//用到qrcode的class功能
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrCodeText, null,
                        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                        dimension);
                try {
//                    將qrCodeEncoder轉成bitmap物件
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ivQRcode.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    Log.e(TAG, e.toString());
                }

                tvQRtext.setText("對方僅需使用LaviLog的行動條碼掃瞄器，並對準本行動條碼，即可將您加入好友名單內。");
                btGenerateQrCode.setEnabled(false); // 更改按鈕為不能按的狀態
                btGenerateQrCode.setText("請小心保管您的行動條碼"); // 更改按鈕文字
                btGenerateQrCode.setTextColor(0xFFFF0000); // 更改按鈕顏色
                btGenerateQrCode.setBackgroundColor(0xFFFFFF); // 更改按鈕背景顏色
            }
        });

//        btGenerateQrCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

//        btScanQR.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Navigation.findNavController(view)
//                        .navigate(R.id.action_defaultQRcodeFragment_to_QRcode2Fragment2);

//                /* 若在Activity內需要呼叫IntentIntegrator(Activity)建構式建立IntentIntegrator物件；
//                 * 而在Fragment內需要呼叫IntentIntegrator.forSupportFragment(Fragment)建立物件，
//                 * 掃瞄完畢時，Fragment.onActivityResult()才會被呼叫 */
//                // IntentIntegrator integrator = new IntentIntegrator(this);
//                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(DefaultQRcodeFragment.this);
//
//                // Set to true to enable saving the barcode image and sending its path in the result Intent.
//                integrator.setBarcodeImageEnabled(true);
//                // Set to false to disable beep on scan.
//                integrator.setBeepEnabled(false); // 掃描到就發出聲音
//                // Use the specified camera ID.
//                integrator.setCameraId(0); // 後鏡頭為0，前鏡頭為1
//                // By default, the orientation is locked. Set to false to not lock.
//                integrator.setOrientationLocked(false); // 現況無效，此意義為拍照框是否可調整成直立或橫向拍照
//                // Set a prompt to display on the capture screen.
//                integrator.setPrompt("對準行動條碼後，即能自動讀取。"); // 顯示文字在掃描視窗上方
//                // Initiates a scan
//                integrator.initiateScan(); // 開始掃描
//
//            }
//        });
    }
}
