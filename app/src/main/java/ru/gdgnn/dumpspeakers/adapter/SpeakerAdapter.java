package ru.gdgnn.dumpspeakers.adapter;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import ru.gdgnn.dumpspeakers.R;
import ru.gdgnn.dumpspeakers.model.Speaker;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Speakers.
 */
public class SpeakerAdapter extends FirestoreAdapter<SpeakerAdapter.ViewHolder> {

    public interface OnSpeakerSelectedListener {

        void onSpeakerSelected(DocumentSnapshot speaker);

    }

    private OnSpeakerSelectedListener mListener;

    public SpeakerAdapter(Query query, OnSpeakerSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_speaker, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.speaker_item_image)
        ImageView imageView;

        @BindView(R.id.speaker_item_name)
        TextView nameView;

        @BindView(R.id.speaker_item_speech)
        TextView speechView;

        @BindView(R.id.speaker_item_job)
        TextView jobView;

        @BindView(R.id.speaker_item_rating)
        MaterialRatingBar ratingBar;

        @BindView(R.id.speaker_item_num_ratings)
        TextView numRatingsView;

        @BindView(R.id.speaker_item_section)
        TextView sectionView;

        @BindView(R.id.speaker_item_city)
        TextView cityView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnSpeakerSelectedListener listener) {

            Speaker speaker = snapshot.toObject(Speaker.class);
            Resources resources = itemView.getResources();

            // Load image
            Glide.with(imageView.getContext())
                    .load(speaker.getPhoto())
                    .into(imageView);

            nameView.setText(speaker.getName());
            speechView.setText(speaker.getSpeech());
            jobView.setText(speaker.getJob());
            ratingBar.setRating((float) speaker.getAvgRating());
            cityView.setText(speaker.getCity());
            sectionView.setText(speaker.getSection());
            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings,
                    speaker.getNumRatings()));
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onSpeakerSelected(snapshot);
                    }
                }
            });
        }

    }
}
