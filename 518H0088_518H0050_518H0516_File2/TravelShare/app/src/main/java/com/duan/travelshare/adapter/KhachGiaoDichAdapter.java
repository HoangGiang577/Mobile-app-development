package com.duan.travelshare.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.duan.travelshare.R;
import com.duan.travelshare.fragment.ChiTietPhongHomeFragment;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.FullUser;
import com.duan.travelshare.model.GiaoDich;
import com.facebook.appevents.codeless.ViewIndexer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KhachGiaoDichAdapter extends RecyclerView.Adapter<KhachGiaoDichAdapter.ViewHolder> {
    List<GiaoDich> list;
    Context context;
    ChiTietPhong chiTietPhong;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferencePhong = firebaseDatabase.getReference("Phong");
    private FirebaseAuth mAuth;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat fm = NumberFormat.getCurrencyInstance(localeVN);
    View view;
    ViewPager viewPager;
    LinearLayout pager_indicator;
    public KhachGiaoDichAdapter(List<GiaoDich> list, Context context) {
        this.list = list;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         view = LayoutInflater.from(context).inflate(R.layout.one_danggiaodich, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        String idPhong = list.get(position).getIdPhong();

        final GiaoDich giaoDich = list.get(position);
        databaseReferencePhong.child(idPhong).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chiTietPhong = snapshot.getValue(ChiTietPhong.class);
                holder.tenPhong.setText(chiTietPhong.getTenPhong());
                holder.giaPhong.setText(fm.format(Integer.parseInt(chiTietPhong.getGiaPhong())));
                holder.diachiPhong.setText(chiTietPhong.getDiaChiPhong());
                if (chiTietPhong.getImgPhong().size() != 0) {
                    Picasso.with(context).load(chiTietPhong.getImgPhong().get(0)).into(holder.imgPhong);
                }
                switch (giaoDich.getTrangThai()) {
                    case "0":
                        holder.trangThai.setText(context.getResources().getString(R.string.st_wait));
                        break;
                    case "1":
                        holder.trangThai.setText(context.getResources().getString(R.string.st_ok));
                        break;
                    case "2":
                        holder.trangThai.setText(context.getResources().getString(R.string.st_cancle));
                        break;
                }
//                notifyDataSetChanged();
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
        TextView tenPhong, giaPhong, diachiPhong, trangThai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tenPhong = itemView.findViewById(R.id.titleP);
            giaPhong = itemView.findViewById(R.id.priceP);
            diachiPhong = itemView.findViewById(R.id.addressP);
            imgPhong = itemView.findViewById(R.id.imgP);
            trangThai = itemView.findViewById(R.id.tvTrangThaiGG);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            final GiaoDich listP = list.get(position);
            final Dialog dialog = new Dialog(context);
            dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
            dialog.setContentView(R.layout.giaodich);
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            final ImageView phong;
            final TextView tenP, giaP, hten, cmnd, tu, den, ghichu, trangThai;
            LinearLayout erro, lnButton;
            final Button ok, huy;

            phong = dialog.findViewById(R.id.ivimgPhongGG);
            tenP = dialog.findViewById(R.id.tvTenPhongGG);
            giaP = dialog.findViewById(R.id.tvGiaphongGG);
            trangThai = dialog.findViewById(R.id.tvTrangThai);
            hten = dialog.findViewById(R.id.tvHoTenGG);
            cmnd = dialog.findViewById(R.id.tvCmndGG);
            tu = dialog.findViewById(R.id.tvTuNgayGG);
            den = dialog.findViewById(R.id.tvDenNgayGG);
            ghichu = dialog.findViewById(R.id.tvGhiChuGG);
            erro = dialog.findViewById(R.id.lnErro);
            lnButton = dialog.findViewById(R.id.lnXacNhan);
            ok = dialog.findViewById(R.id.btnOkGG);
            huy = dialog.findViewById(R.id.btnCancleGG);

            databaseReferencePhong.child(listP.getIdPhong()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chiTietPhong = snapshot.getValue(ChiTietPhong.class);
                    if (!chiTietPhong.getImgPhong().isEmpty()) {
                        Picasso.with(context).load(chiTietPhong.getImgPhong().get(0)).into(phong);
                    }
                    tenP.setText(chiTietPhong.getTenPhong());
                    giaP.setText(fm.format(Integer.parseInt(chiTietPhong.getGiaPhong()))+"/day");
                    hten.setText(listP.getHoTen());
                    cmnd.setText(listP.getCmnd());
                    tu.setText(listP.getTuTime() + " " + listP.getTuNgay());
                    den.setText(listP.getDenTime() + " " + listP.getDenNgay());
                    ghichu.setText(listP.getGhiChu());
                    switch (listP.getTrangThai()) {
                        case "0":
                            trangThai.setText("WAIT FOR CONFIRMATION");
                            break;
                        case "1":
                            trangThai.setText("CONFIRMED");
                            break;
                        case "2":
                            trangThai.setText("CANCELLED");
                            break;
                    }
//                    notifyDataSetChanged();
                    ok.setVisibility(View.GONE);
                    huy.setText(context.getResources().getString((R.string.cancle)));
                    huy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            dialog.show();
        }
    }

    private void getPhong(String keyPhong) {
        databaseReferencePhong.child(keyPhong).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chiTietPhong = snapshot.getValue(ChiTietPhong.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
