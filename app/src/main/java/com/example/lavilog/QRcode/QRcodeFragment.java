package com.example.lavilog.QRcode;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lavilog.R;

import com.google.zxing.integration.android.IntentIntegrator; // 掃描QRcode 使⽤IntentIntegrator類別功能

import java.security.Permission;


public class QRcodeFragment extends Fragment {
    private Activity activity;
    private Button btScanQR, btPickPicture, btShowQR;
    private static final int PER_EXTERNAL_STORAGE = 0; // 同意存儲外部照片
    private static final int REQ_PICK_PICTUEE = 1; // 同意選擇相簿照片

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btScanQR = view.findViewById(R.id.btScanQR);
        btPickPicture = view.findViewById(R.id.btPickPicture);
        btShowQR = view.findViewById(R.id.btShowQR);

        btScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 若在Activity內需要呼叫IntentIntegrator(Activity)建構式建立IntentIntegrator物件；
                 * 而在Fragment內需要呼叫IntentIntegrator.forSupportFragment(Fragment)建立物件，
                 * 掃瞄完畢時，Fragment.onActivityResult()才會被呼叫 */
                // IntentIntegrator integrator = new IntentIntegrator(this);
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(QRcodeFragment.this);

                // Set to true to enable saving the barcode image and sending its path in the result Intent.
                integrator.setBarcodeImageEnabled(true);
                // Set to false to disable beep on scan.
                integrator.setBeepEnabled(false); // 掃描到就發出聲音
                // Use the specified camera ID.
                integrator.setCameraId(0); // 後鏡頭為0，前鏡頭為1
                // By default, the orientation is locked. Set to false to not lock.
                integrator.setOrientationLocked(false); // 現況無效，此意義為拍照框是否可調整成直立或橫向拍照
                // Set a prompt to display on the capture screen.
                integrator.setPrompt("對準行動條碼後，即能自動讀取。"); // 顯示文字在掃描視窗上方
                // Initiates a scan
                integrator.initiateScan(); // 開始掃描

            }
        });

        btPickPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    startActivityForResult(intent, REQ_PICK_PICTUEE);
                } else {
                    Toast.makeText(activity, "請選擇照片", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btShowQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_QRcodeFragment_to_defaultQRcodeFragment);

            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        askExtrnalStoragePermission();
//    }
//
//    private void askExtrnalStoragePermission() {
//        String[] Permission = {
//                Manifest.permission.READ_EXTERNAL_STORAGE};
//
//        int result = ContextCompat.checkSelfPermission(activity, Permission[0]);
//        if (result == PackageManager.PERMISSION_DENIED) {
//            requestPermissions(Permission, PER_EXTERNAL_STORAGE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == PER_EXTERNAL_STORAGE) {
//            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                Toast.makeText(activity, "請授權權限", Toast.LENGTH_SHORT).show();
//                btPickPicture.setEnabled(false);
//            } else {
//                btPickPicture.setEnabled(true);
//            }
//        }
//    }
}
