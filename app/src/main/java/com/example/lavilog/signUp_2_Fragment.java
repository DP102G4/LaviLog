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

public class signUp_2_Fragment extends Fragment {
    Activity activity;
    EditText etPhone,etConfirmCode;
    Button btConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_2_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etPhone=view.findViewById(R.id.etPhone);
        etConfirmCode=view.findViewById(R.id.etConfirmCode);
        btConfirm=view.findViewById(R.id.btConfirm3);
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = etPhone.getText().toString().trim();
                Bundle bundle=new Bundle();
                bundle.putSerializable("phone",phone);
                Navigation.findNavController(view).navigate(R.id.action_signUp_2_Fragment_to_signUp_3_Fragment,bundle);
            }
        });
    }
}
