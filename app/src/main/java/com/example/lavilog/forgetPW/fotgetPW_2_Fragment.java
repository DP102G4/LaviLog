package com.example.lavilog.forgetPW;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lavilog.R;

public class fotgetPW_2_Fragment extends Fragment {
    Activity activity;
    EditText etVarificationCode;
    TextView tvStatus,tvPhone;
    Button btConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fotget_pw_2_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvPhone=view.findViewById(R.id.tvPhone);
        etVarificationCode=view.findViewById(R.id.etVarificationCode_forgetPW_2_);
        tvStatus=view.findViewById(R.id.tvStatus);
        btConfirm=view.findViewById(R.id.btConfirm_forgetPW_2_);
        Bundle bundle=getArguments();
        String phone=(String)bundle.getSerializable("phone");
        tvPhone.setText("請輸入寄送到"+phone+"的認證碼以確認變更");
    }
}
