package com.holdbetter.stonks.exercises;

import android.util.Log;
import android.view.View;
import android.widget.ListView;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;

public class Exercise {
    public static void main(String[] args) {
        createCountingObservable().subscribe(n -> System.out.println(n));
        createCountingObservable().subscribe(n -> System.out.println(n));
        createCountingObservable().subscribe(n -> System.out.println(n));
    }

    static Observable<Integer> createCountingObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            Integer number = 0;
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                emitter.onNext(++number);
            }
        });
    }

    Observable<View> itemClicks(ListView list) {
        return Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<View> emitter) throws Throwable {
                final View.OnClickListener listener = v -> Log.d("ACTION", "it was clicked");
                list.getSelectedView().setOnClickListener(listener);
                final int id = list.getSelectedItemPosition();

                emitter.setDisposable(Disposable.fromAction(() -> { list.getChildAt(id).setOnClickListener(null); }));
            }
        });
    }
}
