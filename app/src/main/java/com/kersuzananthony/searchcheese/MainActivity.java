package com.kersuzananthony.searchcheese;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mEditText;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private CheeseAdapter mCheeseAdapter;
    private CheeseSearchEngine mCheeseSearchEngine;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCheeseSearchEngine = new CheeseSearchEngine(Arrays.asList(getResources().getStringArray(R.array.cheeses)));

        mButton = (Button) findViewById(R.id.searchButton);
        mEditText = (EditText) findViewById(R.id.queryEditText);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.list);

        mCheeseAdapter = new CheeseAdapter();
        mRecyclerView.setAdapter(mCheeseAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Observable<String> buttonClickStream = createButtonClickObservable();
        final Observable<String> editTextChangeStream = createTextChangeObservable();

        mDisposable = Observable.merge(buttonClickStream, editTextChangeStream)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            showProgressView();
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new Function<String, List<String>>() {
                        @Override
                        public List<String> apply(@NonNull String s) throws Exception {
                            return mCheeseSearchEngine.search(s);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<String>>() {
                        @Override
                        public void accept(List<String> result) throws Exception {
                            hideProgressView();
                            showResult(result);
                        }
                    });
    }

    @Override
    protected void onStop() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        super.onStop();
    }

    protected void showProgressView() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressView() {
        mProgressBar.setVisibility(View.GONE);
    }

    protected void showResult(List<String> result) {
        if (result.isEmpty()) {
            Toast.makeText(this, R.string.nothing_found, Toast.LENGTH_LONG).show();
        }

        mCheeseAdapter.setCheeses(result);
    }

    protected Observable<String> createButtonClickObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> emitter) throws Exception {
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emitter.onNext(mEditText.getText().toString());
                    }
                });

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        mButton.setOnClickListener(null);
                    }
                });
            }
        });
    }

    protected Observable<String> createTextChangeObservable() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> emitter) throws Exception {
                final TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence == null) return;
                        emitter.onNext(charSequence.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                };

                mEditText.addTextChangedListener(textWatcher);

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        mEditText.removeTextChangedListener(textWatcher);
                    }
                });
            }
        });

        return observable.filter(new Predicate<String>() {
            @Override
            public boolean test(@NonNull String s) throws Exception {
                return s.length() >= 2;
            }
        }).debounce(1000, TimeUnit.MILLISECONDS);
    }
}
