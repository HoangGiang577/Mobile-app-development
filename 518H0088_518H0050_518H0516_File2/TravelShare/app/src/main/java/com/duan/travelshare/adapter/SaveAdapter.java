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

import com.duan.travelshare.R;
import com.duan.travelshare.fragment.ChiTietPhongHomeFragment;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.Save;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.ViewHolder> {
    List<Save> list;
    List<Save> listSort;
    Filter filter;
    Context context;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferencePhong = firebaseDatabase.getReference("Phong");
    ChiTietPhong chiTietPhong;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat fm = NumberFormat.getCurrencyInstance(localeVN);
    public SaveAdapter(List<Save> list, Context context) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Save save = list.get(position);
        String idPhong = save.getIdPhong();

        databaseReferencePhong.child(idPhong).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chiTietPhong = snapshot.getValue(ChiTietPhong.class);

                holder.tenPhong.setText(chiTietPhong.getTenPhong());
                holder.giaPhong.setText(fm.format(Integer.parseInt(chiTietPhong.getGiaPhong()))+"/day");
                holder.diachiPhong.setText(chiTietPhong.getDiaChiPhong());
                if (chiTietPhong.getImgPhong().size() != 0) {
                    Picasso.with(context).load(chiTietPhong.getImgPhong().get(0)).into(holder.imgPhong);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


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
            Save listP = list.get(position);

            ChiTietPhongHomeFragment Save = new ChiTietPhongHomeFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("list", chiTietPhong);
            bundle.putString("from","save");
            Save.setArguments(bundle);

            FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, Save)
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
                List<Save> save = new ArrayList<Save>();
                for (Save p : list) {
                    if (chiTietPhong.getTenPhong().toUpperCase().contains(constraint.toString().toUpperCase()))
                        save.add(p);
                }
                results.values = save;
                results.count = save.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetChanged();
            else {
                list = (List<Save>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
