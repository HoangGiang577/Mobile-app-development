package com.duan.travelshare.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.adapter.SaveAdapter;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.Save;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SaveFragment extends Fragment {
    RecyclerView rcvPhong;
    public static SaveAdapter saveAdapter;
    List<Save> list = new ArrayList<>();
    private EditText textSearch;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceSave = firebaseDatabase.getReference("Save");
    private FirebaseAuth mAuth;
    ShimmerFrameLayout containerx;
    LinearLayout lnEmty;
    TextView textEmty;
    public SaveFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Toolbar
        lnEmty = view.findViewById(R.id.lnEmtyGDHome);
        textEmty = view.findViewById(R.id.textEmty);
        containerx = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        containerx.startShimmerAnimation();
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.tbTitle);
        ImageView back = toolbar.findViewById(R.id.tbBack);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserFragment userFragment = new UserFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, userFragment)
                        .commit();
                MainActivity.navigation.setSelectedItemId(R.id.user);
            }
        });
        MainActivity.navigation.setVisibility(View.GONE);
        LinearLayout lnMap  = view.findViewById(R.id.lnMap);
        lnMap.setVisibility(View.GONE);
        title.setText("FAVORITE ROOM");
        rcvPhong = view.findViewById(R.id.listPhong);
        saveAdapter = new SaveAdapter(list, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvPhong.setLayoutManager(linearLayoutManager);
        rcvPhong.setAdapter(saveAdapter);
        getAllSave();
        //Tìm kiếm
        textSearch = view.findViewById(R.id.edtSearch);
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int
                    count) {
                System.out.println("Text [" + s + "] - Start [" + start + "] - Before [" + before + "] - Count [" + count + "]");
                if (count < before) {
                    saveAdapter.resetData();
                }
                saveAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void getAllSave() {
        String uID= mAuth.getCurrentUser().getUid();
        Log.i("TAG", mAuth.getCurrentUser().getUid());
        databaseReferenceSave.orderByChild("uID").equalTo(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Save s = postSnapshot.getValue(Save.class);
                    list.add(s);
                }
                if(list.isEmpty()){
                    lnEmty.setVisibility(View.VISIBLE);
                }
                else {
                    lnEmty.setVisibility(View.INVISIBLE);
                }
                containerx.setVisibility(View.GONE);
                saveAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}