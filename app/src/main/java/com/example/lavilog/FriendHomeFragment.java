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
import android.widget.ImageView;
import android.widget.TextView;

public class FriendHomeFragment extends Fragment {
    private Activity activity;
    private ImageView ivUserPhoto;
    private TextView tvUserName;
    private Button btSearchFriend, btSearchID, btQRcode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivUserPhoto = view.findViewById(R.id.ivUserPhoto);
        tvUserName = view.findViewById(R.id.tvFriendName);
        btSearchFriend = view.findViewById(R.id.btSearchFriend);
        btSearchID = view.findViewById(R.id.btSearchID);
        btQRcode = view.findViewById(R.id.btQRcode);

        btSearchFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_friendHomeFragment_to_friendSearchFragment);
            }
        });

        btSearchID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_friendHomeFragment_to_searchUserIdFragment);
            }
        });

//        btQRcode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view)
//                        .navigate(R.id.action_friendHomeFragment_to_QRcodeFragment);
//            }
//        });


    }
}
