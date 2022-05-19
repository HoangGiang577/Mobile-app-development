package com.duan.travelshare.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duan.travelshare.R;
import com.duan.travelshare.fragment.ChiTietPhongHomeFragment;
import com.duan.travelshare.fragment.ChiTietPhongManagerFragment;
import com.duan.travelshare.fragment.ShowDialog;
import com.duan.travelshare.model.ChiTietPhong;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
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

public class PhongManagerAdapter extends RecyclerView.Adapter<PhongManagerAdapter.ViewHolder> {
    List<ChiTietPhong> list;
    Context context;
    List<ChiTietPhong> listSort;
    Filter filter;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat fm = NumberFormat.getCurrencyInstance(localeVN);
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Phong");
    DatabaseReference databaseReferenceTB = firebaseDatabase.getReference("ThongBao");
    DatabaseReference databaseReferenceSave = firebaseDatabase.getReference("Save");
    ShowDialog showDialog;
    ChiTietPhong listP;

    public PhongManagerAdapter(List<ChiTietPhong> list, Context context) {
        this.list = list;
        this.context = context;
        this.listSort = list;
        showDialog = new ShowDialog((Activity) context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_room, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.ten.setText(list.get(position).getTenPhong());
        holder.gia.setText(fm.format(Integer.parseInt(list.get(position).getGiaPhong())) + "/day");
        holder.diachi.setText(list.get(position).getDiaChiPhong());
        if (list.get(position).getImgPhong().size() != 0) {
            Picasso.with(context).load(list.get(position).getImgPhong().get(0)).into(holder.img);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView img;
        public TextView ten, gia, diachi;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgP);
            ten = itemView.findViewById(R.id.titleP);
            gia = itemView.findViewById(R.id.priceP);
            diachi = itemView.findViewById(R.id.addressP);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            listP = list.get(position);
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    context, R.style.BottomSheetDialogTheme
            );

            View bottomSheetView = LayoutInflater.from(context).inflate(
                    R.layout.bottom_sheet_dialog,
                    (LinearLayout) bottomSheetDialog.findViewById(R.id.bottomSheetContainer)
            );
            bottomSheetView.findViewById(R.id.txt_XemChiTiet).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();

                    ChiTietPhongManagerFragment chiTietPhong = new ChiTietPhongManagerFragment();

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", listP);
                    chiTietPhong.setArguments(bundle);

                    FragmentManager fragmentManager = ((AppCompatActivity) view.getContext()).getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit, R.anim.fragment_pop_enter, R.anim.fragment_pop_exit)
                            .replace(R.id.frame, chiTietPhong)
                            .commit();
                }
            });
            bottomSheetView.findViewById(R.id.txt_Huy).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                }
            });
            bottomSheetView.findViewById(R.id.txt_XoaKhoaHoc).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.dismiss();
                                       AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setMessage(String.valueOf(R.string.del_want))
                            .setCancelable(false)
                            .setPositiveButton(R.string.del, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseReference.child(listP.getIdPhong()).removeValue();
                                    databaseReferenceSave.child(listP.getIdPhong()).removeValue();
                                    databaseReferenceTB.orderByChild("idPhong").equalTo(listP.getIdPhong()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                                String key = childSnapshot.getKey();
                                                databaseReferenceTB.child(key).removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    showDialog.show(context.getResources().getString((R.string.del_succes)));
                                }
                            })
                            .setNegativeButton(R.string.no_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
                    alertDialog.show();

                }


            });
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }
    }

    public void resetData() {
        list = listSort;
    }

    public Filter getFilter() {
        if (filter == null)
            filter = new PhongManagerAdapter.CustomFilter();
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
