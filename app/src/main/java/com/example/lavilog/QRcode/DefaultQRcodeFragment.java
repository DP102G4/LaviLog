package com.example.lavilog.QRcode;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lavilog.R;
import com.google.zxing.integration.android.IntentIntegrator;

public class DefaultQRcodeFragment extends Fragment {
    private Activity activity;
    private ImageView ivQRcode;
    private TextView textView;
    private Button btScanQR;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_qrcode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivQRcode = view.findViewById(R.id.ivQRcode);
        textView = view.findViewById(R.id.textView);
        btScanQR = view.findViewById(R.id.btScanQR);

        btScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 若在Activity內需要呼叫IntentIntegrator(Activity)建構式建立IntentIntegrator物件；
                 * 而在Fragment內需要呼叫IntentIntegrator.forSupportFragment(Fragment)建立物件，
                 * 掃瞄完畢時，Fragment.onActivityResult()才會被呼叫 */
                // IntentIntegrator integrator = new IntentIntegrator(this);
                IntentIntegrator integrator = IntentIntegrator.forSupportFragment(DefaultQRcodeFragment.this);

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
    }
}
