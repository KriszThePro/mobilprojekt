package hu.szollosikrisztian.mobilprojekt.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.controllers.UserController;
import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.models.UserModel;

public class ProfileActivity extends AppCompatActivity {

    private EditText editUsername;
    private UserModel currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        editUsername = findViewById(R.id.editUsername);
        Button buttonSave = findViewById(R.id.buttonSave);

        UserController userController = new UserController();

        userController.currentUserDetails().get().addOnSuccessListener(doc -> {
            if (doc.exists() && doc.contains("username")) {
                String username = doc.getString("username");
                currentUser = new UserModel(username);
                editUsername.setText(username);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ProfileActivity.this, "Failed to load user", Toast.LENGTH_SHORT).show();
        });

        buttonSave.setOnClickListener(v -> {
            String newUsername = editUsername.getText().toString().trim();
            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUser != null) {
                currentUser.setUsername(newUsername);
                userController.storeUserData(currentUser, new ISimpleCallback() {
                    @Override
                    public void onSuccess(Object data) {
                        Toast.makeText(ProfileActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}