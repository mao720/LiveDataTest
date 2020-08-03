package top.lifegame.livedatatest;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class CleanLiveData<T> extends LiveData<T> {
    private boolean hasModified = false;

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        super.observe(owner, new Observer<T>() {
            private boolean hasIntercept = false;

            @Override
            public void onChanged(T t) {
                if (!hasModified || hasIntercept) {
                    observer.onChanged(t);
                }
                hasIntercept = true;
            }
        });
    }

    @Override
    public void observeForever(@NonNull final Observer<? super T> observer) {
        super.observeForever(new Observer<T>() {
            private boolean hasIntercept = false;

            @Override
            public void onChanged(T t) {
                if (!hasModified || hasIntercept) {
                    observer.onChanged(t);
                }
                hasIntercept = true;
            }
        });
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        hasModified = true;
    }

    @Override
    public void postValue(T value) {
        super.postValue(value);
        hasModified = true;
    }
}
