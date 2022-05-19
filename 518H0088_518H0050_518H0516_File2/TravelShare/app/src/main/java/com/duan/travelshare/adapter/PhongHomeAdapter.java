package com.duan.travelshare.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.fragment.ChiTietPhongHomeFragment;
import com.duan.travelshare.model.ChiTietPhong;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PhongHomeAdapter extends RecyclerView.Adapter<PhongHomeAdapter.ViewHolder> {
    List<ChiTietPhong> list;
    List<ChiTietPhong> listSort;
    Filter filter;
    Context context;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat fm = NumberFormat.getCurrencyInstance(localeVN);
    public PhongHomeAdapter(List<ChiTietPhong> list, Context context) {
        this.list = list;
        this.context = context;
        this.listSort = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MainActivity.navigation.setVisibility(View.VISIBLE);
        holder.tenPhong.setText(list.get(position).getTenPhong());
        holder.giaPhong.setText(fm.format(Integer.parseInt(list.get(position).getGiaPhong()))+"/day");
        holder.diachiPhong.setText(list.get(position).getDiaChiPhong());
        if (list.get(position).getImgPhong().size() != 0) {
            Picasso.with(context).load(list.get(position).getImgPhong().get(0)).into(holder.imgPhong);
        }

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgPhong;
        TextView tenPhong, giaPhong, diachiPhong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenPhong = itemView.findViewById(R.id.titleP);
            giaPhong = itemView.findViewById(R.id.priceP);
            diachiPhong = itemView.findViewById(R.id.addressP);
            imgPhong = itemView.findViewById(R.id.imgP);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            ChiTietPhong listP = list.get(position);
            ChiTietPhongHomeFragment chiTietPhong = new ChiTietPhongHomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("from","home");
            bundle.putSerializable("list", listP);
            chiTietPhong.setArguments(bundle);

            FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,R.anim.from_defaut, R.anim.slide_bottom,R.anim.from_defaut)
                    .replace(R.id.frame, chiTietPhong)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void resetData() {
        list = listSort;
    }

    public Filter getFilter() {
        if (filter == null)
            filter = new CustomFilter();
        return filter;
    }

    private class CustomFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                results.values = listSort;
                results.count = listSort.size();
            } else {
                List<ChiTietPhong> lsChiTietPhong = new ArrayList<ChiTietPhong>();
                for (ChiTietPhong p : list) {
                    if
                    (p.getTenPhong().toUpperCase().contains(constraint.toString().toUpperCase()))
                        lsChiTietPhong.add(p);
                }
                results.values = lsChiTietPhong;
                results.count = lsChiTietPhong.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetChanged();
            else {
                list = (List<ChiTietPhong>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
