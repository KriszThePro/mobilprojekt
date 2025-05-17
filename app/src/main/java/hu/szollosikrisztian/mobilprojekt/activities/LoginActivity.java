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
import hu.szollosikrisztian.mobilprojekt.utils.IntentUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInputField;
    private EditText passwordInputField;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar progressBar;

    private void initializeActivityComponents() {
        this.emailInputField = findViewById(R.id.email_input);
        this.passwordInputField = findViewById(R.id.password_input);
        this.loginButton = findViewById(R.id.login_button);
        this.registerButton = findViewById(R.id.register_button);
        this.progressBar = findViewById(R.id.progress_bar);
    }

    private void handleLogin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        this.progressBar.setVisibility(View.VISIBLE);
        String email = this.emailInputField.getText().toString();
        String password = this.passwordInputField.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            this.progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    this.progressBar.setVisibility(View.GONE);

                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Login failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    navigateToRegister();
                });
    }

    private void navigateToRegister() {
        IntentUtil.navigate(this, RegisterActivity.class);
    }

    private void setupLoginButtonClickHandler() {
        this.loginButton.setOnClickListener(v -> handleLogin());
    }

    private void setupRegisterButtonClickHandler() {
        this.registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        this.initializeActivityComponents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.setupLoginButtonClickHandler();
        this.setupRegisterButtonClickHandler();
    }
}