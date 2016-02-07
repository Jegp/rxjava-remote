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

import org.nustaq.kontraktor.util.RateMeasure;
import org.reactivestreams.Publisher;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Subscriber;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        BidirectionalPublisherImpl<String, Integer> bidirectionalPublisher1 = new BidirectionalPublisherImpl<>(7777, 8888);
        BidirectionalPublisherImpl<Integer, String> bidirectionalPublisher2 = new BidirectionalPublisherImpl<>(8888, 7777);

        Publisher<Integer> intPublisher = RxReactiveStreams.toPublisher(Observable.range(0, 1_000_000));

        bidirectionalPublisher2.send(intPublisher);
        RateMeasure rm = new RateMeasure("events");

        RxReactiveStreams.toObservable(bidirectionalPublisher1.get(Integer.class)).forEach(i -> rm.count());
    }

}
