package hu.szollosikrisztian.mobilprojekt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.util.IntentUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button mLoginButton;
    private Button mRegisterButton;
    private ProgressBar mProgressBar;

    private void initializeActivityComponents() {
        mEmailInput = findViewById(R.id.email_input);
        mPasswordInput = findViewById(R.id.password_input);
        mLoginButton = findViewById(R.id.login_button);
        mRegisterButton = findViewById(R.id.register_button);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    private void handleLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        mProgressBar.setVisibility(View.VISIBLE);
        String email = mEmailInput.getText().toString();
        String password = mPasswordInput.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    mProgressBar.setVisibility(View.GONE);

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    navigateToMain();
                });
    }

    private void navigateToMain() {
        IntentUtil.navigate(this, RegisterActivity.class);
    }

    private void setupLoginButtonClickHandler() {
        mLoginButton.setOnClickListener(v -> handleLogin());
    }

    private void setupRegisterButtonClickHandler() {
        mRegisterButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        initializeActivityComponents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupLoginButtonClickHandler();
        setupRegisterButtonClickHandler();
    }
}