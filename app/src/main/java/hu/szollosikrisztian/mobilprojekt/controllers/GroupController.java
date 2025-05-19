package hu.szollosikrisztian.mobilprojekt.controllers;

import androidx.lifecycle.LiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

import hu.szollosikrisztian.mobilprojekt.models.ChatRoomModel;
import hu.szollosikrisztian.mobilprojekt.models.MessageModel;
import hu.szollosikrisztian.mobilprojekt.utils.FirestoreLiveData;
import hu.szollosikrisztian.mobilprojekt.utils.LogUtil;

public class GroupController {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface FirestoreCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void createGroup(ChatRoomModel chatRoom, FirestoreCallback callback) {
        chatRoom.setCreatedAt(null); // Firestore szerveridőt használunk

        db.collection("groups")
                .add(chatRoom)
                .addOnSuccessListener(documentReference -> {
                    LogUtil.d(this, "Group created: " + documentReference.getId());
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    LogUtil.e(this, "Error creating group", e);
                    callback.onFailure(e);
                });
    }

    public void sendMessageToGroup(String groupId, MessageModel message, FirestoreCallback callback) {
        db.collection("groups")
                .document(groupId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    LogUtil.d(this, "Message sent");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    LogUtil.e(this, "Error sending message", e);
                    callback.onFailure(e);
                });
    }

    public LiveData<List<ChatRoomModel>> getGroupsForUser(String userId) {
        Query query = db.collection("groups")
                .whereArrayContains("participants", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);

        return new FirestoreLiveData<>(query, ChatRoomModel.class);
    }

    public LiveData<List<MessageModel>> getMessagesForGroup(String groupId) {
        Query query = db.collection("groups")
                .document(groupId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        return new FirestoreLiveData<>(query, MessageModel.class);
    }
}
