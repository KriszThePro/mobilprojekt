package hu.szollosikrisztian.mobilprojekt.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.models.MessageModel;

public class ChatViewModel extends ViewModel {

    // LiveData for messages
    private final MutableLiveData<List<MessageModel>> messages = new MutableLiveData<>(new ArrayList<>());

    // Firestore instance and pagination state
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentSnapshot lastVisible = null;
    private boolean isLoading = false;
    private boolean hasMore = true;

    private static final int PAGE_SIZE = 25;

    public ChatViewModel() {
        listenForRealtimeUpdates();
    }

    // Expose messages as LiveData
    public LiveData<List<MessageModel>> getMessages() {
        if (Objects.requireNonNull(messages.getValue()).isEmpty()) {
            loadMoreMessages();
        }
        return messages;
    }

    // Load next page of messages
    public void loadMoreMessages() {
        if (isLoading || !hasMore) return;
        isLoading = true;

        Query query = firestore.collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE);

        if (lastVisible != null) {
            query = query.startAfter(lastVisible);
        }

        query.get().addOnSuccessListener(snapshot -> {
            List<MessageModel> currentMessages = new ArrayList<>(Objects.requireNonNull(messages.getValue()));
            List<MessageModel> newMessages = extractMessagesFromSnapshot(snapshot);

            if (!snapshot.isEmpty()) {
                lastVisible = snapshot.getDocuments().get(snapshot.size() - 1);
            }
            if (newMessages.size() < PAGE_SIZE) {
                hasMore = false;
            }

            // Reverse to maintain chronological order
            Collections.reverse(newMessages);
            currentMessages.addAll(0, newMessages);
            messages.setValue(currentMessages);
            isLoading = false;
        }).addOnFailureListener(e -> isLoading = false);
    }

    // Reset pagination and reload messages
    public void resetPagination() {
        lastVisible = null;
        hasMore = true;
        messages.setValue(new ArrayList<>());
        loadMoreMessages();
    }

    // Delete a message from Firestore and update LiveData
    public void deleteMessage(MessageModel message) {
        firestore.collection("messages")
                .document(message.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    List<MessageModel> current = new ArrayList<>(Objects.requireNonNull(messages.getValue()));
                    current.removeIf(m -> m.getId().equals(message.getId()));
                    messages.setValue(current);
                });
    }

    // Listen for real-time updates from Firestore
    private void listenForRealtimeUpdates() {
        firestore.collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null || snapshots == null) return;
                    List<MessageModel> updatedMessages = extractMessagesFromSnapshot(snapshots);
                    messages.setValue(updatedMessages);
                });
    }

    // Helper to extract MessageModel list from Firestore snapshot
    private List<MessageModel> extractMessagesFromSnapshot(com.google.firebase.firestore.QuerySnapshot snapshot) {
        List<MessageModel> messageList = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            MessageModel msg = doc.toObject(MessageModel.class);
            if (msg != null) {
                msg.setId(doc.getId());
                messageList.add(msg);
            }
        }
        return messageList;
    }
}