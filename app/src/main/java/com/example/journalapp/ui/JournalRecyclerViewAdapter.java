package com.example.journalapp.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.journalapp.R;
import com.example.journalapp.model.Journal;

import java.util.List;

public class JournalRecyclerViewAdapter extends RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Journal> journalList;

    public JournalRecyclerViewAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.journal_row,viewGroup,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalRecyclerViewAdapter.ViewHolder holder, int position){
        Journal journal = journalList.get(position);
        String imageUrl;

        holder.titleOfPosts.setText(journal.getTitleOfPost());
        holder.comments.setText(journal.getComments());
        holder.name.setText(journal.getUserName());
        imageUrl = journal.getImageUrl();

        String timeAgo = (String) DateUtils.getRelativeTimeSpanString(
                journal.getTimestamp()
                        .getSeconds()*1000);
        holder.dateAdded.setText(timeAgo);

        //Glide library to display images
        Glide.with(context)
                .load(imageUrl)
                //.placeholder()
                .fitCenter()
                .into(holder.imageV);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    //ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleOfPosts, comments, name, dateAdded;
        public ImageView imageV;
        public ImageView shareBtn;
        String userId;
        String username;

        public ViewHolder(@NonNull View itemView, Context context1) {
            super(itemView);
            context = context1;
            titleOfPosts = itemView.findViewById(R.id.titleList);
            comments = itemView.findViewById(R.id.commentsList);
            dateAdded = itemView.findViewById(R.id.timestampList);
            name = itemView.findViewById(R.id.rowUsername);

            imageV = itemView.findViewById(R.id.imageList);

            shareBtn = itemView.findViewById(R.id.rowShareButton);
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //sharing posts
                }
            });
        }
    }
}
