package com.defaultapps.moviebase.ui.person.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.defaultapps.moviebase.R;
import com.defaultapps.moviebase.data.models.responses.person.Crew;
import com.defaultapps.moviebase.di.ActivityContext;
import com.defaultapps.moviebase.di.scope.PerFragment;
import com.defaultapps.moviebase.ui.person.CastCrewViewHolder;
import com.defaultapps.moviebase.utils.AppConstants;
import com.defaultapps.moviebase.utils.listener.OnMovieClickListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@PerFragment
public class CreditsCrewAdapter extends RecyclerView.Adapter<CastCrewViewHolder> {

    private final Context context;
    private List<Crew> crewCredits;
    private OnMovieClickListener listener;

    @Inject
    CreditsCrewAdapter(@ActivityContext Context context) {
        this.context = context;
        crewCredits = new ArrayList<>();
    }

    @Override
    public CastCrewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CastCrewViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cast_crew, parent, false));
    }

    @Override
    public void onBindViewHolder(CastCrewViewHolder holder, int position) {
        int aPosition = holder.getAdapterPosition();
        Crew crew = crewCredits.get(aPosition);
        holder.personJob.setText(crew.getJob());
        holder.itemView.setOnClickListener(view -> listener.onMovieClick(crew.getId()));
        Picasso
                .with(context)
                .load(AppConstants.POSTER_BASE_URL + crewCredits.get(aPosition).getPosterPath())
                .into(holder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return crewCredits.size();
    }

    public void setData(List<Crew> crewCredits) {
        this.crewCredits.clear();
        this.crewCredits.addAll(crewCredits);
        notifyDataSetChanged();
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }
}
