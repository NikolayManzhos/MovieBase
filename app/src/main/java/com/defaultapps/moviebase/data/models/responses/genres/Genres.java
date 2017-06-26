package com.defaultapps.moviebase.data.models.responses.genres;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Genres {
    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

}