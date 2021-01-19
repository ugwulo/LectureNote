package dev.ugwulo.lecturenote.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.ugwulo.lecturenote.databinding.FragmentCourseDetailsBinding;
import dev.ugwulo.lecturenote.model.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    FragmentCourseDetailsBinding mCourseDetailsBinding;
    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private List<Message> userMessageList;

    public MessageAdapter(List<Message> userMessageList){
        this.userMessageList = userMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentCourseDetailsBinding itemBinding = FragmentCourseDetailsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public MessageViewHolder(@NonNull FragmentCourseDetailsBinding itemView) {
            super(itemView.getRoot());
        }

        public void bind(Message message){

        }
    }
}
