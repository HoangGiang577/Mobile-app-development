package com.duan.travelshare.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.duan.travelshare.R;
import com.duan.travelshare.adapter.KhachGiaoDichAdapter;
import com.duan.travelshare.adapter.TongGiaoDichdapter;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.FullUser;
import com.duan.travelshare.model.GiaoDich;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KhachGDFragment extends Fragment {
    public static ArrayList<GiaoDich> list = new ArrayList<>();
    public static ArrayList<GiaoDich> listDangGG = new ArrayList<>();
    public static ArrayList<GiaoDich> lisTongGG = new ArrayList<>();
    public static ArrayList<ChiTietPhong> listPhong = new ArrayList<>();
    private RecyclerView DangGG, TongGG;
    public static KhachGiaoDichAdapter khachGiaoDichAdapter;
    public static TongGiaoDichdapter tongGiaoDichdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferencePhong = firebaseDatabase.getReference("Phong");
    DatabaseReference databaseReferenceGD = firebaseDatabase.getReference("GiaoDich");
    ShimmerFrameLayout containerx;
    String uID;
    private FirebaseAuth mAuth;
    FullUser fullUser;
    ChiTietPhong chiTietPhong;
    View view;
    LinearLayout lnEmty;
    private ViewPager pager;
    private TabLayout tabLayout;
    public KhachGDFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_khach_g_d, container, false);
        containerx = view.findViewById(R.id.shimmer_GDKhach);
        containerx.startShimmerAnimation();
        lnEmty = view.findViewById(R.id.lnEmtyGDKhach);
        DangGG = view.findViewById(R.id.listDangGG);
        khachGiaoDichAdapter = new KhachGiaoDichAdapter(listDangGG, getActivity());
        DangGG.setAdapter(khachGiaoDichAdapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        DangGG.setLayoutManager(linearLayoutManager2);

        init();
        return view;
    }


    private void getGDKhach() {
        databaseReferenceGD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    GiaoDich gd = postSnapshot.getValue(GiaoDich.class);
                    list.add(gd);
                }

                listDangGG.clear();
                for (int i = 0; i < list.size(); i++) {
                    final int z = i;
                    if (list.get(z).getIdUser().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                        listDangGG.add(list.get(z));
                        Log.i("TAG", "email của người đặt: " + mAuth.getCurrentUser().getUid());
                        khachGiaoDichAdapter.notifyDataSetChanged();
                    }
                    if(listDangGG.isEmpty()){
                        containerx.setVisibility(View.GONE);
                        lnEmty.setVisibility(View.VISIBLE);
                    }
                    else{
                        containerx.setVisibility(View.GONE);
                        lnEmty.setVisibility(View.INVISIBLE);
                    }
                }
                containerx.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    public void init(){
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            uID = mAuth.getCurrentUser().getUid();
            containerx.startShimmerAnimation();
            databaseReferencePhong.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listPhong.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ChiTietPhong chiTietP = ds.getValue(ChiTietPhong.class);
                        listPhong.add(chiTietP);
                    }
                        getGDKhach();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            lnEmty.setVisibility(View.VISIBLE);
            containerx.setVisibility(View.INVISIBLE);
        }
    }
}