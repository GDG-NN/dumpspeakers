package ru.gdgnn.dumpspeakers;

import android.content.Context;
import android.text.TextUtils;

import ru.gdgnn.dumpspeakers.model.Speaker;

import com.google.firebase.firestore.Query;


public class Filters {

    private String section = null;
    private String job = null;
    private String city = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(Speaker.FIELD_AVG_RATING);
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasSection() {
        return !(TextUtils.isEmpty(section));
    }

    public boolean hasCity() {
        return !(TextUtils.isEmpty(city));
    }

    public boolean hasJob() {
        return !(TextUtils.isEmpty(job));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (section == null && city == null && job == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_speakers));
            desc.append("</b>");
        }

        if (section != null) {
            desc.append("<b> ");
            desc.append(section);
            desc.append("</b>");
        }

        if (city != null) {
            desc.append("<b> ");
            desc.append(city);
            desc.append("</b>");
        }

        if (job != null) {
            desc.append("<b> ");
            desc.append(job);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
       if (Speaker.FIELD_POPULARITY.equals(sortBy)) {
            return context.getString(R.string.sorted_by_popularity);
        } else {
            return context.getString(R.string.sorted_by_rating);
        }
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
