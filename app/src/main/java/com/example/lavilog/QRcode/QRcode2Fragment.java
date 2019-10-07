package com.example.lavilog.QRcode;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lavilog.R;
import com.google.zxing.integration.android.IntentIntegrator; // 掃描QRcode 使⽤IntentIntegrator類別功能
import com.google.zxing.integration.android.IntentResult;     // 顯示掃描的QRcode 使⽤IntentResult類別功能


public class QRcode2Fragment extends Fragment {
    private Activity activity;
    private Button btScan2;
    private TextView tvPrompt, tvResult, tvPrompt2, tvPrompt3;
    private Button btToSearchUserId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("行動條碼掃條");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrcode2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btScan2 = view.findViewById(R.id.btScanQR2);
        tvPrompt = view.findViewById(R.id.tvPrompt);
        tvResult = view.findViewById(R.id.tvResult);
        tvPrompt2 = view.findViewById(R.id.tvPrompt2);
        tvPrompt3 = view.findViewById(R.id.tvPrompt3);
        btToSearchUserId = view.findViewById(R.id.btToSearchUserId);

        btScan2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 若在Activity內需要呼叫IntentIntegrator(Activity)建構式建立IntentIntegrator物件；
                 * 而在Fragment內需要呼叫IntentIntegrator.forSupportFragment(Fragment)建立物件，
                 * 掃瞄完畢時，Fragment.onActivityResult()才會被呼叫 */
                // IntentIntegrator integrator = new IntentIntegrator(this);
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(QRcode2Fragment.this);

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

        btToSearchUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_QRcode2Fragment_to_searchUserIdFragment);
            }
        });
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {
            tvResult.setText(intentResult.getContents());
        } else {
            tvResult.setText(R.string.textResultNotFound);
        }
    }
}
