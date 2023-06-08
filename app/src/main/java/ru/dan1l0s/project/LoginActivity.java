package ru.dan1l0s.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/** Login-screen activity */
public class LoginActivity extends AppCompatActivity {

  private boolean state = false; // 0 = login, 1 = signup
  private Fragment current_fragment;
  private TextView textViewRegLink, login_title, swap_title;
  private Button buttonLogin;

  private FirebaseAuth mAuth;

  /** onCreate override to initialize required variables and add listeners */
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    Objects.requireNonNull(getSupportActionBar()).hide();

    current_fragment = new LoginFragment();
    // signup fragment

    setNewFragment(current_fragment);

    swap_title = findViewById(R.id.swap_fragment_title);
    login_title = findViewById(R.id.login_title);
    textViewRegLink = findViewById(R.id.tvRegisterHere);
    buttonLogin = findViewById(R.id.btnLogin);

    mAuth = FirebaseAuth.getInstance();

    buttonLogin.setOnClickListener(v -> { proceed(); });

    textViewRegLink.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View view){
        swap_fragment();
      }
    });
  }

  private void swap_fragment()
  {
    if(!state)
    {
      current_fragment = new RegisterFragment();
      setNewFragment(current_fragment);
      swap_title.setText(getString(R.string.already_registered));
      login_title.setText(getString(R.string.signup_title));
      textViewRegLink.setText(getString(R.string.login_here));
      buttonLogin.setText(getString(R.string.signup_title));
    }
    else  {
      current_fragment = new LoginFragment();
      setNewFragment(current_fragment);
      swap_title.setText(getString(R.string.not_registered));
      login_title.setText(getString(R.string.login_title));
      textViewRegLink.setText(getString(R.string.register_here));
      buttonLogin.setText(getString(R.string.login_title));
    }
    state = !state;
  }
  /** 'Login' and 'Reg' button handler */
  private void proceed() {
    String email, password;
    if (state)
    {
      email = ((RegisterFragment)current_fragment).getEmail();
      password = ((RegisterFragment)current_fragment).getPassword();
    }
    else
    {
      email = ((LoginFragment)current_fragment).getEmail();
      password = ((LoginFragment)current_fragment).getPassword();
    }

    EditText mail = findViewById(R.id.etLoginEmail);
    EditText pswd = findViewById(R.id.etLoginPass);
    if (TextUtils.isEmpty(email)) {
      mail.setError(getString(R.string.reg_email_error));
      mail.requestFocus();
    } else if (TextUtils.isEmpty(password)) {
      pswd.setError(getString(R.string.reg_pass_error));
      pswd.requestFocus();
    }
    else {

      if (state)
      {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                      sendEmailVer();
                      swap_fragment();
                    }
                    else {Toast
                              .makeText(LoginActivity.this,
                                      getString(R.string.reg_error) +
                                              task.getException().getMessage(),
                                      Toast.LENGTH_LONG)
                              .show();
                    }
                  }
                });
      }
      else {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                      if (!mAuth.getCurrentUser().isEmailVerified()) {
                        Toast
                                .makeText(LoginActivity.this,
                                        getString(R.string.email_auth_error),
                                        Toast.LENGTH_SHORT)
                                .show();
                      } else {
                        Toast
                                .makeText(LoginActivity.this,
                                        getString(R.string.login_succ),
                                        Toast.LENGTH_SHORT)
                                .show();
                        finish();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                      }
                    } else {
                      Toast
                              .makeText(LoginActivity.this,
                                      getString(R.string.login_error) +
                                              task.getException().getMessage(),
                                      Toast.LENGTH_LONG)
                              .show();
                    }
                  }
                });
      }
    }
  }

  private void setNewFragment(Fragment fragment) {
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.replace(findViewById(R.id.input_fields).getId(), fragment);
    ft.commit();
  }



  /** Method which sends email verification message and waits for handshake */
  private void sendEmailVer() {
    FirebaseUser user = mAuth.getCurrentUser();
    user.sendEmailVerification().addOnCompleteListener(
            new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                  Toast
                          .makeText(LoginActivity.this,
                                  getString(R.string.email_auth_error),
                                  Toast.LENGTH_SHORT)
                          .show();
                } else {
                  Toast
                          .makeText(LoginActivity.this,
                                  getString(R.string.email_failed),
                                  Toast.LENGTH_SHORT)
                          .show();
                }
              }
            });
  }
}
