package com.duan.travelshare.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.adapter.PhongManagerAdapter;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.FullUser;
import com.duan.travelshare.model.HinhPhong;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class ManegerPhongThueFragment extends Fragment {
    LinearLayout lnEmty;
    ShimmerFrameLayout containerx;
    ShowDialog showDialog;
    ImageView btnAddPhongThue;
    ArrayList<ChiTietPhong> list = new ArrayList<>();
    RecyclerView rcvphong;
    public static PhongManagerAdapter chiTietPhongAdapter;
    ArrayList<HinhPhong> listHinh = new ArrayList<>();
    private String key = "";
    private ArrayList<Uri> listHinhPhong = new ArrayList<>();
    private ArrayList<String> listImageFireBase = new ArrayList<>();


    //Xin quyền chụp ảnh, thư viện
    Uri image_uri;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private String[] cameraPermission;
    private String[] storagePermission;
    ImageView h1, h2, h3;
    private EditText textSearch;
    private int chooseImage = 0;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferencePhong = firebaseDatabase.getReference("Phong");
    private FirebaseAuth mAuth;
    String uID;
    private View view;

    public ManegerPhongThueFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_maneger_phong_thue, container, false);
        containerx = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_containerPT);
        lnEmty = view.findViewById(R.id.lnEmtyPhongThue);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            uID = mAuth.getCurrentUser().getUid();
            containerx.startShimmerAnimation();
        }
        init();

        //Tìm kiếm
        textSearch = view.findViewById(R.id.edtSearch);
        textSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int
                    count) {
                System.out.println("Text [" + s + "] - Start [" + start + "] - Before [" + before + "] - Count [" + count + "]");
                if (count < before) {
                    chiTietPhongAdapter.resetData();
                }
                chiTietPhongAdapter.getFilter().filter(s.toString());
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

    private void init() {
        MainActivity.navigation.setVisibility(View.GONE);
        rcvphong = view.findViewById(R.id.rec_MngPhongThue);
        //Khi ấn nút back
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.tbTitle);
        final ImageView back = toolbar.findViewById(R.id.tbBack);
        title.setText("ROOM MANAGER");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navigation.setVisibility(View.VISIBLE);
                UserFragment userFragment = new UserFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, userFragment)
                        .commit();
            }
        });
        showDialog = new ShowDialog(getActivity());
        btnAddPhongThue = view.findViewById(R.id.btnAddMngPhongThue);
        btnAddPhongThue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoom();

            }
        });
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://truyen-60710.appspot.com");
        progressDialog = new ProgressDialog(getActivity());
        chiTietPhongAdapter = new PhongManagerAdapter(list, getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcvphong.setLayoutManager(linearLayoutManager);
        rcvphong.setAdapter(chiTietPhongAdapter);
    }

    private void addRoom() {
        listImageFireBase.clear();
        listHinhPhong.clear();
        listHinh.clear();
        key = databaseReferencePhong.push().getKey();
        camera();
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog.setContentView(R.layout.add_room);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final EditText tenPhong, giaPhong, diaChi, moTa;
        Button btnThem, btnNhapLai;
        btnThem = dialog.findViewById(R.id.btnTPThem);
        btnNhapLai = dialog.findViewById(R.id.btnTPNhapLai);
        tenPhong = dialog.findViewById(R.id.edtTPTieuDe);
        giaPhong = dialog.findViewById(R.id.edtTPGiaPhong);
        diaChi = dialog.findViewById(R.id.edtTPDiaChi);
        moTa = dialog.findViewById(R.id.edtTPMoTaChiTietPhong);
        h1 = dialog.findViewById(R.id.ivH1);
        h2 = dialog.findViewById(R.id.ivH2);
        h3 = dialog.findViewById(R.id.ivH3);


        h1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage = 1;
                showImagePickDialog();
            }
        });

        h2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage = 2;
                showImagePickDialog();
            }
        });

        h3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage = 3;
                showImagePickDialog();
            }
        });

        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ten = tenPhong.getText().toString();
                final String gia = giaPhong.getText().toString();
                final String dc = diaChi.getText().toString();
                final String mot = moTa.getText().toString();
                progressDialog.show();
                progressDialog.setMessage(getString(R.string.updating));

                //Đổ listHinh vào
                if (!listHinh.isEmpty()) {
                    for (int i = 0; i < listHinh.size(); i++) {
                        listHinhPhong.add(listHinh.get(i).getLinkHinh());
                    }
                }

                for (int i = 0; i < listHinhPhong.size(); i++) {
                    Uri IndividualImage = listHinhPhong.get(i);
                    storageReference.child(key + "h" + (i + 1)).putFile(IndividualImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while ((!uriTask.isSuccessful())) ;

                            Uri dowloadUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                listImageFireBase.add(String.valueOf(dowloadUri));
                                if (listImageFireBase.size() == listHinhPhong.size()) {
                                    final ChiTietPhong chiTietPhong = new ChiTietPhong(key, ten, gia, dc, mot, listImageFireBase, uID);
                                    //Thêm phòng
                                    databaseReferencePhong.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            databaseReferencePhong.child(key).setValue(chiTietPhong);

                                            showDialog.show(getString(R.string.add_room_success));
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                dialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }


            }
        });

        //Button nhập lại
        btnNhapLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tenPhong.setText("");
                giaPhong.setText("");
                diaChi.setText("");
                moTa.setText("");
            }
        });

        dialog.show();
    }


    private void camera() {
        //Khai báo xin quyền
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void showImagePickDialog() {
        String option[] = {"Camera", getString(R.string.library)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.load_where));
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragetPermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragetPermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccept = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccept && storageAccept) {
                        pickFromCamera();
                    } else {
                        showDialog.show(getString(R.string.erro_camera));
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccpted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccpted) {
                        pickFromGallery();
                    } else {
                        showDialog.show(getString(R.string.erro_lib));
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                chooseImage();
//                insertImage(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                chooseImage();
//                insertImage(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Boolean checkLink = false;

    private void chooseImage() {
        switch (chooseImage) {
            case 1:
                checkLink = false;
                Picasso.with(getActivity()).load(image_uri).into(h1);
                if (!listHinh.isEmpty()) {
                    for (int i = 0; i < listHinh.size(); i++) {
                        HinhPhong hinhPhong = listHinh.get(i);
                        if (hinhPhong.getIdHinh().matches("h1")) {
                            checkLink = true;
                            listHinh.set(i, new HinhPhong("h1", image_uri));

                            break;
                        }
                    }
                }
                if (!checkLink) {
                    listHinh.add(new HinhPhong("h1", image_uri));
                }
                h2.setVisibility(View.VISIBLE);
                h3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                checkLink = false;
                Picasso.with(getActivity()).load(image_uri).into(h2);
                if (!listHinh.isEmpty()) {
                    for (int i = 0; i < listHinh.size(); i++) {
                        HinhPhong hinhPhong = listHinh.get(i);
                        if (hinhPhong.getIdHinh().matches("h2")) {
                            checkLink = true;
                            listHinh.set(i, new HinhPhong("h2", image_uri));

                            break;
                        }
                    }
                }
                if (!checkLink) {
                    listHinh.add(new HinhPhong("h2", image_uri));
                }
                h3.setVisibility(View.VISIBLE);
                break;
            case 3:
                checkLink = false;
                Picasso.with(getActivity()).load(image_uri).into(h3);
                if (!listHinh.isEmpty()) {
                    for (int i = 0; i < listHinh.size(); i++) {
                        HinhPhong hinhPhong = listHinh.get(i);
                        if (hinhPhong.getIdHinh().matches("h3")) {
                            checkLink = true;
                            listHinh.set(i, new HinhPhong("h3", image_uri));
                            break;
                        }
                    }
                }
                if (!checkLink) {
                    listHinh.add(new HinhPhong("h3", image_uri));
                }
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        databaseReferencePhong.orderByChild("uID").equalTo(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    ChiTietPhong chiTietPhong = postSnapshot.getValue(ChiTietPhong.class);
                    list.add(chiTietPhong);
                }
                if(list.isEmpty()){
                    lnEmty.setVisibility(View.VISIBLE);
                    containerx.setVisibility(View.GONE);
                }
                else {
                    containerx.setVisibility(View.GONE);
                    lnEmty.setVisibility(View.INVISIBLE);
                }
                chiTietPhongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}