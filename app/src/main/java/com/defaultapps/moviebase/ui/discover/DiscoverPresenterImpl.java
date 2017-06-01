package com.defaultapps.moviebase.ui.discover;

import com.defaultapps.moviebase.data.interactor.DiscoverUseCaseImpl;
import com.defaultapps.moviebase.di.scope.PerActivity;
import com.defaultapps.moviebase.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

@PerActivity
public class DiscoverPresenterImpl extends BasePresenter<DiscoverContract.DiscoverView> implements DiscoverContract.DiscoverPresenter {

    private DiscoverUseCaseImpl discoverUseCase;

    @Inject
    public DiscoverPresenterImpl(DiscoverUseCaseImpl discoverUseCase) {
        this.discoverUseCase = discoverUseCase;
    }

    @Override
    public void requestData() {
        getCompositeDisposable().add(
                discoverUseCase.provideGenresList()
                .subscribe(
                        genres -> {
                            if (getView() != null) getView().showData(genres);
                        },
                        err -> {}
                )
        );
    }
}