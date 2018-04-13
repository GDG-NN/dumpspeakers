package ru.gdgnn.dumpspeakers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import ru.gdgnn.dumpspeakers.model.Speaker;
import com.google.firebase.firestore.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class FilterDialogFragment extends DialogFragment {

    public static final String TAG = "FilterDialog";

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private View mRootView;

    @BindView(R.id.spinner_section)
    Spinner mSectionSpinner;

    @BindView(R.id.spinner_city)
    Spinner mCitySpinner;

    @BindView(R.id.spinner_job)
    Spinner mJobSpinner;

    @BindView(R.id.spinner_sort)
    Spinner mSortSpinner;

    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filters, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.button_search)
    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    @OnClick(R.id.button_cancel)
    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedSection() {
        String selected = (String) mSectionSpinner.getSelectedItem();
        if (getString(R.string.value_any_section).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedJob() {
        String selected = (String) mJobSpinner.getSelectedItem();
        if (getString(R.string.value_any_job).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedCity() {
        String selected = (String) mCitySpinner.getSelectedItem();
        if (getString(R.string.value_any_city).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Speaker.FIELD_AVG_RATING;
        } if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Speaker.FIELD_POPULARITY;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        } if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mSectionSpinner.setSelection(0);
            mJobSpinner.setSelection(0);
            mCitySpinner.setSelection(0);
            mSortSpinner.setSelection(0);
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mRootView != null) {
            filters.setSection(getSelectedSection());
            filters.setJob(getSelectedJob());
            filters.setCity(getSelectedCity());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }
}
