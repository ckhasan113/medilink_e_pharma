package com.example.epharmaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epharmaapp.R;
import com.example.epharmaapp.pojo.OrderChart;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListAdapterViewHolder> {

    private Context context;
    private List<OrderChart> chartList;

    public OrderListAdapter(Context context, List<OrderChart> chartList) {
        this.context = context;
        this.chartList = chartList;
    }

    @NonNull
    @Override
    public OrderListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderListAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.order_list_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListAdapterViewHolder holder, int position) {

        final OrderChart oc = chartList.get(position);

        holder.productNameTV.setText("Product: "+oc.getProductDetails().getName());
        holder.productCompanyTV.setText("Company: "+oc.getProductDetails().getCompany());
        holder.productQuantityTV.setText("Quantity: "+oc.getQuentity());
        holder.productPriceTV.setText("Price: "+oc.getPrice());

    }

    @Override
    public int getItemCount() {
        return chartList.size();
    }

    class OrderListAdapterViewHolder extends RecyclerView.ViewHolder{

        TextView productNameTV, productCompanyTV, productQuantityTV, productPriceTV;
        public OrderListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTV = itemView.findViewById(R.id.product_name_tv);
            productCompanyTV = itemView.findViewById(R.id.product_company_name_tv);
            productQuantityTV = itemView.findViewById(R.id.product_quantity_tv);
            productPriceTV = itemView.findViewById(R.id.product_price_tv);
        }
    }
}
