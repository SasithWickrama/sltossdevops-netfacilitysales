package com.slt.devops.netfacilitysales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.slt.devops.netfacilitysales.R;
import com.slt.devops.netfacilitysales.models.Equipment;

import java.util.ArrayList;
import java.util.List;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentAdapter.UsersViewHolder> implements Filterable {

    private Context mCtx;
    private List<Equipment> equipmentList;
    private List<Equipment> equipmentListFull;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener =  listener;
    }

    public EquipmentAdapter(Context mCtx, List<Equipment> equipmentList) {
        this.mCtx = mCtx;
        this.equipmentList = equipmentList;
        this.equipmentListFull = new ArrayList<>(equipmentList);
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.equip_list, parent, false);
        return new UsersViewHolder(view,mListener);
    }



    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        Equipment equpment = equipmentList.get(position);

        holder.textViewRtom.setText(equpment.getRtom());
        holder.textViewLocation.setText(equpment.getLocation());
        holder.textViewType.setText(equpment.getType());
        holder.textViewLat.setText(equpment.getLatitude());
        holder.textViewLon.setText(equpment.getLontitude());
        holder.textViewDes.setText(equpment.getDISTANCE());
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    @Override
    public Filter getFilter() {
        return equipmentFilter;
    }

    private Filter equipmentFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Equipment> filteredList = new ArrayList<>();

            if(charSequence == null || charSequence.length() == 0){

                filteredList.addAll(equipmentListFull);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Equipment item : equipmentListFull) {
                    if (item.getLocation().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            equipmentList.clear();
            equipmentList.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    };

    class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView textViewRtom, textViewLocation, textViewType,textViewLat,textViewLon,textViewDes;

        public UsersViewHolder(View itemView,final OnItemClickListener listener) {
            super(itemView);

            textViewRtom = itemView.findViewById(R.id.textViewRtom);
            textViewLocation = itemView.findViewById(R.id.textViewLocation);
            textViewType = itemView.findViewById(R.id.textViewType);
            textViewLat = itemView.findViewById(R.id.textViewLat);
            textViewLon = itemView.findViewById(R.id.textViewLon);
            textViewDes= itemView.findViewById(R.id.textViewDes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position );
                        }
                    }
                }
            });
        }



    }
}

