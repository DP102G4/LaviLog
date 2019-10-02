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

public class signUp_3_Fragment extends Fragment {
    Activity acitvity;
    EditText etAccount,etPassword,etRePassword,etName;
    TextView tvStatus_Account,tvStatus_Password,tvStatus_Name,tvPhone;
    Button btConfirm,btBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acitvity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_3_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAccount=view.findViewById(R.id.etAccount);
        etPassword=view.findViewById(R.id.etPassword);
        etRePassword=view.findViewById(R.id.etRePassword);
        etName=view.findViewById(R.id.etName);
        tvStatus_Account=view.findViewById(R.id.tvStatus_Account);
        tvStatus_Password=view.findViewById(R.id.tvStatus_Password);
        tvStatus_Name=view.findViewById(R.id.tvStatus_Name);
        tvPhone=view.findViewById(R.id.tvPhone);
        btConfirm=view.findViewById(R.id.btConfirm3);
        btBack=view.findViewById(R.id.btBack);
        Bundle bundle =getArguments();
        String phone=(String)bundle.getSerializable("phone");
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
                String account=etAccount.getText().toString().trim();
                String password=etPassword.getText().toString().trim();
                String rePassword=etRePassword.getText().toString().trim();
                String name=etName.getText().toString().trim();
                if(!account.contains("@")){
                    tvStatus_Account.setText("帳號應為信箱格式");
                    return;
                }else{
                    tvStatus_Account.setText("");
                }
                if(!password.equals(rePassword)){
                    tvStatus_Password.setText("兩次密碼輸入不同，請重新輸入");
                    return;
                }else{
                    tvStatus_Password.setText("");
                }
                if(name.length()>5){
                    tvStatus_Name.setText("姓名長度不得大於5個字");
                }
            }
        });
    }
}
