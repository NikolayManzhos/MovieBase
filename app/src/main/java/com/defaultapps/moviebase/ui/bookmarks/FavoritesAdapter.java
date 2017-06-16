package com.defaultapps.moviebase.ui.bookmarks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.defaultapps.moviebase.R;
import com.defaultapps.moviebase.data.models.firebase.Favorite;
import com.defaultapps.moviebase.di.ActivityContext;
import com.defaultapps.moviebase.di.scope.PerActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends FirebaseRecyclerAdapter<Favorite, FavoritesAdapter.FavoritesViewHolder> {

    private Context context;

    static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.favoritePoster)
        ImageView favoritePoster;

        FavoritesViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    @Inject
    public FavoritesAdapter(DatabaseReference dbReference, @ActivityContext Context context) {
        super(Favorite.class, R.layout.item_favorite, FavoritesViewHolder.class, dbReference);
        this.context = context;
    }

    @Override
    protected void populateViewHolder(FavoritesViewHolder viewHolder, Favorite model, int position) {
        Picasso
                .with(context)
                .load("https://image.tmdb.org/t/p/w300" + model.getPosterPath())
                .fit()
                .centerCrop()
                .into(viewHolder.favoritePoster);
    }
}