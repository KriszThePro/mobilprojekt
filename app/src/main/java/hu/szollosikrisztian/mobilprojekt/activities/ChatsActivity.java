package hu.szollosikrisztian.mobilprojekt.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.utils.IntentUtil;

public class ChatsActivity extends AppCompatActivity {

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.menu_bar);
        setSupportActionBar(toolbar);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        database.getReference("users").child(userId).child("username").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String username = task.getResult().getValue(String.class);
                toolbar.setTitle(username);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        setupToolbar();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu_bar), (view, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            view.setPadding(0, statusBarHeight, 0, 0);
            return insets;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            IntentUtil.navigate(this, LoginActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}