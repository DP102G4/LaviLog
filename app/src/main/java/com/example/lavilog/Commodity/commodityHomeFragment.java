//package com.example.lavilog.Commodity;
//
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.navigation.Navigation;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.example.lavilog.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class commodityHomeFragment extends Fragment {
//    private RecyclerView recyclerView;
//    Activity activity;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_commodity_home, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        List<Commodity> commodities = getCommodity();
//        recyclerView=view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
//        recyclerView.setAdapter(new CommodityAdapter(getActivity(), commodities));
//    }
//
//    private class CommodityAdapter extends RecyclerView.Adapter <CommodityAdapter.MyViewHolder>{
//        Context context;
//        List<Commodity> commodities;
//        public CommodityAdapter(Context context, List<Commodity> commodities){
//            if(context == null) System.out.println("Error!!!");
//            this.commodities = commodities;
//            this.context = context;
//        }
//
//        public class MyViewHolder extends RecyclerView.ViewHolder{
//            ImageView imageView;
//            TextView tvName, tvowner, tvprice;
//            private MyViewHolder(View itemView){
//                super(itemView);
//                imageView = itemView.findViewById(R.id.imageView2);
//                tvName = itemView.findViewById(R.id.tvName);
//                tvowner = itemView.findViewById(R.id.tvＯwner);
//                tvprice = itemView.findViewById(R.id.tvPrice);
//            }
//        }
//
//        @NonNull
//        @Override
//        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
//            Context context = this.context;
//            if(context == null) System.out.println("Error!!!");
//            View itemView = LayoutInflater.from(context)
//                    .inflate(R.layout.commodity_item_view, viewGroup, false);
//            return new MyViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//            final Commodity commodity = commodities.get(position);
//            holder.imageView.setImageResource(commodity.getImageID());
//            holder.tvName.setText(commodity.getName());
//            holder.tvowner.setText(commodity.getOwner());
//            holder.tvprice.setText(String.valueOf(commodity.getPrice()));
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (commodity == null) System.out.println("Null!!!");
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Name", commodity.getName());
//                    bundle.putString("Detail", commodity.getDetail());
//                    bundle.putInt("ImageID", commodity.getImageID());
//                    bundle.putInt("Price", commodity.getPrice());
//                    Navigation.findNavController(view)
//                            .navigate(R.id.action_commodityHomeFragment_to_commodityDetailFragment, bundle);
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() { return commodities.size(); }
//
//    }
//
//    private List<Commodity> getCommodity(){
//        List<Commodity> commodities = new ArrayList<>();
//        commodities.add(new Commodity(R.drawable.notebook_red, "土豪金", "神愛87", "有錢人的快樂就是這麼樸實無華", 100));
//        commodities.add(new Commodity(R.drawable.notebook_yellow, "土豪銀", "神愛87", "有錢人的快樂就是這麼枯燥", 100));
//        commodities.add(new Commodity(R.drawable.notebook_green, "法拉利紅", "母湯哲", "一起來找紅心Ａ～", 100));
//        return commodities;
//    }
//
//}
