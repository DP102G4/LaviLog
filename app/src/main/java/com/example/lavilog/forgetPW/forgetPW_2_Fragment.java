package com.example.lavilog.forgetPW;


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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.lavilog.Account.myProfileFragment.accountChangePassword;

public class forgetPW_2_Fragment extends Fragment {
    Activity activity;
    EditText etPassword,etPassword2;
    Button btChangePW;
    TextView tvChangePW;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity.setTitle("修改密碼");
        return inflater.inflate(R.layout.fragment_forget_pw_2_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvChangePW=view.findViewById(R.id.tvChangePW);
        etPassword=view.findViewById(R.id.etPassword);
        etPassword2=view.findViewById(R.id.etPassword2);
        btChangePW=view.findViewById(R.id.btChangePW);
        Bundle bundle=getArguments();
        final String phone=(String)bundle.getSerializable("phone");
        auth.signOut();

        btChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String password=etPassword.getText().toString().trim();
                String password2=etPassword2.getText().toString().trim();
                if(password.equals(password2)){
                    Query query =db.collection("users");
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                User userChangePW = documentSnapshot.toObject(User.class);
                                String phonePW=userChangePW.getPhone();
                                if(phone.equals(phonePW)){
                                    String accountChangePW=userChangePW.getAccount();
                                    String passwordChangePW=userChangePW.getPassword();
                                    String id = userChangePW.getId();
                                    changePW(accountChangePW,passwordChangePW,password,id);
                                }
                            }
                        }
                    });
                }else{
                    tvChangePW.setText("兩次密碼輸入不同，請重新輸入");
                }
            }
        });
    }

    private void changePW(final String accountChangePW, final String passwordChangePW,final String password,final String id) {
        auth.signInWithEmailAndPassword(accountChangePW, passwordChangePW).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user1 = auth.getCurrentUser();
                    user1.delete();
                    auth.createUserWithEmailAndPassword(accountChangePW, password)
                            .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Query query = db.collection("users");
                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                    User userChangePW = documentSnapshot.toObject(User.class);
                                                    String account = userChangePW.getAccount();
                                                    if (account.equals(accountChangePW)) {
                                                        userChangePW.setPassword(password);
                                                        db.collection("users").document(id).set(userChangePW);

                                                        if(accountChangePassword){
                                                            Toast.makeText(activity, "修改完成", Toast.LENGTH_SHORT).show();
                                                            Navigation.findNavController(btChangePW).navigate(R.id.action_forgetPW_2_Fragment_to_myProfileFragment);
                                                        }else{
                                                            Toast.makeText(activity, "修改完成，請重新登入", Toast.LENGTH_SHORT).show();
                                                            Navigation.findNavController(btChangePW).navigate(R.id.action_forgetPW_2_Fragment_to_signInFragment);
                                                        }
                                                    } else {
                                                        Exception exception = task.getException();
                                                        String message = exception == null ? "" : exception.getMessage();
                                                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(activity, "建立異常，密碼修改失敗", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }else{
                    Toast.makeText(activity, "登入失敗，密碼修改失敗", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
