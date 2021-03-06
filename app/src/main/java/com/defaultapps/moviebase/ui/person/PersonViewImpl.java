package com.defaultapps.moviebase.ui.person;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.defaultapps.moviebase.R;
import com.defaultapps.moviebase.data.models.responses.person.Cast;
import com.defaultapps.moviebase.data.models.responses.person.Crew;
import com.defaultapps.moviebase.data.models.responses.person.PersonResponse;
import com.defaultapps.moviebase.di.FragmentContext;
import com.defaultapps.moviebase.ui.base.BaseFragment;
import com.defaultapps.moviebase.ui.base.Navigator;
import com.defaultapps.moviebase.ui.person.PersonContract.PersonPresenter;
import com.defaultapps.moviebase.ui.person.adapter.CreditsCastAdapter;
import com.defaultapps.moviebase.ui.person.adapter.CreditsCrewAdapter;
import com.defaultapps.moviebase.utils.AppConstants;
import com.defaultapps.moviebase.utils.SimpleItemDecorator;
import com.defaultapps.moviebase.utils.listener.OnMovieClickListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import easybind.Layout;
import easybind.bindings.BindNavigator;
import easybind.bindings.BindPresenter;

@Layout(id = R.layout.fragment_person, name = "Person")
public class PersonViewImpl extends BaseFragment implements PersonContract.PersonView, OnMovieClickListener {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.errorTextView)
    TextView errorText;

    @BindView(R.id.personPortrait)
    CircleImageView circleImageView;

    @BindView(R.id.personBiography)
    ExpandableTextView staffBiographyView;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitleView;

    @BindView(R.id.contentContainer)
    LinearLayout contentContainer;

    @BindView(R.id.castCreditsRecyclerView)
    RecyclerView castRecyclerView;

    @BindView(R.id.crewCreditsRecyclerView)
    RecyclerView crewRecyclerView;

    @BindView(R.id.errorButton)
    Button errorButton;

    @BindView(R.id.castSubtitle)
    TextView castSubtitle;

    @BindView(R.id.crewSubtitle)
    TextView crewSubtitle;

    @BindPresenter
    @Inject
    PersonPresenter presenter;

    @Inject
    CreditsCastAdapter castAdapter;

    @Inject
    CreditsCrewAdapter crewAdapter;

    @BindNavigator
    @FragmentContext
    @Inject
    Navigator navigator;


    public static PersonViewImpl createInstance(int staffId) {
        PersonViewImpl staffView = new PersonViewImpl();
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.PERSON_ID, staffId);
        staffView.setArguments(bundle);
        return staffView;
    }

    @Override
    protected void inject() {
        getFragmentComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerViews();

        int personId = getArguments().getInt(AppConstants.PERSON_ID);
        if (savedInstanceState == null) {
            presenter.requestPersonInfo(personId, true);
        } else {
            presenter.requestPersonInfo(personId, false);
        }
    }

    @OnClick(R.id.backButton)
    void onBackClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onMovieClick(int movieId) {
        navigator.toMovieActivity(movieId);
    }

    @Override
    public void displayStaffInfo(PersonResponse personResponse) {
        Picasso
                .with(getContext())
                .load("https://image.tmdb.org/t/p/w300" + personResponse.getProfilePath())
                .placeholder(R.drawable.placeholder_human)
                .into(circleImageView);
        String biography = personResponse.getBiography() != null && !personResponse.getBiography().isEmpty() ?
                personResponse.getBiography() : getString(R.string.person_biography_empty);
        staffBiographyView.setText(biography);
        toolbarTitleView.setText(personResponse.getName());

        List<Cast> castList = personResponse.getMovieCredits().getCast();
        List<Crew> crewList = personResponse.getMovieCredits().getCrew();
        int castVisibility = castList.size() == 0 ? View.GONE : View.VISIBLE;
        int crewVisibility = crewList.size() == 0 ? View.GONE : View.VISIBLE;
        castAdapter.setData(personResponse.getMovieCredits().getCast());
        crewAdapter.setData(personResponse.getMovieCredits().getCrew());
        castSubtitle.setVisibility(castVisibility);
        crewSubtitle.setVisibility(crewVisibility);
    }

    @Override
    public void hideData() {
        contentContainer.setVisibility(View.GONE);
    }

    @Override
    public void showData() {
        contentContainer.setVisibility(View.VISIBLE);
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
    public void hideError() {
        errorText.setVisibility(View.GONE);
        errorButton.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        errorText.setVisibility(View.VISIBLE);
        errorButton.setVisibility(View.VISIBLE);
    }

    public void initRecyclerViews() {
        SimpleItemDecorator simpleItemDecorator = new SimpleItemDecorator(30, true);
        castRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        castRecyclerView.setAdapter(castAdapter);
        castRecyclerView.setNestedScrollingEnabled(false);
        castRecyclerView.addItemDecoration(simpleItemDecorator);
        castAdapter.setOnMovieClickListener(this);

        crewRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        crewRecyclerView.setAdapter(crewAdapter);
        crewRecyclerView.setNestedScrollingEnabled(false);
        crewRecyclerView.addItemDecoration(simpleItemDecorator);
        crewAdapter.setOnMovieClickListener(this);
    }
}
