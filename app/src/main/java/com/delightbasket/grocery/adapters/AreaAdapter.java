package com.delightbasket.grocery.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.databinding.ItemTextviewListweightpriceBinding;
import com.delightbasket.grocery.model.Area;

import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.AreaViewHolder> {
    private List<Area.Datum> areas;
    private OnAreaSelectListnear onAreaSelectListnear;

    public AreaAdapter(List<Area.Datum> areas, OnAreaSelectListnear onAreaSelectListnear) {

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
        holder.binding.text1.setText(areas.get(position).getAreaName());
        holder.binding.text1.setOnClickListener(v -> onAreaSelectListnear.onAreaSelect(areas.get(position)));
    }

    @Override
    public int getItemCount() {
        return areas.size();
    }

    public interface OnAreaSelectListnear {
        void onAreaSelect(Area.Datum area);
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder {
        ItemTextviewListweightpriceBinding binding;

        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTextviewListweightpriceBinding.bind(itemView);
        }
    }
}
