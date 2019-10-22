package com.example.lavilog.BakeStage;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lavilog.R;
import com.google.firebase.auth.FirebaseAuth;

public class BackStageFragment extends Fragment {
    Button btBackStage,btUser;
    TextView tvSignOut;
    FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_back_stage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btBackStage=view.findViewById(R.id.btBackStage);
        btUser=view.findViewById(R.id.btUser);
        tvSignOut=view.findViewById(R.id.tvSignOut);

        tvSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Navigation.findNavController(tvSignOut).navigate(R.id.action_backStageFragment_to_signInFragment);
            }
        });

        btBackStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(btBackStage).navigate(R.id.action_backStageFragment_to_admListFragment);
            }
        });
        btUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(btUser).navigate(R.id.action_backStageFragment_to_userStatusListFragment);
            }
        });
    }
}
