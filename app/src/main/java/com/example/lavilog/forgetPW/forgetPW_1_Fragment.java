package com.example.lavilog.forgetPW;


import android.app.Activity;
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

import static com.example.lavilog.Account.myProfileFragment.accountChangePassword;
import static java.lang.String.valueOf;

public class forgetPW_1_Fragment extends Fragment {
    private String TAG = "TAG_forgetPW_1_";
    Activity activity;
    Button btConfirm,btConfirmCode,btResendCode;
    EditText etPhone,etVerificationCode;
    TextView tvStatus,tvPhone,textView8,tvVerificationCode;
    private String verificationId;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("電話號碼確認");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_pw_1_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btConfirm=view.findViewById(R.id.btConfirm);
        etPhone=view.findViewById(R.id.etPhone);
        etVerificationCode=view.findViewById(R.id.etVerificationCode);
        tvStatus=view.findViewById(R.id.tvAdmStatus);
        tvPhone=view.findViewById(R.id.tvPhone);
        textView8=view.findViewById(R.id.textView8);
        tvVerificationCode=view.findViewById(R.id.tvVerificationCode);
        btConfirmCode=view.findViewById(R.id.btConfirmCode);
        btResendCode=view.findViewById(R.id.btResendCode);

        tvVerificationCode.setVisibility(View.GONE);
        btConfirmCode.setVisibility(View.GONE);
        tvPhone.setVisibility(View.GONE);
        etVerificationCode.setVisibility(View.GONE);
        btResendCode.setVisibility(View.GONE);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvStatus.setText("");
                String phone=etPhone.getText().toString().trim();
                if(phone.isEmpty()){
                    tvStatus.setText("電話號碼不得為空白");
                    return;
                }
                if(phone.length()!=10){
                    tvStatus.setText("電話號碼長度錯誤");
                    return;
                }
                Query query = db.collection("users");
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            User userFB = documentSnapshot.toObject(User.class);
                            String phone = etPhone.getText().toString().trim();
                            String phoneFB = userFB.getPhone();
                            if(accountChangePassword){
                                final String emailUser=auth.getCurrentUser().getEmail();
                                Query query=db.collection("users").whereEqualTo("account",emailUser);
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                            User userChangePW = documentSnapshot.toObject(User.class);
                                            String phoneChangePassword=userChangePW.getPhone();
                                            String phone = etPhone.getText().toString().trim();//定義final會影響下方變數變更
                                            if(!phoneChangePassword.equals(phone)) {
                                                tvStatus.setText("非本帳號註冊的手機");
                                                return;
                                            }else{   //下面重複部分，使用方法代替
                                                Exception exception = task.getException();
                                                String message = exception == null ? "" : exception.getMessage();
                                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                                goneAndVisible(phone);
                                                phone="+886"+phone;
                                                sendVerificationCode(phone);
                                            }
                                        }
                                    }
                                });
                            }else if (!phone.equals(phoneFB)) {
                                tvStatus.setText("此手機號碼未註冊");
                            } else {
                                Exception exception = task.getException();
                                String message = exception == null ? "" : exception.getMessage();
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                goneAndVisible(phone);
                                phone="+886"+phone;
                                sendVerificationCode(phone);
                            }
                        }
                    }
                });
            }
        });
        btResendCode.setOnClickListener(new View.OnClickListener() {
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
        btConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { String verificationCode = etVerificationCode.getText().toString().trim();
                if (verificationCode.isEmpty()) {
                    etVerificationCode.setError("驗證碼為空白");
                    return;
                }
                verifyPhoneNumberWithCode(verificationId, verificationCode);
            }
        });
    }
    private void sendVerificationCode(String phone) {
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
                            String phone=etPhone.getText().toString().trim();
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("phone",phone);
                            Navigation.findNavController(btConfirmCode).navigate(R.id.action_forgetPW_1_Fragment_to_forgetPW_2_Fragment,bundle);
                            Toast.makeText(activity,"修改進行",Toast.LENGTH_SHORT).show();
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
        }
    };

    public void goneAndVisible(String phone){
        tvPhone.setText(("請輸入寄送到"+phone+"的認證碼以確認變更"));
        textView8.setVisibility(View.GONE);
        etPhone.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        btConfirm.setVisibility(View.GONE);
        tvVerificationCode.setVisibility(View.VISIBLE);
        btConfirmCode.setVisibility(View.VISIBLE);
        tvPhone.setVisibility(View.VISIBLE);
        etVerificationCode.setVisibility(View.VISIBLE);
        btResendCode.setVisibility(View.VISIBLE);
    }
}