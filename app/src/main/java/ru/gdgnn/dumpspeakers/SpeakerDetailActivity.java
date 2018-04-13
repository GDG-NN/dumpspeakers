package ru.gdgnn.dumpspeakers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import ru.gdgnn.dumpspeakers.adapter.RatingAdapter;
import ru.gdgnn.dumpspeakers.model.Rating;
import ru.gdgnn.dumpspeakers.model.Speaker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class SpeakerDetailActivity extends AppCompatActivity
        implements EventListener<DocumentSnapshot>, RatingDialogFragment.RatingListener {

    private static final String TAG = "SpeakerDetail";

    public static final String KEY_SPEAKER_ID = "key_speaker_id";

    @BindView(R.id.speaker_image)
    ImageView mImageView;

    @BindView(R.id.speaker_name)
    TextView mNameView;

    @BindView(R.id.speaker_speech)
    TextView mSpeechView;

    @BindView(R.id.speaker_job)
    TextView mJobView;

    @BindView(R.id.speaker_rating)
    MaterialRatingBar mRatingIndicator;

    @BindView(R.id.speaker_num_ratings)
    TextView mNumRatingsView;

    @BindView(R.id.speaker_city)
    TextView mCityView;

    @BindView(R.id.speaker_section)
    TextView mSectionView;

    @BindView(R.id.view_empty_ratings)
    ViewGroup mEmptyView;

    @BindView(R.id.recycler_ratings)
    RecyclerView mRatingsRecycler;

    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mSpeakerRef;
    private ListenerRegistration mSpeakerRegistration;

    private RatingAdapter mRatingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speaker_detail);
        ButterKnife.bind(this);

        // Get speaker ID from extras
        String speakerId = getIntent().getExtras().getString(KEY_SPEAKER_ID);
        if (speakerId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_SPEAKER_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the speaker
        mSpeakerRef = mFirestore.collection("speakers").document(speakerId);

        // Get ratings
        Query ratingsQuery = mSpeakerRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);
        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mRatingsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRatingsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mRatingsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRatingsRecycler.setAdapter(mRatingAdapter);

        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mSpeakerRegistration = mSpeakerRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mSpeakerRegistration != null) {
            mSpeakerRegistration.remove();
            mSpeakerRegistration = null;
        }
    }

    private Task<Void> setRating(final DocumentReference speakerRef, final DocumentReference ratingRef,
                                 final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                Speaker speaker = transaction.get(speakerRef)
                        .toObject(Speaker.class);
                int newNumRatings = speaker.getNumRatings() + 1;
                double oldRatingTotal = speaker.getAvgRating() *
                        speaker.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) /
                        newNumRatings;
                speaker.setNumRatings(newNumRatings);
                speaker.setAvgRating(newAvgRating);

                transaction.set(speakerRef, speaker);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    /**
     * Listener for the Speaker document ({@link #mSpeakerRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "speaker:onEvent", e);
            return;
        }

        onSpeakerLoaded(snapshot.toObject(Speaker.class));
    }

    private void onSpeakerLoaded(Speaker speaker) {
        mNameView.setText(speaker.getName());
        mSpeechView.setText(speaker.getSpeech());
        mJobView.setText(speaker.getJob());
        mRatingIndicator.setRating((float) speaker.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, speaker.getNumRatings()));
        mCityView.setText(speaker.getCity());
        mSectionView.setText(speaker.getSection());


        // Background image
        Glide.with(mImageView.getContext())
                .load(speaker.getPhoto())
                .into(mImageView);
    }

    @OnClick(R.id.speaker_button_back)
    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    @OnClick(R.id.fab_show_rating_dialog)
    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
    }

    @Override
    public void onRating(final Rating rating) {
        final OnSuccessListener<Void> successListener = new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Rating added");
                hideKeyboard();
                mRatingsRecycler.smoothScrollToPosition(0);
            }
        };
        final OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Add rating failed", e);
                hideKeyboard();
                Snackbar.make(findViewById(android.R.id.content), "Failed to add rating",
                        Snackbar.LENGTH_SHORT).show();
            }
        };
        mSpeakerRef.collection("ratings").
                whereEqualTo("userId", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                setRating(mSpeakerRef, mSpeakerRef.collection("ratings").document(), rating)
                                        .addOnSuccessListener(successListener)
                                        .addOnFailureListener(failureListener);
                            } else {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    setRating(mSpeakerRef, mSpeakerRef.collection("ratings").document(document.getId()), rating)
                                            .addOnSuccessListener(successListener)
                                            .addOnFailureListener(failureListener);
                                }
                            }
                        }
                    }
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
