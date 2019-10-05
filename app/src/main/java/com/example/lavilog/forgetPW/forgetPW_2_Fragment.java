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
import com.google.firebase.storage.FirebaseStorage;

public class forgetPW_2_Fragment extends Fragment {
    Activity activity;
    EditText etPassword,etPassword2;
    Button btChangePW;
    TextView tvChangePW;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    int count=0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
        Bundle bundle=getArguments();
        final String phone=(String)bundle.getSerializable("phone");
        tvChangePW=view.findViewById(R.id.tvChangePW);
        etPassword=view.findViewById(R.id.etPassword);
        etPassword2=view.findViewById(R.id.etPassword2);
        btChangePW=view.findViewById(R.id.btChangePW);

        btChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String password=etPassword.getText().toString().trim();
                String password2=etPassword2.getText().toString().trim();
                if(password.equals(password2)){
                    tvChangePW.setText("111");
                    Query query =db.collection("users");
                    tvChangePW.setText(query.toString());
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                User userChangePW = documentSnapshot.toObject(User.class);
                            String phone=userChangePW.getPhone();
//                                String id = userChangePW.getId();
//                                userChangePW.setPassword(password);
//                                db.collection("users").document(id).set(userChangePW);
//                                Toast.makeText(activity,"修改完成",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    tvChangePW.setText("兩次密碼輸入不同，請重新輸入");
                }
            }
        });
    }
}
