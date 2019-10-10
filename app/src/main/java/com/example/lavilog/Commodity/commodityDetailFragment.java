package com.example.lavilog.Commodity;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.lavilog.R;


public class commodityDetailFragment extends Fragment {
    private Activity activity;
    private TextView tvName, tvDetail, tvPrice;
    private ImageView imageView;
    private Button btBuy;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commodity_detail, null);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageView2);
        tvName = view.findViewById(R.id.tvName);
        tvDetail = view.findViewById(R.id.tvDetal);
        tvPrice = view.findViewById(R.id.tvPrice);
        btBuy = view.findViewById(R.id.btBuy);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int imageID = bundle.getInt("ImageID");
            String name = bundle.getString("Name");
            String detail = bundle.getString("Detail");
            int price = bundle.getInt("Price");

            imageView.setImageResource(imageID);
            tvName.setText(name);
            tvDetail.setText(detail);
            tvPrice.setText(String.valueOf(price));
        }
        btBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view)
                        .navigate(R.id.action_commodityHomeFragment_to_commodityDetailFragment);
            }
        });
    }
}
