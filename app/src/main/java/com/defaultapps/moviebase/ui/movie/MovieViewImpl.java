package com.defaultapps.moviebase.ui.movie;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defaultapps.moviebase.R;
import com.defaultapps.moviebase.data.models.responses.movie.Cast;
import com.defaultapps.moviebase.data.models.responses.movie.Crew;
import com.defaultapps.moviebase.data.models.responses.movie.MovieDetailResponse;
import com.defaultapps.moviebase.data.models.responses.movie.VideoResult;
import com.defaultapps.moviebase.data.models.responses.movies.Result;
import com.defaultapps.moviebase.ui.base.BaseFragment;
import com.defaultapps.moviebase.ui.movie.MovieContract.MoviePresenter;
import com.defaultapps.moviebase.ui.movie.adapter.CastAdapter;
import com.defaultapps.moviebase.ui.movie.adapter.CrewAdapter;
import com.defaultapps.moviebase.ui.movie.adapter.SimilarAdapter;
import com.defaultapps.moviebase.ui.movie.adapter.VideosAdapter;
import com.defaultapps.moviebase.utils.AppConstants;
import com.defaultapps.moviebase.utils.SimpleItemDecorator;
import com.defaultapps.moviebase.utils.Utils;
import com.defaultapps.moviebase.utils.ViewUtils;
import com.defaultapps.moviebase.utils.listener.OnMovieClickListener;
import com.defaultapps.moviebase.utils.listener.OnPersonClickListener;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import easybind.Layout;
import easybind.bindings.BindNavigator;
import easybind.bindings.BindPresenter;

import static com.defaultapps.moviebase.utils.AppConstants.EMPTY;
import static com.defaultapps.moviebase.utils.AppConstants.MOVIE_ID;

@Layout(id = R.layout.fragment_movie, name = "Movie")
public class MovieViewImpl extends BaseFragment
        implements MovieContract.MovieView, OnMovieClickListener,
        VideosAdapter.OnVideoClickListener, OnPersonClickListener {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.errorTextView)
    TextView errorText;

    @BindView(R.id.errorButton)
    Button errorButton;

    @BindView(R.id.toolbarContainer)
    AppBarLayout toolbarContainer;

    @BindView(R.id.mainContent)
    NestedScrollView nestedScrollView;

    @BindView(R.id.contentContainer)
    RelativeLayout contentContainer;

    @BindView(R.id.image)
    ImageView imageBackdrop;

    @BindView(R.id.imagePoster)
    ImageView imagePoster;

    @BindView(R.id.movieTitle)
    TextView movieTitle;

    @BindView(R.id.releaseDate)
    IconTextView releaseDate;

    @BindView(R.id.runtime)
    IconTextView runtime;

    @BindView(R.id.rating)
    IconTextView rating;

    @BindView(R.id.budget)
    TextView budget;

    @BindView(R.id.revenue)
    TextView revenue;

    @BindView(R.id.movieOverview)
    ExpandableTextView movieOverview;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.favoriteFab)
    FloatingActionButton favoriteFab;

    @BindView(R.id.videos)
    TextView videosTitle;

    @BindView(R.id.videosRecyclerView)
    RecyclerView videosRecyclerView;

    @BindView(R.id.cast)
    TextView castTitle;

    @BindView(R.id.castRecyclerView)
    RecyclerView castRecyclerView;

    @BindView(R.id.crew)
    TextView crewTitle;

    @BindView(R.id.crewRecyclerView)
    RecyclerView crewRecyclerView;

    @BindView(R.id.similar)
    TextView similarTitle;

    @BindView(R.id.similarRecyclerView)
    RecyclerView similarRecyclerView;

    @BindPresenter
    @Inject
    MoviePresenter presenter;

    @Inject
    VideosAdapter videosAdapter;

    @Inject
    CastAdapter castAdapter;

    @Inject
    CrewAdapter crewAdapter;

    @Inject
    SimilarAdapter similarAdapter;

    @Inject
    ViewUtils viewUtils;

    @BindNavigator
    @Inject
    MovieContract.MovieNavigator navigator;

    Menu toolbarMenu;

    private int movieId;
    private MovieDetailResponse movieInfo;

    public static MovieViewImpl newInstance(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID, movieId);
        MovieViewImpl fragment = new MovieViewImpl();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void inject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar();
        initFAB();
        initRecyclerViews();
        initViews();

        Utils.checkNotNull(getArguments());
        movieId = getArguments().getInt(AppConstants.MOVIE_ID);
        presenter.requestMovieInfo(movieId, false);
        presenter.requestFavoriteStatus(movieId);
    }

    private void initViews() {
        movieTitle.setSelected(true);
    }

    @OnClick(R.id.errorButton)
    void onErrorClick() {
        presenter.requestMovieInfo(movieId, true);
    }

    @OnClick(R.id.favoriteFab)
    void onFavoriteClick() {
        presenter.addOrRemoveFromFavorites(movieInfo.getId(), movieInfo.getPosterPath());
    }

    @Override
    public void onMovieClick(int movieId) {
        navigator.toMovieActivity(movieId);
    }

    @Override
    public void onVideoClick(String videoPath) {
        navigator.toFullScreenVideoActivity(videoPath);
    }

    @Override
    public void onPersonClick(int personId) {
        navigator.toPersonActivity(personId);
    }

    @Override
    public void displayTransactionError() {
        viewUtils.showSnackbar(nestedScrollView, getString(R.string.movie_favorite_failure));
    }

    @Override
    public void displayMovieInfo(MovieDetailResponse movieInfo) {
        this.movieInfo = movieInfo;
        loadImage(movieInfo.getBackdropPath(), imageBackdrop);
        loadImage(movieInfo.getPosterPath(), imagePoster);
        toolbarMenu.findItem(R.id.share).setOnMenuItemClickListener(it -> {
            navigator.shareAction(movieInfo.getHomepage());
            return true;
        });
        movieTitle.setText(movieInfo.getTitle());
        releaseDate.append(" " + Utils.convertDate(movieInfo.getReleaseDate()));
        runtime.append(" " + Utils.formatMinutes(getContext(), movieInfo.getRuntime()));
        rating.append(" " + movieInfo.getVoteAverage());
        String budgetString = movieInfo.getBudget() == 0 ?
                getString(R.string.movie_budget_unknown, AppConstants.UNKNOWN) :
                getString(R.string.movie_budget, Utils.formatNumber(movieInfo.getBudget()));
        budget.setText(budgetString);
        String revenueString = movieInfo.getRevenue() == 0 ?
                getString(R.string.movie_revenue_unknown, AppConstants.UNKNOWN) :
                getString(R.string.movie_revenue, Utils.formatNumber(movieInfo.getRevenue()));
        revenue.setText(revenueString);
        movieOverview.setText(movieInfo.getOverview());
        List<VideoResult> videos = movieInfo.getVideos().getVideoResults();
        toggleTitleVisibility(videosTitle, videos.size() == EMPTY);
        videosAdapter.setData(movieInfo.getVideos().getVideoResults());

        List<Cast> castPeople = movieInfo.getCredits().getCast();
        toggleTitleVisibility(castTitle, castPeople.size() == EMPTY);
        castAdapter.setData(castPeople);

        List<Crew> crewPeople = movieInfo.getCredits().getCrew();
        toggleTitleVisibility(crewTitle, crewPeople.size() == EMPTY);
        crewAdapter.setData(crewPeople);

        List<Result> similarMovies = movieInfo.getSimilar().getResults();
        toggleTitleVisibility(similarTitle, similarMovies.size() == EMPTY);
        similarAdapter.setData(movieInfo.getSimilar().getResults());
    }

    private void toggleTitleVisibility(TextView title, boolean isEmpty) {
        if (isEmpty) {
            title.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideData() {
        toolbarContainer.setVisibility(View.GONE);
        contentContainer.setVisibility(View.GONE);
        favoriteFab.setVisibility(View.GONE);
    }

    @Override
    public void showData() {
        toolbarContainer.setVisibility(View.VISIBLE);
        contentContainer.setVisibility(View.VISIBLE);
        favoriteFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        errorText.setVisibility(View.GONE);
        errorButton.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        errorText.setVisibility(View.VISIBLE);
        errorButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void setFabStatus(boolean isActive) {
        favoriteFab.setImageDrawable(new IconDrawable(getContext(),
                (isActive ? MaterialIcons.md_favorite : MaterialIcons.md_favorite_border))
                .colorRes(R.color.colorAccent)
        );
    }

    @Override
    public void displayLoginScreen() {
        navigator.toLoginActivity();
    }

    private void initFAB() {
        favoriteFab.setImageDrawable(
                new IconDrawable(getContext(), MaterialIcons.md_favorite_border)
                .colorRes(R.color.colorAccent));
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > oldScrollY) {
                favoriteFab.hide();
            } else {
                favoriteFab.show();
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void initToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_inverted);
        toolbar.setNavigationOnClickListener(view -> navigator.finishActivity());
        toolbar.inflateMenu(R.menu.movie_toolbar);
        toolbarMenu = toolbar.getMenu();
        toolbarContainer.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(movieInfo.getTitle());
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initRecyclerViews() {
        SimpleItemDecorator horizontalDivider = new SimpleItemDecorator(30, true);
        videosRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        videosRecyclerView.setAdapter(videosAdapter);
        videosRecyclerView.addItemDecoration(horizontalDivider);
        videosRecyclerView.setNestedScrollingEnabled(false);
        videosAdapter.setOnVideoClickListener(this);

        castRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        castRecyclerView.setAdapter(castAdapter);
        castRecyclerView.addItemDecoration(horizontalDivider);
        castRecyclerView.setNestedScrollingEnabled(false);
        castAdapter.setOnPersonClickListener(this);

        crewRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        crewRecyclerView.setAdapter(crewAdapter);
        crewRecyclerView.addItemDecoration(horizontalDivider);
        crewRecyclerView.setNestedScrollingEnabled(false);
        crewAdapter.setOnPersonClickListener(this);

        similarRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        similarRecyclerView.setAdapter(similarAdapter);
        similarRecyclerView.addItemDecoration(horizontalDivider);
        similarRecyclerView.setNestedScrollingEnabled(false);
        similarAdapter.setOnMovieClickListener(this);
    }

    private void loadImage(String uri, ImageView iv) {
        Picasso
                .with(getContext())
                .load("http://image.tmdb.org/t/p//w1280" + uri)
                .fit()
                .centerCrop()
                .into(iv);
    }
}
