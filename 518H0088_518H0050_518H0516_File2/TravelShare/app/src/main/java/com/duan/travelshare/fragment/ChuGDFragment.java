package com.duan.travelshare.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duan.travelshare.R;
import com.duan.travelshare.adapter.TongGiaoDichdapter;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.GiaoDich;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChuGDFragment extends Fragment {
    public static ArrayList<ChiTietPhong> listPhong = new ArrayList<>();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferencePhong = firebaseDatabase.getReference("Phong");
    DatabaseReference databaseReferenceGD = firebaseDatabase.getReference("GiaoDich");
    public static ArrayList<GiaoDich> lisTongGG = new ArrayList<>();
    public static TongGiaoDichdapter tongGiaoDichdapter;
    private RecyclerView TongGG;
    LinearLayout lnEmty;
    private FirebaseAuth mAuth;
    ShimmerFrameLayout container;
    String uID;
    View view;

    public ChuGDFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chu_g_d, container, false);
        mAuth = FirebaseAuth.getInstance();
        lnEmty = view.findViewById(R.id.lnEmtyGDChu);

        TongGG = view.findViewById(R.id.listTongGG);
        tongGiaoDichdapter = new TongGiaoDichdapter(lisTongGG, getActivity());
        TongGG.setAdapter(tongGiaoDichdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        TongGG.setLayoutManager(linearLayoutManager2);
        init();
        return view;
    }


    private void getGDTong() {
        final ArrayList<GiaoDich> list2 = new ArrayList<>();
        databaseReferenceGD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list2.clear();
                lisTongGG.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    GiaoDich gd = postSnapshot.getValue(GiaoDich.class);
                    list2.add(gd);
                }

                for (int i = 0; i < list2.size(); i++) {
                    String idPhong = list2.get(i).getIdPhong();
                    final int z = i;

                    for (int j = 0; j < listPhong.size(); j++) {
                        ChiTietPhong chiTietPhong = listPhong.get(j);
                        if (chiTietPhong.getIdPhong().equalsIgnoreCase(idPhong)) {
                            if (chiTietPhong.getuID().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                                lisTongGG.add(list2.get(z));
                                tongGiaoDichdapter.notifyDataSetChanged();
                            }
                            container.setVisibility(View.GONE);
                        }
                    }
                }
                if (lisTongGG.isEmpty()) {
                    lnEmty.setVisibility(View.VISIBLE);
                } else {
                    lnEmty.setVisibility(View.INVISIBLE);
                }
                container.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void init() {
        mAuth = FirebaseAuth.getInstance();
        container = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_GDChu);
        container.setVisibility(View.GONE);
        if (mAuth.getCurrentUser() != null) {
            uID = mAuth.getCurrentUser().getUid();
            container.setVisibility(View.VISIBLE);
            container.startShimmerAnimation();
            databaseReferencePhong.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listPhong.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChiTietPhong chiTietP = ds.getValue(ChiTietPhong.class);
                        listPhong.add(chiTietP);
                    }
                    getGDTong();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            lnEmty.setVisibility(View.VISIBLE);
        }
    }
}