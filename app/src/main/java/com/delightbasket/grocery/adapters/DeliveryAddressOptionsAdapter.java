package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemDeliveryaddressOptionsBinding;
import com.delightbasket.grocery.model.Address;

import java.util.List;

public class DeliveryAddressOptionsAdapter extends RecyclerView.Adapter<DeliveryAddressOptionsAdapter.OptionsViewHolder> {
    public OnAddressSelectListnear getOnAddressSelectListnear() {
        return onAddressSelectListnear;
    }

    public void setOnAddressSelectListnear(OnAddressSelectListnear onAddressSelectListnear) {
        this.onAddressSelectListnear = onAddressSelectListnear;
    }

    OnAddressSelectListnear onAddressSelectListnear;
    private List<Address.Datum> data;
    private int checkPos = 0;


    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deliveryaddress_options, parent, false);
        return new OptionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, int position) {
        Address.Datum address = data.get(position);

        holder.binding.radioBtn.setChecked(checkPos == position);
        holder.binding.radioBtn.setOnClickListener(v -> {

            checkPos = position;
            onAddressSelectListnear.onAddressSelected(address);
            notifyDataSetChanged();

        });
        holder.setModel(address);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addData(List<Address.Datum> data) {
        this.data = data;
    }

    public interface OnAddressSelectListnear {
        void onAddressSelected(Address.Datum address);
    }

    public class OptionsViewHolder extends RecyclerView.ViewHolder {
        ItemDeliveryaddressOptionsBinding binding;

        public OptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDeliveryaddressOptionsBinding.bind(itemView);
        }

        public void setModel(Address.Datum address) {
            binding.tvUserName.setText(address.getFirstName().concat(" " + address.getLastName()));
            binding.tvAddress.setText(address.getHomeNo().concat(" " +
                    address.getSociety() + " " +
                    address.getStreet() + " " +
                    address.getArea() + " " +
                    address.getCity() + " " +
                    (!address.getPincode().equals("") ? address.getPincode() : "")));

            binding.tvMobile.setText(address.getMobileNumber().concat("  " +
                    (address.getAltMobileNumber() != null ? address.getAltMobileNumber() : "")));


            if(address.getIsDefault() == 1) {
                binding.tvDefult.setVisibility(View.VISIBLE);
            } else {
                binding.tvDefult.setVisibility(View.GONE);
            }
            String s = getAddressType(address.getAddressType());
            binding.tvType.setText(s);
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
    }
}
