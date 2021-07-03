package com.delightbasket.grocery.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.activities.AddDeliveryAddressActivity;
import com.delightbasket.grocery.databinding.ItemAddressBinding;
import com.delightbasket.grocery.model.Address;
import com.google.gson.Gson;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<Address.Datum> data;
    private Context context;

    OnClickDeleveryAddress onClickDeleveryAddress;

    public OnClickDeleveryAddress getOnClickDeleveryAddress() {
        return onClickDeleveryAddress;
    }

    public void setOnClickDeleveryAddress(OnClickDeleveryAddress onClickDeleveryAddress) {
        this.onClickDeleveryAddress = onClickDeleveryAddress;
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address.Datum address = data.get(position);
        Log.d("TAG", "onBindViewHolder: " + address.getLastName());
        holder.binding.imgDelete.setVisibility(View.VISIBLE);
        holder.binding.imgEdit.setVisibility(View.VISIBLE);
        holder.binding.tvName.setText(address.getFirstName().concat(" " + address.getLastName()));
        holder.binding.tvAddress.setText(address.getHomeNo().concat(" " +
                address.getSociety() + " " +
                address.getStreet() + " " +
                address.getArea() + " " +
                address.getCity() + " " +
                (!address.getPincode().equals("") ? address.getPincode() : "")));
        holder.binding.tvMobile.setText(address.getMobileNumber().concat("  " +
                (address.getAltMobileNumber() != null ? address.getAltMobileNumber() : "")));


        if(address.getIsDefault() == 1) {
            holder.binding.tvDefult.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvDefult.setVisibility(View.INVISIBLE);
        }
        String s = getAddressType(address.getAddressType());
        holder.binding.tvType.setText(s);

        holder.binding.imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddDeliveryAddressActivity.class);
            intent.putExtra("address", new Gson().toJson(address));
            context.startActivity(intent);
        });
        holder.binding.imgDelete.setOnClickListener(v -> onClickDeleveryAddress.onDeleteClick(address));
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    public interface OnClickDeleveryAddress {
        void onDeleteClick(Address.Datum datum);
    }

    private String getAddressType(Long addressType) {
        if(addressType == 1) {
            return "Home";
        } else if(addressType == 2) {
            return "Office";
        } else {
            return "Other";
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<Address.Datum> data) {
        this.data = data;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        ItemAddressBinding binding;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAddressBinding.bind(itemView);
        }
    }
}
