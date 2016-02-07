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
import org.nustaq.kontraktor.reactivestreams.KxReactiveStreams;
import org.nustaq.kontraktor.remoting.tcp.TCPConnectable;
import org.nustaq.kontraktor.remoting.tcp.TCPNIOPublisher;
import org.reactivestreams.Publisher;

public class BidirectionalPublisherImpl<S, R> implements BidirectionalPublisher<S, R> {

    private final int localPort;
    private final int remotePort;

    public BidirectionalPublisherImpl(int localPort, int remotePort) {
        this.localPort = localPort;
        this.remotePort = remotePort;
    }

    @Override
    public Publisher<R> get(Class<R> type) {
        return KxReactiveStreams.get().connect(type, new TCPConnectable().port(remotePort).actorClass(Actor.class));
    }

    @Override
    public void send(Publisher<S> publisher) {
        KxReactiveStreams.get().asKxPublisher(publisher).serve(new TCPNIOPublisher().port(localPort));
    }
}
