package com.example.redux;

/**
 * Created by kang on 17-3-13.
 */
public interface Middleware<S> {
    IDispatcher create(Store<S> store, IDispatcher nextDispatcher);
}
