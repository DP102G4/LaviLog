package com.example.lavilog.Account;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lavilog.MainActivity;
import com.example.lavilog.R;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.lavilog.MainActivity.bottomNavigationView;

public class AccountFragment extends Fragment {
    Activity activity;
    Button btMyprofile,btPurchaseDetail,btHelpQA,btSignOut;
    FirebaseAuth auth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        activity.setTitle("帳號管理");
        auth=FirebaseAuth.getInstance();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btMyprofile=view.findViewById(R.id.btMyprofile);
        btPurchaseDetail=view.findViewById(R.id.btPurchaseDetail);
        btHelpQA=view.findViewById(R.id.btHelpQA);
        btSignOut=view.findViewById(R.id.btSignOut);
        btMyprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(btMyprofile).navigate(R.id.action_accountFragment_to_myProfileFragment);
            }
        });
        btSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
                builder1.setMessage("確定帳號登出?")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                auth.signOut();
                                Navigation.findNavController(btSignOut).navigate(R.id.action_accountFragment_to_signInFragment);
                                Toast.makeText(activity,"登出成功，請重新登入",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
// User cancelled the dialog
                            }
                        });
// Create the AlertDialog object and return it
                builder1.show();
            }
        });

        btPurchaseDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String account = auth.getCurrentUser().getEmail();
                Bundle bundle = new Bundle();
                bundle.putSerializable("account",account);
                Navigation.findNavController(btPurchaseDetail).navigate(R.id.action_accountFragment_to_order_1_Fragment,bundle);
            }
        });
    }
}
