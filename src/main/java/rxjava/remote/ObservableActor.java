/*
 * Copyright 2016 Jens Egholm Pedersen
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package rxjava.remote;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import rx.Observable;
import rx.Subscription;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * A sender of observables to a remote server.
 * <p>
 * Thanks to <a href="https://dzone.com/articles/creating-rxjava-observable"
 * title="Creating RxJava Observables from an Akka actor">Joachim Hofer on DZone</a>
 *
 * @param <T> The type of elements to observe.
 */
public class ObservableActor<T> extends AbstractActor {

    private final Class<T> classType;

    public ObservableActor(Class<T> classType) {
        this.classType = classType;
        receive(ReceiveBuilder
                .match(Subscribe.class, instance -> {
                    context().become(subscribed(instance::onNext));
                })
                .build());
    }

    public PartialFunction<Object, BoxedUnit> subscribed(Consumer<T> onNext) {
        return ReceiveBuilder
                .match(Unsubscribe.class, instance -> {
                    System.out.println("Unsubscribing");
                    context().become(receive());
                })
                .match(classType, message -> {
                    onNext.accept((T) message);
                }).build();
    }

    public static <T> Observable<T> fromActor(ActorRef actor) {
        return Observable.create(observer -> {
            actor.tell((Subscribe<T>) observer::onNext, null);
            observer.add(new Subscription() {
                private final AtomicBoolean isUnsubscribed = new AtomicBoolean(false);

                @Override
                public void unsubscribe() {
                    actor.tell(Unsubscribe.INSTANCE, null);
                    isUnsubscribed.set(true);
                }

                @Override
                public boolean isUnsubscribed() {
                    return isUnsubscribed.get();
                }
            });
        });
    }

    @FunctionalInterface
    private interface Subscribe<T> {
        void onNext(T content);
    }

    private enum Unsubscribe {
        INSTANCE;
    }

}
