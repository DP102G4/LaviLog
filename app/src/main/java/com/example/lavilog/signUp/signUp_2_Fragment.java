package com.example.lavilog.signUp;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.MainActivity;
import com.example.lavilog.R;
import com.example.lavilog.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;

public class signUp_2_Fragment extends Fragment {
    private String TAG = "TAG_signUp_2_";
    Activity activity;
    EditText etPhone,etVerificationCode;
    Button btConfirm,btSend,btResend;
    TextView tvPhone,tvPhoneCheck,textView3,textView4,textView5;
    private String verificationId;
    private String verificationCode;
    private ConstraintLayout layoutVerify;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        activity=getActivity();
        db = FirebaseFirestore.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("手機驗證");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_2_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPhone=view.findViewById(R.id.etPhone);
        layoutVerify=view.findViewById(R.id.layoutVerify);
        layoutVerify.setVisibility(View.GONE);
        btSend=view.findViewById(R.id.btSend);
        btResend=view.findViewById(R.id.btResend);
        etVerificationCode=view.findViewById(R.id.etVerificationCode);
        btConfirm=view.findViewById(R.id.btConfirm2);
        textView3=view.findViewById(R.id.textView3);
        textView4=view.findViewById(R.id.textView4);
        textView5=view.findViewById(R.id.textView5);
        tvPhone=view.findViewById(R.id.tvPhone);
        tvPhoneCheck=view.findViewById(R.id.tvPhoneCheck);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvPhone.setText("");
                String phone =etPhone.getText().toString().trim();
                if (phone.isEmpty()||phone.length()!=10) {
                    tvPhone.setText("電話格式錯誤");
                    return;
                }else {
                    Query query = db.collection("users").whereEqualTo("phone",phone);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                User userFB = documentSnapshot.toObject(User.class);
                                String phone = etPhone.getText().toString().trim();
                                String phoneFB = userFB.getPhone();
                                if (phone.equals(phoneFB)) {
                                    tvPhone.setText("手機號碼已重複註冊");
                                    return;
                                }
                            }
                                    String phone =etPhone.getText().toString().trim();
                                    Exception exception = task.getException();
                                    String message = exception == null ? "" : exception.getMessage();
                                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                    phone="+886"+phone;
                                    sendVerificationCode(phone);
                                    textView3.setVisibility(View.GONE);
                                    textView4.setVisibility(View.GONE);
                                    textView5.setVisibility(View.GONE);
                                    etPhone.setVisibility(View.GONE);
                                    btSend.setVisibility(View.GONE);
                                    tvPhone.setVisibility(View.GONE);
                                    tvPhoneCheck.setText("請輸入傳至"+etPhone.getText().toString().trim()+"號碼的驗證碼");

                        }
                    });
                }
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificationCode = etVerificationCode.getText().toString().trim();
                if (verificationCode.isEmpty()) {
                    etVerificationCode.setError("驗證碼為空白");
                    return;
                }
                verifyPhoneNumberWithCode(verificationId, verificationCode);
            }
        });

        btResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "+886" + etPhone.getText().toString().trim();
                if (phone.isEmpty()) {
                    etPhone.setError("手機號碼不得為空白");
                    return;
                }
                resendVerificationCode(phone, resendToken);
            }
        });
    }
    private void sendVerificationCode(String phone) {
        layoutVerify.setVisibility(View.VISIBLE);
        // 設定簡訊語系為繁體中文
        auth.setLanguageCode("zh-Hant");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, // 電話號碼，驗證碼寄送的電話號碼
                60, // 驗證碼失效時間，設為60秒代表多次呼叫verifyPhoneNumber()，過了60秒才會發送第2次
                TimeUnit.SECONDS, // 設定時間單位為秒
                activity,
                verifyCallbacks); // 監聽電話驗證的狀態
    }
    private void resendVerificationCode(String phone,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, // 電話號碼，驗證碼寄送的電話號碼
                60, // 驗證碼失效時間，設為60秒代表多次呼叫verifyPhoneNumber()，過了60秒才會發送第2次
                TimeUnit.SECONDS, // 設定時間單位為秒
                activity,
                verifyCallbacks, // 監聽電話驗證的狀態
                token); // 驗證碼發送後，verifyCallbacks.onCodeSent()會傳來token，方便user要求重傳驗證碼
    }
    private void verifyPhoneNumberWithCode(String verificationId, String verificationCode) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
            firebaseAuthWithPhoneNumber(credential);
    }
    private void firebaseAuthWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String phone = etPhone.getText().toString().trim();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("phone", phone);
                            bundle.putSerializable("verificationId",verificationId);
                            bundle.putSerializable("verificationCode",verificationCode);
                            Navigation.findNavController(etPhone).navigate(R.id.action_signUp_2_Fragment_to_signUp_3_Fragment, bundle);
                        } else {
                            Exception exception = task.getException();
                            String message = exception == null ? "驗證失敗" : exception.getMessage();
//                            Toast.makeText(activity,"登入失敗",Toast.LENGTH_SHORT).show();
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                etVerificationCode.setError("驗證碼錯誤");
                            }
                        }
                    }
                });
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verifyCallbacks
            = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        /** This callback will be invoked in two situations:
         1 - Instant verification. In some cases the phone number can be instantly
         verified without needing to send or enter a verification code.
         2 - Auto-retrieval. On some devices Google Play services can automatically
         detect the incoming verification SMS and perform verification without
         user action. */
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted: " + credential);
        }

        /**
         * 發送驗證碼填入的電話號碼格式錯誤，或是使用模擬器發送都會產生發送錯誤，
         * 使用模擬器發送會產生下列執行錯誤訊息：
         * App validation failed. Is app running on a physical device?
         */
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.e(TAG, "onVerificationFailed: " + e.getMessage());
        }
        /**
         * The SMS verification code has been sent to the provided phone number,
         * we now need to ask the user to enter the code and then construct a credential
         * by combining the code with a verification ID.
         */
        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent: " + id);
            verificationId = id;
            resendToken = token;
            // 顯示填寫驗證碼版面
//            layoutVerify.setVisibility(View.VISIBLE);
        }
    };
}
