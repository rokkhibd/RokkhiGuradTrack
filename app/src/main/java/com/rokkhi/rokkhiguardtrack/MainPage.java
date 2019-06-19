package com.rokkhi.rokkhiguardtrack;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Context context;
    RelativeLayout mrootview;
    private static final String TAG = "MainActivity";
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // Normalfunc normalfunc;
    private static final int RC_SIGN_IN = 12773;
    AuthUI.IdpConfig phoneConfigWithDefaultNumber;

    RelativeLayout main,splash1;
    Button logOut,cwp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainPage.this;
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        main= findViewById(R.id.main);
        splash1= findViewById(R.id.splash);
        logOut=findViewById(R.id.log_out_btn);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null) {
                    gologout();


                } else {

                    firebaseFirestore.collection("allguards").document(firebaseUser.getPhoneNumber()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot != null && documentSnapshot.exists()) {
                                    String gphone = documentSnapshot.getString("g_phone");
                                    Toast.makeText(context, "Kaj hoise", Toast.LENGTH_SHORT).show();


                                }
                                else if(!documentSnapshot.exists()){
                                    //show dialog

                                    splash1.setVisibility(View.VISIBLE);
                                    main.setVisibility(View.GONE);

                                }
                            }
                        }
                    });


                }
            }
        };


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

    }
    private void gologout() {
        List<String> whitelistedCountries = new ArrayList<String>();
        whitelistedCountries.add("bd");
        phoneConfigWithDefaultNumber = new AuthUI.IdpConfig.PhoneBuilder()
                .setDefaultCountryIso("bd")
                .setWhitelistedCountries(whitelistedCountries)
                .build();

        signInPhone(mrootview);

    }

    public void signInPhone(View view) {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(phoneConfigWithDefaultNumber))
                        .build(),
                RC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
        }
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK) {
            Log.d(TAG, "handleSignInResponse: checkhere ");
            Log.d(TAG, "handleSignInResponse: jjj " + FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            //loaduserdata();

        } else {
            if (response == null) {
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }
            if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

    }

    private void showSnackbar(int errorMessageRes) {
        Snackbar.make(mrootview, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }
}

