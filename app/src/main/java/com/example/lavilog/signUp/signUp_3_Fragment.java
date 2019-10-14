package com.example.lavilog.signUp;


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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.MainActivity;
import com.example.lavilog.R;
import com.example.lavilog.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class signUp_3_Fragment extends Fragment {
    Activity acitvity;
    EditText etAccount, etPassword, etRePassword, etName;
    TextView tvStatus_Account, tvStatus_Password, tvStatus_Name, tvPhone;
    Button btConfirm, btBack;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    String account;
    String password;
    String name;
    String phone;
    String verificationId,verificationCode;
    String status="0";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acitvity = getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        acitvity.setTitle("會員註冊");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_3_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAccount = view.findViewById(R.id.etAccount);
        etPassword = view.findViewById(R.id.etPassword);
        etRePassword = view.findViewById(R.id.etRePassword);
        etName = view.findViewById(R.id.etName);
        tvStatus_Account = view.findViewById(R.id.tvStatus_Account);
        tvStatus_Password = view.findViewById(R.id.tvStatus_Password);
        tvStatus_Name = view.findViewById(R.id.tvStatus_Name);
        tvPhone = view.findViewById(R.id.tvPhone);
        btConfirm = view.findViewById(R.id.btConfirm3);
        btBack = view.findViewById(R.id.btBack);
        Bundle bundle = getArguments();
        phone = (String) bundle.getSerializable("phone");
        verificationId=(String) bundle.getSerializable("verificationId");
        verificationCode=(String) bundle.getSerializable("verificationCode");
        tvPhone.setText(phone);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_signUp_3_Fragment_to_signInFragment);
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                account = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                String rePassword = etRePassword.getText().toString().trim();
                name = etName.getText().toString().trim();
                if(!isValidEmail(account)){
                    tvStatus_Account.setText("帳號應為信箱格式");
                    return;
                }else {
                    tvStatus_Account.setText("");
                }
                if(password.isEmpty()){
                    tvStatus_Password.setText("密碼不得為空");
                    return;
                }
                if (!password.equals(rePassword)) {
                    tvStatus_Password.setText("兩次密碼輸入不同，請重新輸入");
                    return;
                } else {
                    tvStatus_Password.setText("");
                }
                if (name.length() > 5) {
                    tvStatus_Name.setText("姓名長度不得大於5個字");
                    return;
                } else {
                    tvStatus_Name.setText("");
                }
                signUp(account,password);
            }
        });
    }
    private void signUp(final String account, final String password) {
        auth.createUserWithEmailAndPassword(account,password)
                .addOnCompleteListener(acitvity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User();
                            String id = db.collection("users").document().getId();
                            user.setId(id);
                            user.setAccount(account);
                            user.setPassword(password);
                            user.setName(name);
                            user.setPhone(phone);
                            user.setVerificationId(verificationId);
                            user.setVerificationCode(verificationCode);
                            user.setStatus(status);
                            registered(user);
                        }else{
                            Query query = db.collection("users");
                            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        User userFB = documentSnapshot.toObject(User.class);
                                        String accountFB = userFB.getAccount();
                                        if (account.equals(accountFB)) {
                                            tvStatus_Account.setText("帳號已重複註冊");
                                        }else{Exception exception = task.getException();
                                            String message = exception == null ? "" : exception.getMessage();
                                            Toast.makeText(acitvity, message, Toast.LENGTH_SHORT).show();}
                                    }
                                }
                            });
                            }
                    }
                });
    }
    private void registered(final User user) {
//        db.collection("users").add(user);
        String accountLower=user.getAccount().toLowerCase();
        //google信箱帳號不區分大小寫，一律轉成小寫，所以資料庫存的帳號也必須是小寫英文
        user.setAccount(accountLower);
        db.collection("users").document(user.getId()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(acitvity, "會員註冊完成", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(btConfirm).navigate(R.id.action_signUp_3_Fragment_to_mainFragment);
                        } else {
                            Toast.makeText(acitvity, "會員註冊失敗，個人資料請再次填寫", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    private boolean isValidEmail(String account){
        String emailRegex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(account.matches(emailRegex))
        {
            return true;
        }
        return false;
    }
}
