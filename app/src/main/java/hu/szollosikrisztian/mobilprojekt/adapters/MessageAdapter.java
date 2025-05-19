package hu.szollosikrisztian.mobilprojekt.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import hu.szollosikrisztian.mobilprojekt.R;
import hu.szollosikrisztian.mobilprojekt.controllers.UserController;
import hu.szollosikrisztian.mobilprojekt.interfaces.ISimpleCallback;
import hu.szollosikrisztian.mobilprojekt.models.MessageModel;
import hu.szollosikrisztian.mobilprojekt.models.UserModel;

public class MessageAdapter extends ListAdapter<MessageModel, MessageAdapter.MessageViewHolder> {

    private final String currentUserId;

    private final UserController userController;

    private OnMessageDeleteListener deleteListener;

    public MessageAdapter(String currentUserId, UserController userController) {
        super(DIFF_CALLBACK);
        this.currentUserId = currentUserId;
        this.userController = userController;
    }

    private static final DiffUtil.ItemCallback<MessageModel> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<MessageModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
                    return oldItem.getTimestamp() == newItem.getTimestamp() &&
                            Objects.equals(oldItem.getSender(), newItem.getSender());
                }

                @Override
                public boolean areContentsTheSame(@NonNull MessageModel oldItem, @NonNull MessageModel newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @Override
    public int getItemViewType(int position) {
        MessageModel message = getItem(position);
        if (message.getSender().equals(currentUserId)) {
            return 1; // Right (current user)
        } else {
            return 0; // Left (other user)
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_message_right : R.layout.item_message_left;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModel message = getItem(position);
        holder.bind(getItem(position), this.userController);

        holder.imageMessage.setOnClickListener(v -> {
            Context context = v.getContext();
            Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            ImageView imageView = new ImageView(context);
            imageView.setImageDrawable(holder.imageMessage.getDrawable());
            //imageView.setBackgroundColor(Color.BLACK);
            imageView.setOnClickListener(view -> dialog.dismiss());
            dialog.setContentView(imageView);
            dialog.show();
        });

        holder.imageMessage.setOnLongClickListener(v -> {
            if (message.getSender().equals(currentUserId) && deleteListener != null) {
                Context context = v.getContext();
                new AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteListener.onDelete(message))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (message.getSender().equals(currentUserId) && deleteListener != null) {
                Context context = v.getContext();
                new AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setMessage("Are you sure you want to delete this message?")
                        .setPositiveButton("Delete", (dialog, which) -> deleteListener.onDelete(message))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });
    }

    public void setOnMessageDeleteListener(OnMessageDeleteListener listener) {
        this.deleteListener = listener;
    }

    public interface OnMessageDeleteListener {
        void onDelete(MessageModel message);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView sender;
        private final TextView textMessage;
        private final ImageView imageMessage;
        private final TextView timeText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            sender = itemView.findViewById(R.id.textSender);
            textMessage = itemView.findViewById(R.id.textMessage);
            imageMessage = itemView.findViewById(R.id.imageMessage);
            timeText = itemView.findViewById(R.id.textTime);
        }

        void bind(MessageModel message, UserController userController) {
            userController.getUserById(message.getSender(), new ISimpleCallback() {
                @Override
                public void onSuccess(Object result) {
                    sender.setText(((UserModel) result).getUsername());
                    textMessage.setText(message.getTextMessage());
                    timeText.setText(formatTime(message.getTimestamp()));

                    if (message.getBase64Image() != null && !message.getBase64Image().isEmpty()) {
                        byte[] decodedString = Base64.decode(message.getBase64Image(), Base64.DEFAULT);
                        imageMessage.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                        imageMessage.setVisibility(View.VISIBLE);
                        textMessage.setVisibility(View.GONE);
                    } else {
                        imageMessage.setVisibility(View.GONE);
                        textMessage.setVisibility(View.VISIBLE);
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(Exception e) {
                    sender.setText("<Unknown>");
                    textMessage.setText(message.getTextMessage());
                    timeText.setText(formatTime(message.getTimestamp()));
                }
            });
        }

        private String formatTime(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}