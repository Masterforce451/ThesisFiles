package com.example.thesisproject;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class LoginScreenActivity extends AppCompatActivity {
    Button loginButton,registerButton,withoutLoginButton;
    EditText email_edit,userpassword_edit;
    FirebaseAuth mAuth;
    String email,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if(user.isEmailVerified()) {
                Intent intent = new Intent(LoginScreenActivity.this,HomeScreenActivity.class);
                startActivity(intent);
                LoginScreenActivity.this.finish();
            }
        }

        email_edit = findViewById(R.id.email_edit);
        userpassword_edit = findViewById(R.id.userPassword_edit);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> userLogin());

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this,RegisterScreenActivity.class);
            startActivity(intent);
            LoginScreenActivity.this.finish();
        });

        withoutLoginButton = findViewById(R.id.withoutLoginButton);
        withoutLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this,HomeScreenActivity.class);
            startActivity(intent);
            LoginScreenActivity.this.finish();
        });
    }

    private void userLogin() {
        email = email_edit.getText().toString().trim();
        password = userpassword_edit.getText().toString().trim();
        if (email.isEmpty()) {
            email_edit.setError(getString(R.string.enter_email));
            email_edit.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_edit.setError(getString(R.string.unvalid_email));
            email_edit.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            userpassword_edit.setError(getString(R.string.enter_password));
            userpassword_edit.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (!Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                mAuth.getCurrentUser().sendEmailVerification();
                Toast.makeText(getApplicationContext(), getString(R.string.verified_false), Toast.LENGTH_SHORT).show();
            } else {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.signed_in), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginScreenActivity.this,HomeScreenActivity.class);
                    startActivity(intent);
                    LoginScreenActivity.this.finish();
                } else {
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        userpassword_edit.setError(getString(R.string.wrong_password));
                        userpassword_edit.requestFocus();
                    }
                }
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}