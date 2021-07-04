package com.example.textapp.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textapp.Models.Message;
import com.example.textapp.R;
import com.example.textapp.databinding.ItemReceiveBinding;
import com.example.textapp.databinding.ItemSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    String senderRoom;
    String receiverRoom;

    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_receive, parent, false);
            return new ReceiveViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);

        if (Objects.equals(FirebaseAuth.getInstance().getUid(), message.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Message message = messages.get(position);

        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == SendViewHolder.class) {
                SendViewHolder viewHolder = (SendViewHolder) holder;
                viewHolder.binding.reaction.setImageResource(reactions[pos]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            } else {
                ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
                viewHolder.binding.reaction.setImageResource(reactions[pos]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            }

            message.setReaction(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup and false is requesting a new selection
        });

        if (holder.getClass() == SendViewHolder.class) {
            SendViewHolder viewHolder = (SendViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getReaction() >= 0) {
                viewHolder.binding.reaction.setImageResource(reactions[message.getReaction()]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.reaction.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });

        } else {
            ReceiveViewHolder viewHolder = (ReceiveViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            if (message.getReaction() >= 0) {
                viewHolder.binding.reaction.setImageResource(reactions[message.getReaction()]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.reaction.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((v, event) -> {
                popup.onTouch(v, event);
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SendViewHolder extends RecyclerView.ViewHolder {

        ItemSendBinding binding;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public static class ReceiveViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceiveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
        }
    }
}
