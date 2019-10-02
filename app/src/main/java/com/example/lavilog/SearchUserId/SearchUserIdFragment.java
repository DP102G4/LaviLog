package com.example.lavilog.SearchUserId;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.R;

import java.util.ArrayList;
import java.util.List;

public class SearchUserIdFragment extends Fragment {
    private Activity activity;
    private ImageView ivUser;
    private TextView tvUserName;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<User> users;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_user_id, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerView.setAdapter(new UserAdapter(activity, users));
                SearchUserIdFragment.UserAdapter adapter = (SearchUserIdFragment.UserAdapter) recyclerView.getAdapter();
                List<User> searchUserId = new ArrayList<>();
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        if(users!=null) {
                            users.removeAll(users); // 全部刪掉
                            adapter.setUsers(users);
                        }
                    } else {
                        users=getUsers();
                        // 搜尋原始資料 equals
                        for (User user : users) {
                            if (user.getName().toUpperCase().equals(newText.toUpperCase())) {
                                searchUserId.add(user);
                            }
                        }
                        adapter.setUsers(searchUserId);
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                recyclerView.setAdapter(new UserAdapter(activity, users));
                SearchUserIdFragment.UserAdapter adapter = (SearchUserIdFragment.UserAdapter) recyclerView.getAdapter();
                List<User> searchUserId = new ArrayList<>();
                if (adapter != null) {                  // 如果適配器 不等於 空值
                    if (newText.isEmpty()) {            // 如果搜尋條件為空字串
                        if (users != null) {            //
                            users.removeAll(users);     // 清空資料
                            adapter.setUsers(users);
                        }
                    } else {
                        adapter.setUsers(searchUserId);

                    }
                }
                return false;
            }
        });
    }

    private class UserAdapter extends RecyclerView.Adapter<SearchUserIdFragment.UserAdapter.MyViewHolder> {
        Context context;
        List<User> users;
        public UserAdapter(Context context,List<User> users) {
            this.context = context;
            this.users = users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivUser;
            TextView tvUserName;
            public MyViewHolder(View itemView) {
                super(itemView);
                ivUser = itemView.findViewById(R.id.ivUser);
                tvUserName = itemView.findViewById(R.id.tvUserName);
            }
        }

        @NonNull
        @Override
        public SearchUserIdFragment.UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.user_id_item_view, viewGroup, false);
            return new SearchUserIdFragment.UserAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchUserIdFragment.UserAdapter.MyViewHolder viewholder, int index) {
            final User user = users.get(index);
            viewholder.ivUser.setImageResource(user.getImageId());
            viewholder.tvUserName.setText(user.getName());
            viewholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Navigation.findNavController(searchView)
                            .navigate(R.id.action_searchUserIdFragment_to_searchUserIdResultFragment);
                }
            });
        }
    }

    private List<User> getUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User(R.drawable.mothersoup1, "user1"));
        users.add(new User(R.drawable.mothersoup1, "user2"));
        users.add(new User(R.drawable.mothersoup1, "user3"));
        users.add(new User(R.drawable.mothersoup1, "user33"));
        return users;
    }
}
