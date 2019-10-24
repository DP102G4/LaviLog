package com.example.lavilog;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;

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
import com.google.firebase.firestore.QuerySnapshot;

public class admAddFragment extends Fragment {
    Activity activity;
    FirebaseFirestore db;
    FirebaseAuth auth;
    EditText etAdmAccount,etAdmPassword,etAdmName,etAdmRePassword;
    TextView tvAdmAccount,tvAdmPassword,tvAdmName;
    String admAccount,admPassword,currentAdmAccount,currentAdmPassword;
    Button btAdmAdd,btAdmBack;
    Boolean isRegisted = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        activity.setTitle("新增管理員");
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        currentAdmAccount = auth.getCurrentUser().getEmail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adm_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAdmAccount = view.findViewById(R.id.etAdmAccount);
        etAdmPassword= view.findViewById(R.id.etAdmPassword);
        etAdmName = view.findViewById(R.id.etAdmName);
        etAdmRePassword = view.findViewById(R.id.etAdmRePassword);
        tvAdmAccount = view.findViewById(R.id.tvAdmAccount);
        tvAdmPassword = view.findViewById(R.id.tvAdmPassword);
        tvAdmName = view.findViewById(R.id.tvAdmName);
        btAdmAdd = view.findViewById(R.id.btAdmAdd);
        btAdmBack = view.findViewById(R.id.btAdmBack);

        btAdmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvAdmAccount.setText("");
                tvAdmPassword.setText("");
                tvAdmPassword.setText("");
                final String account = etAdmAccount.getText().toString().trim().toLowerCase();
                final String password = etAdmPassword.getText().toString().trim();
                final String rePassword = etAdmRePassword.getText().toString().trim();
                final String name = etAdmName.getText().toString().trim();
                if(!isValidEmail(account)){
                    tvAdmAccount.setText("帳號應為信箱格式");
                    return;
                }else {
                    tvAdmAccount.setText("");
                }
                if(password.isEmpty()){
                    tvAdmPassword.setText("密碼不得為空");
                    return;
                }
                if (!password.equals(rePassword)) {
                    tvAdmPassword.setText("兩次密碼輸入不同，請重新輸入");
                    return;
                } else {
                    tvAdmPassword.setText("");
                }
                if (name.length() > 5) {
                    tvAdmName.setText("姓名長度不得大於5個字");
                    return;
                } else {
                    tvAdmName.setText("");
                }
                db.collection("users").whereEqualTo("account",account).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                            User adm = documentSnapshot.toObject(User.class);
                            if (account.equals(adm.getAccount())){
                                isRegisted = true;
                                tvAdmAccount.setText("此帳號已重複註冊");
                                return;
                            }
                        }
                        if(!isRegisted){
//                            tvAdmAccount.setText("此帳號未註冊");
                            auth.createUserWithEmailAndPassword(account,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    User adm = new User();
                                    adm.setAccount(account);
                                    adm.setPassword(password);
                                    adm.setName(name);
                                    adm.setStatus("1");
                                    adm.setImagePath("/images_users/ no_image.jpg");
                                    registered(adm);
//                                    Navigation.findNavController(etAdmPassword).popBackStack();
                                }
                            });
                        }
                    }
                });
            }
        });

        btAdmBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(etAdmPassword).popBackStack();
            }
        });
    }
    private void registered(final User adm) {
//        db.collection("users").add(user);
        String accountLower = adm.getAccount().toLowerCase();
        //google信箱帳號不區分大小寫，一律轉成小寫，所以資料庫存的帳號也必須是小寫英文
        adm.setAccount(accountLower);
        String id = db.collection("users").document().getId();
        adm.setId(id);
        db.collection("users").document(id).set(adm)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "管理者新增完成", Toast.LENGTH_SHORT).show();
//                            Navigation.findNavController(etAdmRePassword).navigate(R.id.action_admAddFragment_to_admListFragment);
                        } else {
                            Toast.makeText(activity, "管理者新增失敗，請確認資料庫狀況", Toast.LENGTH_SHORT).show();
                        }

//                        帳號註冊完成後,系統會自己判定登入新的管理者,要去取得原本的管理者帳號密碼,並重新登入
                        db.collection("users").whereEqualTo("account",currentAdmAccount).
                                get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                                    User currentAdm = documentSnapshot.toObject(User.class);
                                    currentAdmPassword = currentAdm.getPassword();
                                    auth.signInWithEmailAndPassword(currentAdmAccount,currentAdmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            Navigation.findNavController(etAdmPassword).popBackStack();
                                        }
                                    });
                                }
                            }
                        });
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