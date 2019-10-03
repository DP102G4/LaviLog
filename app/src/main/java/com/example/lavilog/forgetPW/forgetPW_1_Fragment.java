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

import com.example.lavilog.R;

public class forgetPW_1_Fragment extends Fragment {
    Activity activity;
    Button btConfirm;
    EditText etPhone;
    TextView tvStatus;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
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
        tvStatus=view.findViewById(R.id.tvStatus);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=etPhone.getText().toString().trim();
                if(phone.isEmpty()){
                    tvStatus.setText("電話號碼不得為空白");
                    return;
                }else{
                    tvStatus.setText("");
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("phone",phone);
                    Navigation.findNavController(view).navigate(R.id.action_forgetPW_1_Fragment_to_fotgetPW_2_Fragment,bundle);
                }
            }
        });
    }
}