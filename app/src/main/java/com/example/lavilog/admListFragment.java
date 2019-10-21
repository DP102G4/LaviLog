package com.example.lavilog;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavilog.Account.Order;
import com.example.lavilog.Account.order_1_Fragment;
import com.example.lavilog.Commodity.Commodity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class admListFragment extends Fragment {
    Activity activity;
    RecyclerView rvAdmList;
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    String account;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        activity.setTitle("管理員列表");
        db=FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adm_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvAdmList = view.findViewById(R.id.rvAdmList);
        account = auth.getCurrentUser().getEmail();
        db.collection("users").whereEqualTo("status","1").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<User> adms = new ArrayList<>();
                QuerySnapshot querySnapshot = (task.isSuccessful()) ? task.getResult() : null;
                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()){
                    User adm = documentSnapshot.toObject(User.class);
                    String admAccount = adm.getAccount();
                    if (!account.equals(admAccount)) {
                        adms.add(adm);
                    }
                }
                rvAdmList.setLayoutManager(new LinearLayoutManager(activity));
                rvAdmList.setAdapter(new admAdapter(activity, adms));
            }
        });
    }
    private class admAdapter extends RecyclerView.Adapter<admAdapter.admViewHolder>{
        Context context;
        List<User> adms;

        admAdapter(Context context,List<User> adms){
            this.context=context;
            this.adms=adms;
        }
        @Override
        public int getItemCount() {
            return adms.size();
        }

        @NonNull
        @Override
        public admViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View itemView = layoutInflater.inflate(R.layout.admlist_item, parent, false);
            return new admViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final admViewHolder holder, int position) {
            final User adm = adms.get(position);
           holder.tvAdmAccount.setText(adm.getAccount());
           holder.tvAdmName.setText(adm.getName());
           if (adm.getStatus().equals("1")){
               holder.tvAdmStatus.setText("狀態 : 正常");
           }else{
               holder.tvAdmStatus.setText("狀態 : 異常");
           }
          holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
              @Override
              public boolean onLongClick(View view) {
                  Toast.makeText(activity,adm.getName(),Toast.LENGTH_SHORT).show();
                  return true;
              }
          });
        }

        class admViewHolder extends RecyclerView.ViewHolder{
            TextView tvAdmAccount,tvAdmName,tvAdmStatus;

            admViewHolder(View itemView){
                super(itemView);
                tvAdmAccount = itemView.findViewById(R.id.tvAdmAccount);
                tvAdmName = itemView.findViewById(R.id.tvAdmName);
                tvAdmStatus =itemView.findViewById(R.id.tvAdmStatus);
            }
        }
    }
}
