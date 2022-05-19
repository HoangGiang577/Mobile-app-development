package com.duan.travelshare.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duan.travelshare.MainActivity;
import com.duan.travelshare.R;
import com.duan.travelshare.model.FullUser;
import com.duan.travelshare.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragment extends Fragment implements View.OnClickListener {
    static CardView fullInfo, roomMng, roomFav, partnerRoom, changePass, logOut, login;
    private ImageView avatar;
    private TextView name, email;
    private User users;
    ShowDialog show;
    public static FullUser list;
    View.OnClickListener listener;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("User");
    DatabaseReference databaseReferenceFull = firebaseDatabase.getReference("FullUser");
    private FirebaseAuth mAuth;
    public static String uID;
    String token;
    private TextInputEditText pass, newPass, newPass2;
    private TextInputLayout tilMk, tilMKN, tilMKN2;

    public UserFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            uID = mAuth.getCurrentUser().getUid();
            getToken();
        }
//        getFullUser();
        MainActivity.navigation.setVisibility(View.VISIBLE);
        show = new ShowDialog(getActivity());
        //Khai báo các id
        avatar = view.findViewById(R.id.imgAvatar);
        fullInfo = view.findViewById(R.id.cvUserInfo);
        roomFav = view.findViewById(R.id.cvRoomFavorite);
        roomMng = view.findViewById(R.id.cvRoomManager);
        partnerRoom = view.findViewById(R.id.cvPartner);
        changePass = view.findViewById(R.id.cvChangePass);
        logOut = view.findViewById(R.id.cvDangXuat);
        login = view.findViewById(R.id.cvDangNhap);
        name = view.findViewById(R.id.tvNameUser);
        email = view.findViewById(R.id.tvEmail);

        try {
            if (!uID.isEmpty()) {
                fullInfo.setOnClickListener(this);
                roomMng.setOnClickListener(this);
                roomFav.setOnClickListener(this);
                partnerRoom.setOnClickListener(this);
                changePass.setOnClickListener(this);
                logOut.setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        login.setOnClickListener(this);

        //Check đã đăng nhập chưa
        checkHaveUser();
        checkLoaiUser();
        return view;
    }

    private void checkHaveUser() {
        if (mAuth.getCurrentUser() != null) {
            login.setVisibility(View.GONE);
            changePass.setVisibility(View.VISIBLE);
            logOut.setVisibility(View.VISIBLE);
            setUser();
        }
    }

    private void dangXuat() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.show2);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView text = dialog.findViewById(R.id.tvInfo2);
        Button ok = dialog.findViewById(R.id.btnOK);
        Button cancle = dialog.findViewById(R.id.btnCancle);

        text.setText(R.string.want_logout);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                LoginFragment fragment = new LoginFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame, fragment)
                        .commit();
                dialog.dismiss();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void changePass() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().getAttributes().windowAnimations = R.style.up_down;
        dialog.setContentView(R.layout.change_pass);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        final Button changePass, cancle;
        pass = dialog.findViewById(R.id.edtOldPassword);
        newPass = dialog.findViewById(R.id.edtPasswordNew);
        newPass2 = dialog.findViewById(R.id.edtPasswordNew2);
        changePass = dialog.findViewById(R.id.btnChangePass);
        cancle = dialog.findViewById(R.id.btnCanclePass);
        tilMk = dialog.findViewById(R.id.tilMKC);
        tilMKN = dialog.findViewById(R.id.tilMKM);
        tilMKN2 = dialog.findViewById(R.id.tilMKM2);
        pass.addTextChangedListener(new UserFragment.ValidationTextWatcher(pass));
        newPass.addTextChangedListener(new UserFragment.ValidationTextWatcher(newPass));
        newPass2.addTextChangedListener(new UserFragment.ValidationTextWatcher(newPass2));
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.show();
                progressDialog.setMessage(getString(R.string.update_success));
                final String passOld, passNew, passNew2;
                passOld = pass.getText().toString();
                passNew = newPass.getText().toString();
                passNew2 = newPass2.getText().toString();
                //Check mật khẩu cũ và mật khẩu mới
                if (validatePassNew2() & validatePassNew() & validatePassOld()) {


                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), passOld);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(passNew)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    databaseReference.child(uID).child("password").setValue(passNew);
                                                    show.show(getString(R.string.change_pw_success));
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                    FirebaseAuth.getInstance().signOut();
                                                    LoginFragment fragment = new LoginFragment();
                                                    getActivity().getSupportFragmentManager()
                                                            .beginTransaction()
                                                            .replace(R.id.frame, fragment)
                                                            .commit();
                                                } else {
                                                    show.show(getString(R.string.change_pw_failed));
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                            }
                        }
                    });

                }


                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dangKyChu() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.show2);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        TextView text = dialog.findViewById(R.id.tvInfo2);
        Button ok = dialog.findViewById(R.id.btnOK);
        Button cancle = dialog.findViewById(R.id.btnCancle);

        text.setText("Do you want to become a rental owner?");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    databaseReference.child(uID).child("loaiUser").setValue("1");
                    checkLoaiUser();
                    show.show(getString(R.string.succer_seller));
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void checkLoaiUser() {
        if (mAuth.getCurrentUser() != null) {
            databaseReference.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    users = snapshot.getValue(User.class);
                    String loaiUser = users.getLoaiUser();
                    if (loaiUser.equalsIgnoreCase("0")) {
                        roomMng.setVisibility(View.GONE);
                        partnerRoom.setVisibility(View.VISIBLE);
                    } else if (loaiUser.equalsIgnoreCase("1")) {
                        roomMng.setVisibility(View.VISIBLE);
                        partnerRoom.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Toolbar
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.tbTitle);
        ImageView back = toolbar.findViewById(R.id.tbBack);
        title.setText(R.string.user_tit);
        back.setVisibility(View.INVISIBLE);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.cvUserInfo:
                fragment = new ShowUserFragment();
                break;
            case R.id.cvRoomFavorite:
                fragment = new SaveFragment();
                break;
            case R.id.cvRoomManager:
                fragment = new ManegerPhongThueFragment();
                break;
            case R.id.cvPartner:
                dangKyChu();
                break;
            case R.id.cvChangePass:
                changePass();
                break;
            case R.id.cvDangXuat:
                dangXuat();
                break;
            case R.id.cvDangNhap:
                fragment = new LoginFragment();
                break;
        }
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,R.anim.from_defaut, R.anim.slide_bottom,R.anim.from_defaut)
                    .replace(R.id.frame, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void setUser() {
        if (mAuth.getCurrentUser() != null) {
            databaseReferenceFull.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    FullUser fullUser = snapshot.getValue(FullUser.class);
                    if (fullUser.getUserName().isEmpty()) {
                        ShowUserFragment fragment = new ShowUserFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frame, fragment)
                                .commit();
                    }
                    email.setText(fullUser.getEmailUser());
                    if (!fullUser.getUserName().isEmpty()) {
                        name.setText(fullUser.getUserName());
                    }
                    if (!fullUser.getLinkImage().isEmpty()) {
                        Picasso.with(getContext()).load(fullUser.getLinkImage()).into(avatar);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        //Cập nhật token
                        databaseReference.child(mAuth.getCurrentUser().getUid()).child("token").setValue(token);

                    }
                });

    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private boolean validatePassOld() {
        if (pass.getText().toString().trim().isEmpty()) {
            tilMk.setError(getString(R.string.emty));
            requestFocus(pass);
            return false;
        } else if (pass.getText().toString().trim().length() < 6) {
            tilMk.setError(getString(R.string.erro_pw));
            requestFocus(pass);
            return false;
        } else if (!users.getPassword().trim().equalsIgnoreCase(pass.getText().toString())) {
            tilMk.setError(getString(R.string.failed_pw));
            requestFocus(pass);
            return false;
        } else {
            tilMk.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassNew() {
        if (newPass.getText().toString().trim().isEmpty()) {
            tilMKN.setError(getString(R.string.emty));
            requestFocus(newPass);
            return false;
        } else if (newPass.getText().toString().trim().length() < 6) {
            tilMKN.setError(getString(R.string.erro_pw));
            requestFocus(newPass);
            return false;
        } else {
            tilMKN.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassNew2() {
        if (newPass2.getText().toString().trim().isEmpty()) {
            tilMKN2.setError(getString(R.string.emty));
            requestFocus(newPass2);
            return false;
        } else if (!newPass2.getText().toString().trim().equalsIgnoreCase(newPass.getText().toString())) {
            tilMKN2.setError(getString(R.string.pw_notlike));
            requestFocus(newPass2);
            return false;
        } else {
            tilMKN2.setErrorEnabled(false);
            return true;
        }
    }

    private class ValidationTextWatcher implements TextWatcher {

        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.edtOldPassword:
                    validatePassOld();
                    break;
                case R.id.edtPasswordNew:
                    validatePassNew();
                    break;
                case R.id.edtPasswordNew2:
                    validatePassNew2();
                    break;
            }
        }
    }
}