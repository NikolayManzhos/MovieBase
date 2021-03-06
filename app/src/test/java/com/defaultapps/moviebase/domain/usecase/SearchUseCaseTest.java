package com.defaultapps.moviebase.domain.usecase;


import com.defaultapps.moviebase.data.models.responses.movies.MoviesResponse;
import com.defaultapps.moviebase.domain.repository.ApiRepository;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

public class SearchUseCaseTest {

    @Mock
    private ApiRepository apiRepository;

    private SearchUseCase searchUseCase;

    private MoviesResponse actualResponse;
    private Throwable actualException;
    private static final String QUERY = "Titanic";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        searchUseCase = new SearchUseCaseImpl(apiRepository);
    }

    @Test
    public void requestSearchResultsSuccess() throws Exception {
        MoviesResponse expectedResponse = random(MoviesResponse.class);
        Single<MoviesResponse> single = Single.just(expectedResponse);

        when(apiRepository.requestSearchResults(QUERY, 1)).thenReturn(single);

        searchUseCase.requestSearchResults(QUERY, false).subscribe(
                actualResponse -> this.actualResponse = actualResponse,
                System.out::print
        );

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void requestSearchResultFailure() throws Exception {
        Throwable expectedException = new Throwable("Network error.");
        Single<MoviesResponse> single = Single.error(expectedException);

        when(apiRepository.requestSearchResults(QUERY, 1)).thenReturn(single);

        searchUseCase.requestSearchResults(QUERY, false).subscribe(
                actualResponse -> {},
                err -> actualException = err
        );
        assertNotNull(actualException);
        assertEquals(expectedException, actualException);
    }

    @Test
    public void requestMoreSearchResultsSuccess() throws Exception {
        MoviesResponse response = random(MoviesResponse.class);
        changeReplaySubject(response);

        MoviesResponse loadMoreResponse = random(MoviesResponse.class);
        Single<MoviesResponse> single = Single.just(loadMoreResponse);

        when(apiRepository.requestSearchResults(eq(QUERY), anyInt()))
                .thenReturn(single);

        searchUseCase.requestMoreSearchResults(QUERY)
                .subscribe(moviesResponse -> assertEquals(loadMoreResponse.getPage(), moviesResponse.getPage()));

        //If previous call still ongoing it should be disposed and new one fired.
        searchUseCase.requestMoreSearchResults(QUERY)
                .subscribe();
    }

    @Test
    public void requestMoreSearchResultsFailure() throws Exception {
        changeReplaySubject(random(MoviesResponse.class));

        Throwable throwable = new Throwable("Exception");
        Single<MoviesResponse> single = Single.error(throwable);

        when(apiRepository.requestSearchResults(eq(QUERY), anyInt()))
                .thenReturn(single);

        searchUseCase.requestMoreSearchResults(QUERY)
                .subscribe(response -> {}, Assert::assertNotNull);
    }

    private void changeReplaySubject(MoviesResponse response) throws Exception {
        Field field = SearchUseCaseImpl.class.getDeclaredField("behaviorSubject");
        field.setAccessible(true);
        field.set(searchUseCase, BehaviorSubject.create());
        //noinspection unchecked
        ((BehaviorSubject<MoviesResponse>) field.get(searchUseCase)).onNext(response);
    }
}
