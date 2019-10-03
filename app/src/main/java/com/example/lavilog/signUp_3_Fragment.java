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
    Boolean isRegister = false;//註冊帳號
    Boolean isRegisterImformation;//填入註冊資料

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acitvity = getActivity();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
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
        String phone = (String) bundle.getSerializable("phone");
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
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String rePassword = etRePassword.getText().toString().trim();
                String name = etName.getText().toString().trim();
                if (!account.contains("@")) {
                    tvStatus_Account.setText("帳號應為信箱格式");
                    return;
                } else {
                    tvStatus_Account.setText("");
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
                isRegisterImformation=signUp(account,password);
                tvStatus_Account.setText("是否註冊資料: "+isRegisterImformation.toString());
                if(isRegisterImformation){
                    User user = new User();
                    final String id = db.collection("users").document().getId();
                    user.setId(id);
                    user.setAccount(account);
                    user.setPassword(password);
                    user.setName(name);
                    registered(user);
                }
//                User user = new User();
//                final String id = db.collection("users").document().getId();
//                user.setId(id);
//                user.setAccount(account);
//                user.setPassword(password);
//                user.setName(name);
//                registered(user);
//                Log.e("TAG_", user.toString());
            }
        });
    }
    private boolean signUp(String account, String password) {
        auth.createUserWithEmailAndPassword(account,password)
                .addOnCompleteListener(acitvity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            isRegister = true;
                            Toast.makeText(acitvity,"註冊成功",Toast.LENGTH_SHORT).show();
                            tvStatus_Name.setText("firebase新增成功");
                            tvStatus_Password.setText(isRegister.toString());
                        }else{
                            isRegister = false;
                            Exception exception = task.getException();
                            String message = exception == null ? "註冊失敗" : exception.getMessage();
                            Toast.makeText(acitvity,"註冊失敗",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        return isRegister;
    }
    private void registered(User user) {
        db.collection("users").add(user);
//        final String account = user.getAccount();
//        final String id = user.getId();
//        Query query = db.collection("users");
////                .whereEqualTo("account", account);
//        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
//                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                    User userFB = documentSnapshot.toObject(User.class);
//                    String accountFB = userFB.getAccount();
//                    if (account.equals(accountFB)) {
//                        tvStatus_Account.setText("帳號已重複註冊");
//                    }
//                    }
//                }
//        });
//            db.collection("users").document(user.getId()).set(user)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(acitvity, "註冊完成", Toast.LENGTH_SHORT).show();
//                                tvStatus_Password.setText(tvStatus_Account.getText());
//                            } else {
//                                Toast.makeText(acitvity, "註冊失敗", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

        }

}
