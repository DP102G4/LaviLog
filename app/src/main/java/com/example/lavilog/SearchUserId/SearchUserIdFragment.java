package com.example.lavilog.SearchUserId;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.lavilog.R;

import java.util.List;

public class SearchUserIdFragment extends Fragment {
    private Activity activity;
    private ImageView ivUser;
    private TextView tvUserName;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<User> userrs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user_id, container, false);
    }

}
