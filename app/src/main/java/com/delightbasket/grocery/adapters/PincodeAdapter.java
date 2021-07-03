package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.model.Area;
import com.delightbasket.grocery.model.Pincode;

import java.util.List;

public class PincodeAdapter extends RecyclerView.Adapter<PincodeAdapter.AreaViewHolder> {
    private List<Pincode.Data> areas;
    private OnAreaSelectListnear onAreaSelectListnear;

    public PincodeAdapter(List<Pincode.Data> areas, OnAreaSelectListnear onAreaSelectListnear) {

        this.areas = areas;
        this.onAreaSelectListnear = onAreaSelectListnear;
    }

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_textview_listweightprice, parent, false);
        return new AreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        holder.binding.text1.setText(String.valueOf(areas.get(position).getPincode()));
        holder.binding.text1.setOnClickListener(v -> onAreaSelectListnear.onAreaSelect(areas.get(position)));
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    public interface OnAreaSelectListnear {
        void onAreaSelect(Pincode.Data area);
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder {
        ItemTextviewListweightpriceBinding binding;

        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTextviewListweightpriceBinding.bind(itemView);
        }
    }
}
