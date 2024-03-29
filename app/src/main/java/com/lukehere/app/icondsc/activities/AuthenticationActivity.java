package com.lukehere.app.icondsc.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lukehere.app.icondsc.R;

public class AuthenticationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private MaterialCardView mPrivacyPolicyCard;
    private TextInputEditText mEmailAddressEditText;
    private TextInputEditText mPasswordEditText;
    private ProgressBar mSignInProgressBar;

    private int mBackCount = 0;

    @Override
    public void onBackPressed() {
        mBackCount++;

        if (mBackCount == 1) {
            Toast.makeText(this, getString(R.string.back_to_exit), Toast.LENGTH_SHORT).show();
        } else if (mBackCount == 2) {
            finishAffinity();
        }
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            setContentView(R.layout.activity_authentication);

            mPrivacyPolicyCard = findViewById(R.id.privacy_policy_card);
            mEmailAddressEditText = findViewById(R.id.email);
            mPasswordEditText = findViewById(R.id.password);
            Button mSignInButton = findViewById(R.id.sign_in_button);
            Button mForgotPasswordButton = findViewById(R.id.forgot_password_button);
            Button mSignUpButton = findViewById(R.id.sign_up_button);
            mSignInProgressBar = findViewById(R.id.sign_in_progress_bar);

            mPrivacyPolicyCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.freeprivacypolicy.com/privacy/view/f523fdd7391d2e01af72ee1a3b2f8a95"));
                    startActivity(i);
                }
            });

            mSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean check = true;
                    String email = mEmailAddressEditText.getText().toString().trim();
                    String password = mPasswordEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(email)) {
                        mEmailAddressEditText.setError(getString(R.string.enter_registered_email_address));
                        if (check) {
                            check = false;
                        }
                    } else {
                        mEmailAddressEditText.setError(null);
                    }

                    if (TextUtils.isEmpty(password)) {
                        mPasswordEditText.setError(getString(R.string.please_enter_password));
                        if (check) {
                            check = false;
                        }
                    } else {
                        mPasswordEditText.setError(null);
                    }

                    if (check) {
                        mSignInProgressBar.setVisibility(View.VISIBLE);
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();

                                        } else {
                                            Toast.makeText(AuthenticationActivity.this, getString(R.string.authentication_failed),
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        mSignInProgressBar.setVisibility(View.GONE);

                                    }
                                });
                    }
                }
            });

            mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AuthenticationActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                }
            });

            mSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AuthenticationActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}