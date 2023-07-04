package com.example.thesisproject;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RegisterScreenActivity extends AppCompatActivity {
    EditText email_edit,userPassword_edit,userPasswordAgain_edit;
    String email,password,passwordAgain;
    Button registerButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        email_edit = findViewById(R.id.email_edit);
        userPassword_edit = findViewById(R.id.userPassword_edit);
        userPasswordAgain_edit = findViewById(R.id.userPasswordAgain_edit);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();
        mAuth.getLanguageCode();

        registerButton.setOnClickListener(v -> register_user());
    }

    public void register_user(){
        email = email_edit.getText().toString().trim();
        password = userPassword_edit.getText().toString().trim();
        passwordAgain = userPasswordAgain_edit.getText().toString().trim();

        if (email.isEmpty()){
            email_edit.setError(getString(R.string.enter_email));
            email_edit.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            email_edit.setError(getString(R.string.unvalid_email));
            email_edit.requestFocus();
            return;
        }
        if(password.isEmpty()){
            userPassword_edit.setError(getString(R.string.enter_password));
            userPassword_edit.requestFocus();
            return;
        }
        if(password.length()<8){
            userPassword_edit.setError(getString(R.string.short_password));
            userPassword_edit.requestFocus();
            return;
        }
        if(!password.equals(passwordAgain)){
            userPasswordAgain_edit.setError(getString(R.string.wrong_passwords));
            userPasswordAgain_edit.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String language = Locale.getDefault().getDisplayLanguage();
                if (language.equals("English")) {
                    mAuth.setLanguageCode(language);
                } else if(language.equals("Turkish")){
                    mAuth.setLanguageCode(language);
                }

                Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification();
                Toast.makeText(getApplicationContext(),getString(R.string.signed_up),Toast.LENGTH_SHORT).show();
                Map<String, Object> data = new HashMap<>();
                data.put("cpu-likes", Collections.emptyList());
                data.put("gpu-likes", Collections.emptyList());
                data.put("laptop-likes", Collections.emptyList());
                FirebaseFirestore user_database = FirebaseFirestore.getInstance();
                String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                assert user_id != null;
                user_database.collection("users").document(user_id).set(data);

                changeActivity();
            } else {
                if(task.getException() instanceof FirebaseAuthUserCollisionException){
                    Toast.makeText(getApplicationContext(),getString(R.string.used_email),Toast.LENGTH_SHORT).show();
                }
            }
        });{
        }
    }

    @Override
    public void onBackPressed() {
        if (!email_edit.getText().toString().equals("") & !userPassword_edit.getText().toString().equals("") & !userPasswordAgain_edit.getText().toString().equals("")) {
            new AlertDialog.Builder(this).setTitle(getString(R.string.return_to_main))
                    .setMessage(getString(R.string.data_will_be_lost))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> changeActivity()).setNegativeButton(getString(R.string.no), null).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            changeActivity();
        }
    }
    public void changeActivity(){
        Intent intent = new Intent(RegisterScreenActivity.this, LoginScreenActivity.class);
        startActivity(intent);
        RegisterScreenActivity.this.finish();
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