package com.example.lavilog.Landing;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lavilog.MainActivity;
import com.example.lavilog.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Timer;
import java.util.TimerTask;


public class LandingFragment extends Fragment {
    Timer timer = new Timer(true);
    View view ;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        view =inflater.inflate(R.layout.fragment_landing, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time();
                timer.cancel( );
                timer=null ;
            }
        },3000);
    }

    void time () {
        try {
            String account = auth.getCurrentUser().getEmail();//避免只是手機註冊而已，信箱沒註冊
            if(!account.equals("")){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
//            else{
//                Navigation.findNavController(view).navigate(R.id.action_landingFragment_to_langindFragment1);
//            }
        }catch (NullPointerException e){
            Navigation.findNavController(view).navigate(R.id.action_landingFragment_to_langindFragment1);
        }
    }
}
