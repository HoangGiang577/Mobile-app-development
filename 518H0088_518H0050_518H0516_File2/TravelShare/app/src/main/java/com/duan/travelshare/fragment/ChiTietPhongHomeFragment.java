package com.duan.travelshare.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.Service.APIService;
import com.duan.travelshare.Service.Client;
import com.duan.travelshare.Service.Data;
import com.duan.travelshare.Service.MyResponse;
import com.duan.travelshare.Service.Sender;
import com.duan.travelshare.adapter.ImageSlide;
import com.duan.travelshare.model.ChiTietPhong;
import com.duan.travelshare.model.FullUser;
import com.duan.travelshare.model.Save;
import com.duan.travelshare.model.ThongBao;
import com.duan.travelshare.model.GiaoDich;
import com.duan.travelshare.model.User;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChiTietPhongHomeFragment extends Fragment {
    Boolean check;
    final int SEND_SMS_PERMISSION_REQUEST_CODE = 111;
    private static final int REQUEST_CALL = 1;
    private ImageView user, call, messenger;
    static ToggleButton save;
    private LinearLayout star;
    private TextView tenPhong, giaPhong, tenUser, emailUser, moTa;
    private Button xem, datPhong;
    ChiTietPhong chiTietPhong;
    private DatePickerDialog datePickerDialog;
    ImageView img;
    TextView tenPhongDat, gia;
    EditText hoten, cmnd, tungay, denngay, ghichu, tutime, dentime;
    private ArrayList<Save> listSave = new ArrayList<>();
    Button datPhongDat, huyDat;
    ShowDialog showDialog;
    Locale localeVN = new Locale("vi", "VN");
    NumberFormat fm = NumberFormat.getCurrencyInstance(localeVN);
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReferenceFull = firebaseDatabase.getReference("FullUser");
    DatabaseReference databaseReferenceGD = firebaseDatabase.getReference("GiaoDich");
    DatabaseReference databaseReferenceTB = firebaseDatabase.getReference("ThongBao");
    DatabaseReference databaseReferenceSave = firebaseDatabase.getReference("Save");
    DatabaseReference databaseReferenceUser = firebaseDatabase.getReference("User");
    String uID;
    private FirebaseAuth mAuth;
    private FullUser fullUser;
    private int mHour, mMinute;
    static String idPhong;
    private View view;
    FullUser fullUserKhach;
    ShimmerFrameLayout container;
    RelativeLayout chitiet;
    ViewPager viewPager;
    LinearLayout pager_indicator;
    static APIService apiService;

    String pattern = "dd/MM/yyyy";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    public ChiTietPhongHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chi_tiet_phong, container, false);

        mAuth = FirebaseAuth.getInstance();
        final Bundle bundle = getArguments();
        chiTietPhong = (ChiTietPhong) bundle.getSerializable("list");
        uID = chiTietPhong.getuID();
        idPhong = chiTietPhong.getIdPhong();
        init();

        //Kiểm tra save chưa
        getSave();

        save.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (mAuth.getCurrentUser() != null) {

                    if (b) {
                        Toast.makeText(getActivity(), R.string.save_room_succes, Toast.LENGTH_SHORT).show();
                        databaseReferenceSave.child(idPhong).setValue(new Save(mAuth.getCurrentUser().getUid(), idPhong));
                    } else {
                        Toast.makeText(getActivity(), R.string.unsave__room, Toast.LENGTH_SHORT).show();
                        databaseReferenceSave.child(idPhong).removeValue();
                    }
                } else {
                    showDialog.show(getString(R.string.login_first));
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });
        messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        xem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog1 = new Dialog(getActivity());
                dialog1.setContentView(R.layout.fragment_show_user);
//                dialog1.setCancelable(true);
                Window window = dialog1.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (dialog1 != null && dialog1.getWindow() != null) {
                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                }
//                FullUser userChuThue = chiTietPhong.getFullUser();
                ImageView ivQR, ivAvt;
                EditText name, email, birthday, phone, address, cmndUser;
                Button btnDong;

                ivQR = dialog1.findViewById(R.id.ivQRUser);
                ivAvt = dialog1.findViewById(R.id.ivAvatar);
                btnDong = dialog1.findViewById(R.id.btnUpdateUser);
                name = dialog1.findViewById(R.id.tvFullName);
                cmndUser = dialog1.findViewById(R.id.tvCmnd);
                email = dialog1.findViewById(R.id.tvEmail);
                birthday = dialog1.findViewById(R.id.tvBirthday);
                phone = dialog1.findViewById(R.id.tvPhone);
                address = dialog1.findViewById(R.id.tvAddress);
                if (!fullUser.getLinkImage().isEmpty()) {
                    Picasso.with(getContext()).load(fullUser.getLinkImage()).into(ivAvt);
                }
                name.setText(fullUser.getUserName());
                cmndUser.setText(fullUser.getCmndUser());
                email.setText(fullUser.getEmailUser());
                birthday.setText(fullUser.getBirtdayUser());
                phone.setText(fullUser.getPhoneUser());
                address.setText(fullUser.getAddressUser());
                btnDong.setText(R.string.Close);
                btnDong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });

                name.setFocusable(false);
                cmndUser.setFocusable(false);
                email.setFocusable(false);
                birthday.setFocusable(false);
                phone.setFocusable(false);
                address.setFocusable(false);

                dialog1.show();
            }
        });

        datPhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (mAuth.getCurrentUser() != null) {
                        if (fullUser.getuID().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                            showDialog.show(getString(R.string.erro_add_room));
                        } else {
                            datPhong();
                        }
                    } else {
                        showDialog.show(getString(R.string.login_first));
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frame, new LoginFragment()).commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //Khi ấn nút back
        //Toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.tbTitle);
        ImageView back = toolbar.findViewById(R.id.tbBack);
        title.setText("ROOM DETAIL");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bundle.getString("from").equalsIgnoreCase("save")) {
                    MainActivity.navigation.setVisibility(View.VISIBLE);
                    SaveFragment userFragment = new SaveFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, userFragment)
                            .commit();
                } else {
                    MainActivity.navigation.setVisibility(View.VISIBLE);
                    HomeFragment userFragment = new HomeFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame, userFragment)
                            .commit();
                }

            }
        });
        return view;
    }

    private void init() {
        chitiet = view.findViewById(R.id.layouChiTiet);
        container = (ShimmerFrameLayout) view.findViewById(R.id.scrimer_CTP);
        container.startShimmerAnimation();
        MainActivity.navigation.setVisibility(View.GONE);
        showDialog = new ShowDialog(getActivity());
        //Nhạn object
        idPhong = chiTietPhong.getIdPhong();
        //Khai báo
        user = view.findViewById(R.id.ivUser);
        save = view.findViewById(R.id.ivSave);
        call = view.findViewById(R.id.ivCall);
        messenger = view.findViewById(R.id.ivMessenger);
        star = view.findViewById(R.id.lnStar);
        tenPhong = view.findViewById(R.id.tvTenPhong);
        giaPhong = view.findViewById(R.id.tvGiaphong);
        tenUser = view.findViewById(R.id.tvUser);
        emailUser = view.findViewById(R.id.tvEmailP);
        moTa = view.findViewById(R.id.tvMota);
        xem = view.findViewById(R.id.btnXemUser);
        datPhong = view.findViewById(R.id.btnDatPhongChiTiet);
        getAllFull();

    }

    private void sendSMS() {
        final Dialog dialog1 = new Dialog(getActivity());
        dialog1.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog1.setContentView(R.layout.sendmasenger);
        dialog1.setCancelable(true);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog1 != null && dialog1.getWindow() != null) {
            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final EditText sms = dialog1.findViewById(R.id.edtsms);
        final TextView phone1 = dialog1.findViewById(R.id.so);
        final TextView sendsms = dialog1.findViewById(R.id.sendsms);
        phone1.setText(fullUser.getPhoneUser());
        sms.setText(getString(R.string.hello) + chiTietPhong.getTenPhong() + getString(R.string.any_room));
        sendsms.setEnabled(false);
        if (checkPermission(Manifest.permission.SEND_SMS)) {
            sendsms.setEnabled(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST_CODE);
        }

        sendsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = phone1.getText().toString();
                String ed = sms.getText().toString();
                if (s == null || s.length() == 0 || ed == null || ed.length() == 0) {
                    return;
                }
                if (checkPermission(Manifest.permission.SEND_SMS)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(s, null, ed, null, null);
                    showDialog.show(getString(R.string.send_done));
                } else {
                    Toast.makeText(getActivity(), R.string.send_erro, Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog1.findViewById(R.id.huysms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
        dialog1.show();
    }

    private void makePhoneCall() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog.setContentView(R.layout.call);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final TextView phone = dialog.findViewById(R.id.number);
        phone.setText(fullUser.getPhoneUser());
        final String number = phone.getText().toString();
        dialog.findViewById(R.id.goi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!number.trim().isEmpty()) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                    } else {
                        String dial = "tel:" + number;
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    }
                } else {
                    showDialog.show(getString(R.string.erro_phone));
                }
            }
        });
        dialog.findViewById(R.id.huy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermission(String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(getActivity(), permission);
        return (checkPermission == PackageManager.PERMISSION_GRANTED);
    }

    private void datPhong() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog.setContentView(R.layout.datphong);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //Tham chiếu id
        img = dialog.findViewById(R.id.ivDatPhong);
        tenPhongDat = dialog.findViewById(R.id.namephong);
        gia = dialog.findViewById(R.id.giaphong);
        hoten = dialog.findViewById(R.id.edtHTen);
        cmnd = dialog.findViewById(R.id.edtCmnd);
        tungay = dialog.findViewById(R.id.edtTuNgay);
        denngay = dialog.findViewById(R.id.edtDenNgay);
        ghichu = dialog.findViewById(R.id.edtGhiChu);
        datPhongDat = dialog.findViewById(R.id.btnDatPhong);
        huyDat = dialog.findViewById(R.id.btnHuyDatPhong);
        tutime = dialog.findViewById(R.id.edtTuTime);
        dentime = dialog.findViewById(R.id.edtDenTime);
        //Set dữ liệu
        if (!chiTietPhong.getImgPhong().get(0).isEmpty()) {
            Picasso.with(getActivity()).load(chiTietPhong.getImgPhong().get(0)).into(img);
        } else {
            img.setImageResource(R.drawable.phongtro);
        }
        tenPhongDat.setText(chiTietPhong.getTenPhong());
        gia.setText(fm.format(Integer.parseInt(chiTietPhong.getGiaPhong())));

        //Lấy thông tin của tk hiện tại
        databaseReferenceFull.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullUserKhach = snapshot.getValue(FullUser.class);
                hoten.setText(fullUserKhach.getUserName());
                cmnd.setText(fullUserKhach.getCmndUser());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tungay.setFocusable(false);
        //Khi chọn vào ngày
        tungay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String ngay = "";
                        if (String.valueOf(month).length() == 1) {
                            ngay = dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                        } else {
                            ngay = dayOfMonth + "/" + (month + 1) + "/" + year;
                        }
                        tungay.setText(ngay);
                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        tutime.setFocusable(false);
        //CHọn giờ
        tutime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                tutime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        dentime.setFocusable(false);
        //CHọn giờ
        dentime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                dentime.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });


        denngay.setFocusable(false);
        //Khi chọn vào ngày
        denngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int m = calendar.get(Calendar.MONTH);
                int y = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String ngay = "";
                        if (String.valueOf(month).length() == 1) {
                            ngay = dayOfMonth + "/" + "0" + (month + 1) + "/" + year;
                        } else {
                            ngay = dayOfMonth + "/" + (month + 1) + "/" + year;
                        }
                        denngay.setText(ngay);
                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });

        datPhongDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check email
                final String ten, cm, tu, den, ghi, time1, time2;
                ten = hoten.getText().toString();
                cm = cmnd.getText().toString();
                tu = tungay.getText().toString();
                den = denngay.getText().toString();
                ghi = ghichu.getText().toString();
                time1 = tutime.getText().toString();
                time2 = dentime.getText().toString();
                Boolean checkNgay = true;
                try {
                    Date tuNgay = simpleDateFormat.parse(tu);
                    Date denNgay = simpleDateFormat.parse(den);
                    if (tuNgay.after(denNgay)) {
                        checkNgay = false;
                    } else {
                        checkNgay = true;
                    }
                } catch (Exception e) {

                }
                //Check lỗi
                if (ten.isEmpty() || cm.isEmpty() || tu.isEmpty() || den.isEmpty() || time1.isEmpty() || time2.isEmpty()) {
                    showDialog.show(getString(R.string.erro_input));
                } else if (!checkNgay) {
                    showDialog.show(getString(R.string.erro_book_toast));
                } else {
                    final String keyGD = chiTietPhong.getIdPhong() + "_" + fullUserKhach.getuID();
                    final GiaoDich giaoDich = new GiaoDich(keyGD, chiTietPhong.getIdPhong(), fullUserKhach.getuID(), ten, cm, time1, tu, time2, den, ghi, "0");
                    //Lấy thông báo ngay thời gian đặt
                    String ngay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                    final ThongBao thongBao = new ThongBao(keyGD, chiTietPhong.getIdPhong(), mAuth.getCurrentUser().getUid(), fullUser.getuID(), ngay, time, "0");
                    databaseReferenceGD.child(keyGD).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            databaseReferenceGD.child(keyGD).setValue(giaoDich);
                            databaseReferenceTB.child(keyGD).setValue(thongBao);
                            MainActivity.navigation.setVisibility(View.VISIBLE);
                            MainActivity.navigation.setSelectedItemId(R.id.giaodich);
                            GiaoDichFragment giaoDichFragment = new GiaoDichFragment();
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.frame, giaoDichFragment)
                                    .commit();
                            GiaoDichFragment.list.clear();
                            GiaoDichFragment.lisTongGG.clear();
                            GiaoDichFragment.listDangGG.clear();
                            showDialog.show(getString(R.string.book_sucess));

                            final String idEmail = chiTietPhong.getuID();
                            databaseReferenceUser.child(idEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User u = snapshot.getValue(User.class);

                                    String token = u.getToken();

                                    if (!token.equals("")) {
                                        String title = "New reservation available";
                                        String message = ten + " have made your reservation. Please confirm the transaction.";
                                        sendNotifications(token, title, message);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }
        });

        huyDat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //Khi ấn nút đặt
        dialog.show();
    }

    private void getSave() {
        check = false;
        if (mAuth.getCurrentUser() != null) {
            databaseReferenceSave.orderByChild("uID").equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listSave.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Save s = postSnapshot.getValue(Save.class);
                        listSave.add(s);
                    }
                    check();
                }

                private void check() {
                    if (listSave != null) {
                        for (int i = 0; i < listSave.size(); i++) {
                            String idP = listSave.get(i).getIdPhong();
                            Log.i("TAG", idP + "");
                            if (idP.equalsIgnoreCase(idPhong)) {
                                save.setChecked(true);
                                break;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public void getAllFull() {
        databaseReferenceFull.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullUser = snapshot.getValue(FullUser.class);
                if (!chiTietPhong.getImgPhong().isEmpty()) {
                    setViewPager();
                }
                tenPhong.setText(chiTietPhong.getTenPhong());
                giaPhong.setText(fm.format(Integer.parseInt(chiTietPhong.getGiaPhong())) + "/day");
                moTa.setText(chiTietPhong.getMoTaPhong());

                //Set cho tài khoản chủ
                if (!fullUser.getLinkImage().isEmpty()) {
                    Picasso.with(getActivity()).load(fullUser.getLinkImage()).into(user);
                }
                tenUser.setText(fullUser.getUserName());
                emailUser.setText(fullUser.getEmailUser());
                container.setVisibility(View.GONE);
                chitiet.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setViewPager() {
        final int size = chiTietPhong.getImgPhong().size();
        pager_indicator = view.findViewById(R.id.viewPagerCountDots);
        if (size == 1) {
            pager_indicator.setVisibility(View.GONE);
        }
        viewPager = view.findViewById(R.id.viewPager);
        ArrayList<String> listImage = chiTietPhong.getImgPhong();
        ImageSlide imageSlide = new ImageSlide(getActivity(), listImage);
        viewPager.setAdapter(imageSlide);
        viewPager.setCurrentItem(0);
        final ImageView dots[] = new ImageView[size];
        for (int i = 0; i < size; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.default_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            final int presentPosition = i;
            dots[presentPosition].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewPager.setCurrentItem(presentPosition);

                }
            });

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < size; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.default_dot));
                }

                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_dot));

                if (position + 1 == size) {

                } else {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        Sender sender = new Sender(data, usertoken);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }
}
