package com.example.redux;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 17-3-11.
 */
public abstract class Store<S> implements IDispatcher {

    public abstract S getState();

    public abstract void dispatch(Action action);

    public abstract Subscription subscribe(Subscriber subscriber);

    public abstract void unSubscribe(Subscriber subscriber);

    public static <S> Store<S> create(S currentState, IReducer<S> reducer, Middleware<S>... middlewares) {
        return new CoreStore<S>(currentState, reducer, middlewares);
    }
    public static <S> Store<S> create(S currentState, IReducer<S> reducer) {
        return new CoreStore<S>(currentState, reducer, null);
    }

    private static class CoreStore<S> extends Store<S> {
        private List<Subscriber> subscribers = new ArrayList<>();
        private IReducer<S> reducer;
        private S currentState;
        private IDispatcher mDispatcher = new IDispatcher() {
            @Override
            public void dispatch(Action action) {
                currentState = reducer.reduce(currentState, action);

                for (Subscriber item : subscribers) {
                    item.onStateUpdate();
                }
            }
        };

        private CoreStore(S currentState, IReducer<S> reducer, Middleware<S> ... middlewareArray) {
            this.reducer = reducer;
            this.currentState = currentState;

            if (middlewareArray == null){
                return;
            }

            IDispatcher dispatcher = mDispatcher;
            for (int i = middlewareArray.length-1; i >= 0; --i) {
                dispatcher = middlewareArray[i].create(this, dispatcher);
            }
            mDispatcher = dispatcher;
        }

        @Override
        public S getState() {
            return currentState;
        }

        @Override
        public void dispatch(Action action) {
            mDispatcher.dispatch(action);
        }



        @Override
        public Subscription subscribe(final Subscriber subscriber) {
            subscribers.add(subscriber);
            return new Subscription() {
                @Override
                public void unSubscribe() {
                    subscribers.remove(subscriber);
                }
            };
        }

        @Override
        public void unSubscribe(Subscriber subscriber) {
            subscribers.remove(subscriber);
        }
    }

}
