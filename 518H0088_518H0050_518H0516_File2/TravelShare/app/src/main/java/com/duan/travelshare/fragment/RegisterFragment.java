package com.duan.travelshare.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class RegisterFragment extends Fragment {
    private TextInputEditText tk, mk, mk2;
    private TextInputLayout tilTk, tilMk, tilMk2;
    private Button dangKy, nhapLai;
    private String tks, mks, mks2;
    private ProgressDialog progressDialog;
    private ShowDialog showDialog;
    private FirebaseAuth mAuth;
    private String userID;
    View view;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("User");
    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("FullUser");
    private String token;

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_register, container, false);
        showDialog = new ShowDialog(getActivity());
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.tbTitle);
        ImageView back = toolbar.findViewById(R.id.tbBack);
        MainActivity.navigation.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        title.setText(getString(R.string.register));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.navigation.setVisibility(View.VISIBLE);
                LoginFragment userFragment = new LoginFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame, userFragment)
                        .commit();
            }
        });
        tk = view.findViewById(R.id.edtTkDK);
        mk = view.findViewById(R.id.edtMkDK);
        mk2 = view.findViewById(R.id.edtMkDK2);
        tilTk = view.findViewById(R.id.tilTk);
        tilMk = view.findViewById(R.id.tilMk);
        tilMk2 = view.findViewById(R.id.tilMk2);
        dangKy = view.findViewById(R.id.btnDangKy);
        nhapLai = view.findViewById(R.id.btnNhapLai);
        tk.addTextChangedListener(new ValidationTextWatcher(tk));
        mk.addTextChangedListener(new ValidationTextWatcher(mk));
        mk2.addTextChangedListener(new ValidationTextWatcher(mk2));
        progressDialog = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();


        dangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tks = tk.getText().toString();
                mks = mk.getText().toString();
                mks2 = mk2.getText().toString();
                if (validateEmail() & validatePassword() & validateRePass()) {

                    progressDialog.show();
                    progressDialog.setMessage(getString(R.string.registing));
                    mAuth.createUserWithEmailAndPassword(tks, mks)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        userID = mAuth.getCurrentUser().getUid();
                                        User user = new User(userID, tks, mks, "0", "token", "0");
                                        databaseReference.child(userID).setValue(user);
                                        FullUser fullUser = new FullUser(userID, "", "", tks, "", "", "", "");
                                        databaseReference2.child(userID).setValue(fullUser);
                                        Toast.makeText(getActivity(), R.string.reg_success, Toast.LENGTH_SHORT).show();
                                        LoginFragment dangNhap = new LoginFragment();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("tk", tks);
                                        bundle.putString("mk", mks);
                                        dangNhap.setArguments(bundle);
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.frame, dangNhap)
                                                .commit();
                                    } else {
                                        showDialog.show(getString(R.string.exist_accpunt));
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });

        return view;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validatePassword() {
        if (mk.getText().toString().trim().isEmpty()) {
            tilMk.setError(getString(R.string.pw_must));
            requestFocus(mk);
            return false;
        } else if (mk.getText().toString().length() < 6) {
            tilMk.setError(getString(R.string.erro_pw));
            requestFocus(mk);
            return false;
        } else {
            tilMk.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateRePass() {
        if (mk2.getText().toString().trim().isEmpty()) {
            tilMk2.setError(getString(R.string.re_pass));
            requestFocus(mk2);
            return false;
        } else if (mk.getText().toString().equals(mk2.getText().toString())) {
            tilMk2.setErrorEnabled(false);
            return true;
        } else {
            tilMk2.setError(getString(R.string.pw_not_like));
            requestFocus(mk2);
        }
        return false;
    }

    private boolean validateEmail() {
        if (tk.getText().toString().trim().isEmpty()) {
            tilTk.setError(getString(R.string.must_email));
            requestFocus(tk);
            return false;
        } else {
            String emailId = tk.getText().toString();
            Boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
            if (!isValid) {
                tilTk.setError(getString(R.string.erro_email));
                requestFocus(tk);
                return false;
            } else {
                tilTk.setErrorEnabled(false);
            }
        }
        return true;
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
                case R.id.edtTkDK:
                    validateEmail();
                    break;
                case R.id.edtMkDK:
                    validatePassword();
                    break;
                case R.id.edtMkDK2:
                    validateRePass();
                    break;
            }
        }
    }


    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

}