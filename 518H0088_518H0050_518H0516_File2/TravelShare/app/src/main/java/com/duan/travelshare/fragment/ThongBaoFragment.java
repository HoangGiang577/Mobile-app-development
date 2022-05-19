package com.duan.travelshare.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.adapter.TBAdapter;
import com.duan.travelshare.model.GiaoDich;
import com.duan.travelshare.model.ThongBao;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ThongBaoFragment extends Fragment {

    ArrayList<ThongBao> listTB = new ArrayList<>();
    ArrayList<GiaoDich> listGD = new ArrayList<>();
    RecyclerView recThongBao;
    String email = MainActivity.emailUser;
    private View view;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceTB = firebaseDatabase.getReference("ThongBao");
    DatabaseReference databaseReferenceGD = firebaseDatabase.getReference("GiaoDich");
    private FirebaseAuth mAuth;
    String uID;
    TBAdapter adapter;
    FirebaseRecyclerAdapter adapterFirebase;
    ShimmerFrameLayout containerx;
    LinearLayout lnEmty;
    public ThongBaoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_thong_bao, container, false);
        //Toolbar
        lnEmty = view.findViewById(R.id.lnEmtyTB);
        mAuth = FirebaseAuth.getInstance();
        containerx = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        init();

        try {
           if (mAuth.getCurrentUser() != null) {
               uID = mAuth.getCurrentUser().getUid();
               containerx.startShimmerAnimation();
               getAllTB();
//            setUpFireBase();
           }
           else {
               lnEmty.setVisibility(View.VISIBLE);
               containerx.setVisibility(View.GONE);
           }
       }
       catch (Exception e){
           e.printStackTrace();
       }
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.tbTitle);
        ImageView back = toolbar.findViewById(R.id.tbBack);
        title.setText(R.string.noti_title);
        back.setVisibility(View.INVISIBLE);

        recThongBao = view.findViewById(R.id.recThongBao);


        adapter = new TBAdapter(getActivity(), listTB);
        recThongBao.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        recThongBao.setLayoutManager(linearLayoutManager2);
    }

    private void getAllTB() {

        databaseReferenceTB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTB.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ThongBao thongBao = postSnapshot.getValue(ThongBao.class);
                    if (uID.equalsIgnoreCase(thongBao.getIdChu()) || uID.equalsIgnoreCase(thongBao.getIdUser())) {
                        listTB.add(thongBao);
                    }
                }
                if(listTB.isEmpty()){
                    lnEmty.setVisibility(View.VISIBLE);
                    containerx.setVisibility(View.GONE);
                }
                else {
                    lnEmty.setVisibility(View.INVISIBLE);
                    containerx.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}