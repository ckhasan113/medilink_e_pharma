package com.example.epharmaapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epharmaapp.R;
import com.example.epharmaapp.pojo.ConfirmOrder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ClientRequestAdapter extends RecyclerView.Adapter<ClientRequestAdapter.ClientRequestAdapterViewHolder> {

    private Context context;
    private List<ConfirmOrder> orderList;
    private ClientRequestAdapterListener listener;

    public ClientRequestAdapter(Context context, List<ConfirmOrder> orderList) {
        this.context = context;
        this.orderList = orderList;
        listener = (ClientRequestAdapterListener) context;
    }

    @NonNull
    @Override
    public ClientRequestAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClientRequestAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.client_request_row_model, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientRequestAdapterViewHolder holder, int position) {

        final ConfirmOrder cd = orderList.get(position);

        Picasso.get().load(Uri.parse(cd.getPatientDetails().getImageURI())).into(holder.image);
        holder.name.setText("Mr. "+cd.getPatientDetails().getFirstName()+" "+cd.getPatientDetails().getLastName());
        holder.address.setText(cd.getPatientDetails().getArea()+", "+cd.getPatientDetails().getCity());
        holder.price.setText("Price: "+cd.getTotalPlusVat());
        holder.getDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getDetails(cd);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ClientRequestAdapterViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name, address, price;
        LinearLayout getDetails;
        public ClientRequestAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            getDetails = itemView.findViewById(R.id.getRequestDetails);
            image = itemView.findViewById(R.id.request_user_iv);
            name = itemView.findViewById(R.id.request_user_name_TV);
            address = itemView.findViewById(R.id.request_user_address_tv);
            price = itemView.findViewById(R.id.request_price_tv);
        }
    }

    public interface ClientRequestAdapterListener{
        void getDetails(ConfirmOrder confirmOrder);
    }
}
