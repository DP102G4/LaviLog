package com.example.lavilog;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FriendSearchFragment extends Fragment {
    private Activity activity;
    private ImageView ivFriend;
    private TextView tvName;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private List<Friend> friends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_search, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        friends = getFriends();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(new FriendAdapter(activity, friends));

        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                FriendSearchFragment.FriendAdapter adapter = (FriendSearchFragment.FriendAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    // 如果搜尋條件為空字串，就顯示原始資料；否則就顯示搜尋後結果
                    if (newText.isEmpty()) {
                        adapter.setFriends(friends);
                    } else {
                        List<Friend> searchFriends = new ArrayList<>();
                        // 搜尋原始資料內有無包含關鍵字(不區別大小寫)
                        for (Friend friend : friends) {
                            if (friend.getName().toUpperCase().contains(newText.toUpperCase())) {
                                searchFriends.add(friend);
                            }
                        }
                        adapter.setFriends(searchFriends);
                    }
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    private class FriendAdapter extends RecyclerView.Adapter<FriendSearchFragment.FriendAdapter.MyViewHolder> {
        Context context;
        List<Friend> friends;
        public FriendAdapter(Context context, List<Friend> friends) {
            this.context = context;
            this.friends = friends;
        }

        public void setFriends(List<Friend> friends) {
            this.friends = friends;
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        private class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFriend;
            TextView tvName;
            public MyViewHolder(View itemView) {
                super(itemView);
                ivFriend = itemView.findViewById(R.id.ivFriend);
                tvName = itemView.findViewById(R.id.tvName);
            }
        }

        @NonNull
        @Override
        public FriendSearchFragment.FriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.friend_item_view, viewGroup, false);
            return new FriendSearchFragment.FriendAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendSearchFragment.FriendAdapter.MyViewHolder viewHolder, int index) {
            final Friend friend = friends.get(index);
            viewHolder.ivFriend.setImageResource(friend.getImageId());
            viewHolder.tvName.setText(friend.getName());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, friend.getName(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private List<Friend> getFriends() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend(R.drawable.mothersoup, "MontherSoupZhe1"));
        friends.add(new Friend(R.drawable.mothersoup, "MontherSoupZhe2"));
        friends.add(new Friend(R.drawable.mothersoup, "MontherSoupZhe3"));
        return friends;
    }
}
