package com.example.lavilog.SearchUserId;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lavilog.R;

public class SearchUserIdResultFragment extends Fragment {
    private Activity activity;
    private ImageView ivUser;
    private TextView tvName;
    private Button btAddFriend;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user_id_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivUser = view.findViewById(R.id.ivUser);
        tvName = view.findViewById(R.id.tvName);
        btAddFriend = view.findViewById(R.id.btAddFriend);

        btAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btAddFriend.setText("已發送邀請");
                btAddFriend.setEnabled(false);
            }
        });
    }
}
