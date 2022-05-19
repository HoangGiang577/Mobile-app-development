package com.duan.travelshare.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.fragment.ChiTietPhongHomeFragment;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.FullUser;
import com.duan.travelshare.model.GiaoDich;
import com.duan.travelshare.model.ThongBao;
import com.duan.travelshare.model.User;
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

public class TBAdapter extends RecyclerView.Adapter<TBAdapter.ViewHolder> {
    List<ThongBao> list;
    List<ThongBao> listSort;
    GiaoDich giaoDich;
    String uID;
    FullUser fullUserChu;
    FullUser fullUserKhach;
    ChiTietPhong chiTietPhong;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceTB = firebaseDatabase.getReference("ThongBao");
    DatabaseReference databaseReferencePhong = firebaseDatabase.getReference("Phong");
    DatabaseReference databaseReferenceFullUser = firebaseDatabase.getReference("FullUser");
    DatabaseReference databaseReferenceGD = firebaseDatabase.getReference("GiaoDich");
    DatabaseReference databaseReferenceUser = firebaseDatabase.getReference("User");

    private FirebaseAuth mAuth;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat fm = NumberFormat.getCurrencyInstance(localeVN);
    ProgressDialog processDiaglog;
    View holder;
    Context context;
    TextView ten, trangThai, gio, ngay;
    CardView cv;
    ThongBao thongBao;
    String tenUser = "";
    ArrayList<FullUser> listFull = new ArrayList<>();
    User user;

    public TBAdapter() {
    }

    public TBAdapter(Context context, List<ThongBao> list) {
        this.list = list;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            uID = mAuth.getCurrentUser().getUid();
        }
        processDiaglog = new ProgressDialog(context);
        databaseReferenceFullUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FullUser full = ds.getValue(FullUser.class);
                    listFull.add(full);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.one_thongbao, parent, false);
        return new TBAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThongBao thongBao = list.get(position);
        Log.i("TAG", "Id chủ: " + uID + "\n id của list: " + thongBao.getIdUser());
        //Set dữ liệu
        final String idChu = thongBao.getIdChu();
        final String idKhach = thongBao.getIdUser();
        final String idGD = thongBao.getIdGG();
        final ThongBao z = thongBao;


        if (uID.equalsIgnoreCase(z.getIdUser())) {
            ten.setText("Successful booking");
            Log.i("TAG", "Trùng");
        } else {
            for (int i = 0; i < listFull.size(); i++) {
                if (idKhach.equalsIgnoreCase(listFull.get(i).getuID())) {
                    tenUser = listFull.get(i).getUserName();
                    break;
                }
            }
            ten.setText("You have a new booking room from " + tenUser);
        }
        switch (thongBao.getTrangThai()) {
            case "0":
                trangThai.setText(context.getResources().getString(R.string.st_wait));
                break;
            case "1":
                trangThai.setText(context.getResources().getString(R.string.st_ok));
                break;
            case "2":
                trangThai.setText(context.getResources().getString(R.string.st_cancle));
                break;
        }
        gio.setText(thongBao.getThoiGian());
        ngay.setText(thongBao.getNgay());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ten = itemView.findViewById(R.id.nameTB);
            trangThai = itemView.findViewById(R.id.chitietTB);
            gio = itemView.findViewById(R.id.timeTB);
            ngay = itemView.findViewById(R.id.dateTB);
            cv = itemView.findViewById(R.id.cvOneTB);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            String idGD = list.get(position).getIdGG();
            thongBao = list.get(position);
//        //Get toàn bộ giao dịch
            databaseReferenceGD.child(idGD).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    giaoDich = snapshot.getValue(GiaoDich.class);

                    processDiaglog.show();
                    final Dialog dialog = new Dialog(context);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
                    dialog.setContentView(R.layout.giaodich);
                    dialog.setCancelable(true);
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    final ImageView phong;
                    final TextView tenP, giaP, hten, cmnd, tu, den, ghichu, trangThai;
                    final LinearLayout erro, lnButton;
                    final Button ok, huy, dong;

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
                    dong = dialog.findViewById(R.id.btnDongGD);
                    if (uID.matches(thongBao.getIdUser())) {
                        dong.setVisibility(View.VISIBLE);
                        ok.setVisibility(View.GONE);
                        huy.setVisibility(View.GONE);
                    }

                    databaseReferencePhong.child(thongBao.getIdPhong()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            chiTietPhong = snapshot.getValue(ChiTietPhong.class);
                            if (!chiTietPhong.getImgPhong().isEmpty()) {
                                Picasso.with(context).load(chiTietPhong.getImgPhong().get(0)).into(phong);
                            }
                            tenP.setText(chiTietPhong.getTenPhong());
                            giaP.setText(fm.format(Integer.parseInt(chiTietPhong.getGiaPhong())) + "/day");
                            hten.setText(giaoDich.getHoTen());
                            cmnd.setText(giaoDich.getCmnd());
                            tu.setText(giaoDich.getTuTime() + " " + giaoDich.getTuNgay());
                            den.setText(giaoDich.getDenTime() + " " + giaoDich.getDenNgay());
                            ghichu.setText(giaoDich.getGhiChu());
                            switch (giaoDich.getTrangThai()) {
                                case "0":
                                    ok.setVisibility(View.VISIBLE);
                                    huy.setVisibility(View.VISIBLE);
                                    trangThai.setText("WAIT FOR CONFIRMATION");
                                    break;
                                case "1":
                                    dong.setVisibility(View.VISIBLE);
                                    ok.setVisibility(View.GONE);
                                    huy.setVisibility(View.GONE);
                                    trangThai.setText("CONFIRMED");
                                    break;
                                case "2":
                                    dong.setVisibility(View.VISIBLE);
                                    ok.setVisibility(View.GONE);
                                    huy.setVisibility(View.GONE);
                                    trangThai.setText("CANCELLED");
                                    break;
                            }
                            erro.setVisibility(View.GONE);
                            processDiaglog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            giaoDich.setTrangThai("1");
                            databaseReferenceTB.child(giaoDich.getIdGD()).child("trangThai").setValue("1");
                            databaseReferenceGD.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReferenceGD.child(giaoDich.getIdGD()).setValue(giaoDich);
                                    databaseReferenceUser.child(giaoDich.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            user = snapshot.getValue(User.class);
                                            String token = user.getToken();
                                            ChiTietPhongHomeFragment.sendNotifications(token,"The order has been confirmed", "Click to see more!");
                                            trangThai.setText("CONFIRMED");
                                            dong.setVisibility(View.VISIBLE);
                                            ok.setVisibility(View.GONE);
                                            huy.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });

                    huy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            processDiaglog.show();
                            processDiaglog.setMessage(String.valueOf(R.string.wait_accept));
                            giaoDich.setTrangThai("2");

                            databaseReferenceGD.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    databaseReferenceGD.child(giaoDich.getIdGD()).setValue(giaoDich);
                                    databaseReferenceTB.child(giaoDich.getIdGD()).child("trangThai").setValue("2");

                                    databaseReferenceUser.child(giaoDich.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            user = snapshot.getValue(User.class);
                                            String token = user.getToken();
                                            ChiTietPhongHomeFragment.sendNotifications(token, "The order has been canceled", "Click to see details");
                                            trangThai.setText(context.getResources().getString(R.string.st_cancle));
                                            dong.setVisibility(View.VISIBLE);
                                            ok.setVisibility(View.GONE);
                                            huy.setVisibility(View.GONE);
                                            processDiaglog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });

                    dong.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.navigation.setSelectedItemId(R.id.thongbao);
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }
}
