package hu.szollosikrisztian.mobilprojekt.controllers;

import com.google.firebase.firestore.FirebaseFirestore;

import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.models.MessageModel;
import hu.szollosikrisztian.mobilprojekt.utils.LogUtil;

public final class ChatController {

    private final FirebaseFirestore firestore;
    private final AuthController auth;

    public ChatController() {
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = new AuthController();
    }

    public void sendMessage(String text) {
        sendMessage(text, null);
    }

    public void sendMessage(String text, ISimpleCallback listener) {
        long timestamp = System.currentTimeMillis();
        MessageModel message = new MessageModel(text, false, auth.getCurrentUserId(), timestamp);

        firestore.collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    LogUtil.d(ChatController.this, "Message sent: " + documentReference.getId());
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.e(ChatController.this, "Error sending message", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    public void sendImageMessage(String base64Image, ISimpleCallback listener) {
        long timestamp = System.currentTimeMillis();
        MessageModel message = new MessageModel(null, true, auth.getCurrentUserId(), timestamp);
        message.setBase64Image(base64Image);

        firestore.collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    LogUtil.d(ChatController.this, "Image message sent: " + documentReference.getId());
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    LogUtil.e(ChatController.this, "Error sending image message", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }
}