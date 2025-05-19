package hu.szollosikrisztian.mobilprojekt.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.adapters.MessageAdapter;
import hu.szollosikrisztian.mobilprojekt.controllers.AuthController;
import hu.szollosikrisztian.mobilprojekt.controllers.ChatController;
import hu.szollosikrisztian.mobilprojekt.controllers.UserController;
import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.utils.IntentUtil;
import hu.szollosikrisztian.mobilprojekt.utils.LogUtil;
import hu.szollosikrisztian.mobilprojekt.viewmodels.ChatViewModel;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST_CODE = 2001;

    private MessageAdapter messageAdapter;
    private AuthController authController;
    private UserController userController;

    private String base64ImageToSend = null;
    private Uri cameraImageUri;
    private FrameLayout imagePreviewContainer;
    private ImageView imagePreview;
    private ImageButton clearPreviewImageButton;

    private boolean isAtBottom = true;
    private boolean hasUserScrolled = false;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        authController = new AuthController();
        userController = new UserController();

        initViews();
        setupToolbar();
        setupChat();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            handleImageSelection(data.getData());
        } else if (requestCode == CAMERA_REQUEST_CODE && cameraImageUri != null) {
            handleImageSelection(cameraImageUri);
        }
    }

    private void initViews() {
        imagePreviewContainer = findViewById(R.id.imagePreviewContainer);
        imagePreview = findViewById(R.id.imagePreview);
        clearPreviewImageButton = findViewById(R.id.clearPreviewImageButton);
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            bitmap = resizeBitmapIfNeeded(bitmap, 800);

            base64ImageToSend = encodeBitmapToBase64(bitmap);

            imagePreview.setImageBitmap(bitmap);
            imagePreviewContainer.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.e(this, "Error picking image", e);
            clearImagePreview();
        }
    }

    private Bitmap resizeBitmapIfNeeded(Bitmap bitmap, int maxWidth) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        if (originalWidth > maxWidth) {
            float ratio = (float) originalHeight / originalWidth;
            int newHeight = Math.round(maxWidth * ratio);
            return Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true);
        }
        return bitmap;
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void clearImagePreview() {
        base64ImageToSend = null;
        imagePreview.setImageBitmap(null);
        imagePreviewContainer.setVisibility(View.GONE);
    }

    private void setupChat() {
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(authController.getCurrentUserId(), userController);
        recyclerView.setAdapter(messageAdapter);

        ChatViewModel chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        setupRecyclerViewScrollListener(recyclerView, layoutManager, chatViewModel);
        observeMessages(chatViewModel, recyclerView, layoutManager);

        messageAdapter.setOnMessageDeleteListener(chatViewModel::deleteMessage);

        setupImagePicker();
        ImageButton cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(v -> openCamera());
        setupClearPreviewButton();
        setupSendButton();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            return;
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = File.createTempFile("IMG_", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            } catch (IOException ex) {
                Toast.makeText(this, "Camera error", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera(); // Try again now that permission is granted
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupRecyclerViewScrollListener(RecyclerView recyclerView, LinearLayoutManager layoutManager, ChatViewModel chatViewModel) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int firstVisible = layoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisible = layoutManager.findLastCompletelyVisibleItemPosition();
                int total = messageAdapter.getItemCount();
                isAtBottom = (lastVisible >= total - 1);
                if (dy != 0) {
                    hasUserScrolled = true;
                }
                // Load more when at the top
                if (firstVisible == 0 && dy < 0) {
                    chatViewModel.loadMoreMessages();
                }
            }
        });
    }

    private void observeMessages(ChatViewModel chatViewModel, RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        chatViewModel.getMessages().observe(this, messages -> {
            if (messages != null && !messages.isEmpty()) {
                boolean shouldScroll = !hasUserScrolled || isAtBottom || isFirstLoad;
                messageAdapter.submitList(messages, () -> {
                    if (shouldScroll) {
                        recyclerView.post(() -> recyclerView.scrollToPosition(messages.size() - 1));
                    }
                    isFirstLoad = false;
                });
            } else {
                messageAdapter.submitList(new java.util.ArrayList<>());
            }
        });
    }

    private void setupImagePicker() {
        ImageButton buttonPickImage = findViewById(R.id.buttonPickImage);
        buttonPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    private void setupClearPreviewButton() {
        clearPreviewImageButton.setOnClickListener(v -> clearImagePreview());
    }

    private void setupSendButton() {
        ImageButton sendButton = findViewById(R.id.sendButton);
        EditText messageInput = findViewById(R.id.messageInput);

        sendButton.setOnClickListener(v -> {
            String str = messageInput.getText().toString().trim();
            ChatController chatController = new ChatController();

            if (base64ImageToSend != null) {
                chatController.sendImageMessage(base64ImageToSend, null);
                clearImagePreview();
            } else if (!str.isEmpty()) {
                chatController.sendMessage(str);
                messageInput.setText("");
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.menuBar);
        setSupportActionBar(toolbar);

        setToolbarTitle(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        setupNavigationView(drawerLayout);
    }

    private void setToolbarTitle(Toolbar toolbar) {
        UserController userController = new UserController();
        userController.getUsername(new ISimpleCallback() {
            @Override
            public void onSuccess(Object result) {
                toolbar.setTitle((String) result);
            }

            @Override
            public void onFailure(Exception e) {
                toolbar.setTitle("Unknown");
            }
        });
    }

    private void setupNavigationView(DrawerLayout drawerLayout) {
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.logoutAction) {
                authController.logoutUser(null);
                IntentUtil.navigate(this, LoginActivity.class);
                return true;
            }

            if (id == R.id.profileAction) {
                IntentUtil.navigate(this, ProfileActivity.class);
                return true;
            }

            drawerLayout.closeDrawers();
            return true;
        });
    }
}