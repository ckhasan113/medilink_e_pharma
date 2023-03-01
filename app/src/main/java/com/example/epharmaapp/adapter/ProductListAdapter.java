package com.example.epharmaapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epharmaapp.R;
import com.example.epharmaapp.pojo.PharmacyProductDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListAdapterViewHolder> {

    private Context context;
    private List<PharmacyProductDetails> list;

    private ProductListAdapterListener listener;

    public ProductListAdapter(Context context, List<PharmacyProductDetails> list) {
        this.context = context;
        this.list = list;
        listener = (ProductListAdapterListener) context;
    }

    @NonNull
    @Override
    public ProductListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductListAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.product_list_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListAdapterViewHolder holder, int position) {
        final PharmacyProductDetails product = list.get(position);

        Picasso.get().load(Uri.parse(product.getImage())).into(holder.image);
        holder.name.setText(product.getName());
        holder.company.setText("Company: "+product.getCompany());
        holder.price.setText("Price: "+product.getPrice());
        holder.expired.setText("Expired date: "+product.getExpiredDate());

        holder.getProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getProductDetails(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ProductListAdapterViewHolder extends RecyclerView.ViewHolder{

        CardView getProductDetails;
        ImageView image;
        TextView name, company, price, expired;

        public ProductListAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            getProductDetails = itemView.findViewById(R.id.getProductDetails);
            image = itemView.findViewById(R.id.imageOFproduct);
            name = itemView.findViewById(R.id.name);
            company = itemView.findViewById(R.id.company);
            price = itemView.findViewById(R.id.price);
            expired = itemView.findViewById(R.id.expired_date);
        }
    }

    public interface ProductListAdapterListener{
        void getProductDetails(PharmacyProductDetails product);
    }
}
