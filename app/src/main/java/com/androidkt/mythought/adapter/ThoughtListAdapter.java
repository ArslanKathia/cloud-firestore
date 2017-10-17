package com.androidkt.mythought.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidkt.mythought.R;
import com.androidkt.mythought.Thought;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by brijesh on 7/10/17.
 */

public class ThoughtListAdapter extends FirestoreAdapter<ThoughtListAdapter.ThoughtItemHolder> {


    ClickListener clickListener;

    public ThoughtListAdapter(Query query, ClickListener clickListener) {
        super(query);
        this.clickListener = clickListener;
    }

    @Override
    public ThoughtItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ThoughtItemHolder(layoutInflater.inflate(R.layout.item_thought, parent, false), clickListener);
    }

    @Override
    public void onBindViewHolder(ThoughtItemHolder holder, int position) {
        holder.bindData(getSnapshot(position));
    }

    public static class ThoughtItemHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.view_foreground)
        public RelativeLayout viewForeground;
        @BindView(R.id.view_background)
        public RelativeLayout viewBackground;
        @BindView(R.id.thought)
        TextView thoughtView;
        @BindView(R.id.publishBy)
        TextView publishByView;


        public ThoughtItemHolder(View itemView, final ClickListener clickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }

        public void bindData(DocumentSnapshot documentSnapshot) {
            Thought thought = documentSnapshot.toObject(Thought.class);
            thoughtView.setText(thought.getText());
            publishByView.setText(thought.getPublisherBy());
        }


    }


}
