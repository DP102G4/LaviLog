package com.example.lavilog.SearchFriend;


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

public class FriendSearchResultFragment extends Fragment {
    private Activity activity;
    private ImageView ivFriend;
    private TextView tvFriendName;
    private Button btFriendDiary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("LaviLog");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_search_result, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivFriend = view.findViewById(R.id.ivFriend);
        tvFriendName = view.findViewById(R.id.tvFriendName);
        btFriendDiary = view.findViewById(R.id.btAddFriend);

        // 改成目的地頁面 進入好友日誌主題內

//        btFriendDiary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Navigation.findNavController(view)
//                        .navigate(R.id.改成目的地頁面 進入好友日誌主題內);
//            }
//        });
    }
}
