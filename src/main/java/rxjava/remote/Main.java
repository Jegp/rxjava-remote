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

import org.nustaq.kontraktor.Actor;
import org.nustaq.kontraktor.reactivestreams.KxPublisher;
import org.nustaq.kontraktor.reactivestreams.KxReactiveStreams;
import org.nustaq.kontraktor.remoting.tcp.TCPConnectable;
import org.nustaq.kontraktor.remoting.tcp.TCPNIOPublisher;
import org.nustaq.kontraktor.util.RateMeasure;
import org.reactivestreams.Publisher;
import rx.Observable;
import rx.RxReactiveStreams;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Observable<Integer> range = Observable.range(0, 1_000_000);
        Publisher<Integer> pub = RxReactiveStreams.toPublisher(range);

        KxReactiveStreams.get().asKxPublisher(pub)
                .serve(new TCPNIOPublisher().port(7777));

        RateMeasure rm = new RateMeasure("events");

        KxPublisher<Integer> remoteStream =
                KxReactiveStreams.get()
                        .connect(Integer.class, new TCPConnectable().port(7777).actorClass(Actor.class));

        RxReactiveStreams.toObservable(remoteStream)
                .forEach(i -> rm.count());
    }

}
