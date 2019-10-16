package com.example.lavilog;


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
import com.google.firebase.storage.FirebaseStorage;

import java.util.concurrent.TimeUnit;

public class changePhoneFragment extends Fragment {
    private String TAG = "TAG_changePhone";
    Activity activity;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseFirestore db;
    TextView tvPhone,tvVerificationCode,tvStatus,textView12;
    EditText etPhone,etVerificationCode;
    Button btConfirm,btResendCode,btConfirmCode;
    private String verificationId,verID;
    private String verificationCode,verCode;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    String phone,account,password,phone886;
    String phoneOld;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        db=FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth.getCurrentUser().getEmail();
        textView12=view.findViewById(R.id.textView12);
        tvPhone=view.findViewById(R.id.tvPhone);
        tvStatus=view.findViewById(R.id.tvStatus);
        tvVerificationCode=view.findViewById(R.id.tvVerificationCode);
        etPhone=view.findViewById(R.id.etPhone);
        etVerificationCode=view.findViewById(R.id.etVerificationCode);
        btConfirm=view.findViewById(R.id.btConfirm);
        btResendCode=view.findViewById(R.id.btResendCode);
        btConfirmCode=view.findViewById(R.id.btConfirmCode);
        tvPhone.setVisibility(View.GONE);
        etVerificationCode.setVisibility(View.GONE);
        tvVerificationCode.setVisibility(View.GONE);
        btResendCode.setVisibility(View.GONE);
        btConfirmCode.setVisibility(View.GONE);
        Bundle bundle=getArguments();
        phoneOld=(String) bundle.getSerializable("phone");//為了更改firebase的電話登入，須先確認原先的電話號碼為何

        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("");//每次點擊，狀態欄都先回覆空白
                phone = etPhone.getText().toString().trim();
                if(phone.isEmpty()){
                    tvStatus.setText("電話號碼不得為空白");
                    return;
                }
                if(phone.length()!=10){
                    tvStatus.setText("電話號碼長度錯誤");
                    return;
                }
                Query query = db.collection("users").whereEqualTo("phone",phone);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                User userAA = documentSnapshot.toObject(User.class);
                                String accountFB = userAA.getAccount();
                                tvStatus.setText("此電話已被註冊過"+phone+" =  "+accountFB);
                                return; }
                        }
//這邊不能設定if跟else，因為若資料庫找不到該欄位指定值，整個就是null
                            tvStatus.setText("有過");
                                phone886="+886"+phone;
                                sendVerificationCode(phone886);
                                textView12.setVisibility(View.GONE);
                                etPhone.setVisibility(View.GONE);
                                tvStatus.setVisibility(View.GONE);
                                btConfirm.setVisibility(View.GONE);
                                tvPhone.setVisibility(View.VISIBLE);
                                etVerificationCode.setVisibility(View.VISIBLE);
                                tvVerificationCode.setVisibility(View.VISIBLE);
                                btResendCode.setVisibility(View.VISIBLE);
                                btConfirmCode.setVisibility(View.VISIBLE);

//                            String account=auth.getCurrentUser().getEmail();
//                            Query query = db.collection("users").whereEqualTo("account",account);
//                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                    QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
//
//                                }
//                            });


                    }
                });
            }
        });
        btResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.isEmpty()) {
                    etPhone.setError("手機號碼不得為空白");
                    return;
                }
                resendVerificationCode(phone886, resendToken);
            }
        });
        btConfirmCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificationCode = etVerificationCode.getText().toString().trim();
                if (verificationCode.isEmpty()) {
                    etVerificationCode.setError("驗證碼為空白");
                    return;
                }
                verifyPhoneNumberWithCode(verificationId, verificationCode);
            }
        });
    }
    private void sendVerificationCode(String phone886) {
        // 設定簡訊語系為繁體中文
        auth.setLanguageCode("zh-Hant");
//        Toast.makeText(activity,phone886,Toast.LENGTH_SHORT).show();  檢查用
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone886, // 電話號碼，驗證碼寄送的電話號碼
                60, // 驗證碼失效時間，設為60秒代表多次呼叫verifyPhoneNumber()，過了60秒才會發送第2次
                TimeUnit.SECONDS, // 設定時間單位為秒
                activity,
                verifyCallbacks); // 監聽電話驗證的狀態
    }
    private void resendVerificationCode(String phone886,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone886, // 電話號碼，驗證碼寄送的電話號碼
                60, // 驗證碼失效時間，設為60秒代表多次呼叫verifyPhoneNumber()，過了60秒才會發送第2次
                TimeUnit.SECONDS, // 設定時間單位為秒
                activity,
                verifyCallbacks, // 監聽電話驗證的狀態
                token); // 驗證碼發送後，verifyCallbacks.onCodeSent()會傳來token，方便user要求重傳驗證碼
    }
    private void verifyPhoneNumberWithCode(String verificationId, String verificationCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verificationCode);
//        Toast.makeText(activity,phoneOld,Toast.LENGTH_SHORT).show();檢查用
        firebaseAuthWithPhoneNumber(credential);
    }
    private void firebaseAuthWithPhoneNumber(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
                            auth.signOut();
                            Query query=db.collection("users").whereEqualTo("phone",phoneOld);
//                            Toast.makeText(activity,phoneOld,Toast.LENGTH_SHORT).show();檢查用
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        final User userFB = documentSnapshot.toObject(User.class);
                                        verID = userFB.getVerificationId();
                                        verCode=userFB.getVerificationCode();
                                        account=userFB.getAccount();//之後登入信箱帳號要使用到帳號
                                        password=userFB.getPassword();//之後登入信箱帳號要使用到密碼
                                        PhoneAuthCredential cred = PhoneAuthProvider.getCredential(verID, verCode);
                                        auth.signInWithCredential(cred).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if(task.isSuccessful()) {
                                                    auth.getCurrentUser().delete();
                                                    auth.signInWithEmailAndPassword(account, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            userFB.setPhone(phone);
                                                            userFB.setVerificationId(verificationId);
                                                            userFB.setVerificationCode(verificationCode);
                                                            db.collection("users").document(userFB.getId()).set(userFB);
                                                            String changePhone="yes";
                                                            Bundle bundle=new Bundle();
                                                            bundle.putSerializable("changePhone",changePhone);
                                                            Navigation.findNavController(btConfirmCode).navigate(R.id.action_changePhoneFragment_to_myProfileFragment,bundle);
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(activity, "登入失敗", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
//                        } else {
//                            Exception exception = task.getException();
//                            String message = exception == null ? "驗證失敗" : exception.getMessage();
////                            Toast.makeText(activity,"登入失敗",Toast.LENGTH_SHORT).show();
//                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
//                                etVerificationCode.setError("驗證碼錯誤");
//                            }
//                        }
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
            Log.e(TAG, "認證錯誤"+phone886 + e.getMessage());
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
