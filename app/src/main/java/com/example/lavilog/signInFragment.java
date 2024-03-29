package com.example.lavilog;


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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.lavilog.forgetPW.forgetPW_2_Fragment.isforgetPw;

public class signInFragment extends Fragment {
    Activity activity;
    EditText etAccount, etPassword;
    TextView tvStatus,tvForgetPW;
    Button btSignIn, btSignUp;
    private FirebaseAuth auth;
    private FirebaseFirestore db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        auth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.GONE);
    }
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAccount = view.findViewById(R.id.etAccount);
        etPassword = view.findViewById(R.id.etPassword);
        tvStatus = view.findViewById(R.id.tvAdmStatus);
        btSignIn = view.findViewById(R.id.btSignIn);
        btSignUp = view.findViewById(R.id.btSignUp);
        tvForgetPW=view.findViewById(R.id.tvForgetPW);


        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvStatus.setText("");
                String account = etAccount.getText().toString();
                String password = etPassword.getText().toString();
                if(!account.isEmpty()&&!password.isEmpty()){
                    signIn(account, password);
                }else{
                    tvStatus.setText("帳號與密碼不得為空");
                }
            }
        });

        tvForgetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(btSignUp).navigate(R.id.action_signInFragment_to_forgetPW_1_Fragment);
            }
        });

        // navigation沒抓到要再改
       btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(btSignUp).navigate(R.id.action_signInFragment_to_signUp_1_Fragment);
            }
        });
    }

    private void signIn(final String account, String password) {
        // 利用user輸入的email與password登入
        auth.signInWithEmailAndPassword(account, password)//官方api
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // 登入成功轉至下頁；失敗則顯示錯誤訊息
                        if (task.isSuccessful()) {
                            db.collection("users").whereEqualTo("account",account).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                        User userFB = documentSnapshot.toObject(User.class);
                                        final String status = userFB.getStatus();
                                        switch (status) {
                                            case "2":
                                                Toast.makeText(activity,"登入失敗，帳號權限封鎖中",Toast.LENGTH_SHORT).show();
                                                break;
                                            case "0":
                                                Navigation.findNavController(etPassword).navigate(R.id.action_signInFragment_to_mainFragment);
                                                Toast.makeText(activity,"登入成功",Toast.LENGTH_SHORT).show();
                                                break;
                                            case "1":
                                                Navigation.findNavController(etPassword).navigate(R.id.action_signInFragment_to_backStageFragment);
                                                Toast.makeText(activity,"管理員登入成功",Toast.LENGTH_SHORT).show();
                                                break;
                                                default:
                                                    Toast.makeText(activity,"發生故障",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                        } else {
                            Exception exception = task.getException();
                            String message = (exception == null ? "登入失敗" : exception.getMessage());
                            if(message.contains("no user")){
                                message="此帳號未註冊";
                            }
                            if(message.contains("invalid")){
                                message="登入密碼錯誤";
                            }
                            if(message.contains("again")){
                                message="錯誤登入太多次，請稍後再嘗試";
                            }
                            tvStatus.setText(message);
                        }
                    }
                });
    }
    public void onStart() {
        super.onStart();

        if (isforgetPw){
            auth.signOut();
            Toast.makeText(activity, "修改完成，請重新登入", Toast.LENGTH_SHORT).show();
            return;
        }

        // 檢查user是否已經登入，是則FirebaseUser物件不為null
       try {
           String account = auth.getCurrentUser().getEmail();//避免只是手機註冊而已，信箱沒註冊
           db.collection("users").whereEqualTo("account",account).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   QuerySnapshot querySnapshot = (task.isSuccessful())? task.getResult() : null;
                   for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                       User user = documentSnapshot.toObject(User.class);
                       String status = user.getStatus();
                       switch (status){
                           case "1":
                               Toast.makeText(activity,"管理者直接進入",Toast.LENGTH_LONG).show();
                               Navigation.findNavController(btSignIn).navigate(R.id.action_signInFragment_to_backStageFragment);
                           break;
                           case "0":
                               Navigation.findNavController(etAccount).navigate(R.id.action_signInFragment_to_mainFragment);
                               Toast.makeText(activity,"使用者直接進入",Toast.LENGTH_LONG).show();
                               break;
                           case "2":
                               Toast.makeText(activity,"您已被封鎖",Toast.LENGTH_LONG).show();
                               break;
                               default:
                                   Toast.makeText(activity,status,Toast.LENGTH_LONG).show();
                       }
                   }

               }
           });


//           Toast.makeText(activity,account+"=="+status,Toast.LENGTH_LONG).show();
//           if((!account.equals(""))&&(status.equals("1"))){
//               Toast.makeText(activity,account,Toast.LENGTH_LONG).show();
//               Navigation.findNavController(etAccount).navigate(R.id.action_signInFragment_to_backStageFragment);
//           }else{
//               Toast.makeText(activity,account,Toast.LENGTH_LONG).show();
//               Navigation.findNavController(etAccount).navigate(R.id.action_signInFragment_to_mainFragment);
//           }
//           if (status.equals("1")){
//               Navigation.findNavController(btSignIn).navigate(R.id.action_signInFragment_to_backStageFragment);
//           }
//
//           db.collection("users").whereEqualTo("account",account).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//               @Override
//               public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                   QuerySnapshot querySnapshot = (task.isSuccessful())? task.getResult() : null;
//                   for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
//                       User user = documentSnapshot.toObject(User.class);
//                       status = user.getStatus();
//                       if (status.equals("1")){
//                           Navigation.findNavController(btSignIn).navigate(R.id.action_signInFragment_to_backStageFragment);
//                       }
//                   }
//
//               }
//           });
//

//           else{
//
//               auth.signOut();
//           }
       }catch (NullPointerException e){
//          Toast.makeText(activity,e.toString(),Toast.LENGTH_LONG).show();
       }
    }
}
//