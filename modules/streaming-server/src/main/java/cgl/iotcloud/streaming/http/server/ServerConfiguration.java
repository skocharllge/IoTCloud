package cgl.iotcloud.streaming.http.server;

import io.netty.bootstrap.ClientBootstrap;
import io.netty.channel.socket.ClientSocketChannelFactory;
import io.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ServerConfiguration {
    private List<HttpServerEndpoint> serverEndpoints = new ArrayList<HttpServerEndpoint>();

    private List<HttpClientEndpoint> clientEndpoints = new ArrayList<HttpClientEndpoint>();

    private List<RoutingRule> routingRules = new ArrayList<RoutingRule>();

    private ClientBootstrap clientBootStrap = null;

    private ThreadPoolExecutor workerExecutor = null;

    public ServerConfiguration() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(), new CustomThreadFactory(new ThreadGroup("io"), "worker-thread"));
        ClientSocketChannelFactory socketFactory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(new CustomThreadFactory(new ThreadGroup("boss"), "boss-thread")),
//                Executors.newCachedThreadPool(new CustomThreadFactory(new ThreadGroup("io"), "io-thread")));
                executor);

        workerExecutor = new ThreadPoolExecutor(20, 100, 10, TimeUnit.SECONDS, new LinkedBlockingQueue(), new CustomThreadFactory(new ThreadGroup("io"), "server-io-thread"));

        clientBootStrap = new ClientBootstrap(socketFactory);
        clientBootStrap.setPipelineFactory(new ClientPipelineFactory());
    }

    public List<HttpServerEndpoint> getServerEndpoints() {
        return serverEndpoints;
    }

    public List<HttpClientEndpoint> getClientEndpoints() {
        return clientEndpoints;
    }

    public List<RoutingRule> getRoutingRules() {
        return routingRules;
    }

    public Executor getWorkerExecutor() {
        return workerExecutor;
    }

    public ClientBootstrap getClientBootStrap() {
        return clientBootStrap;
    }

    public void addServerEndpoint(HttpServerEndpoint endpoint) {
        serverEndpoints.add(endpoint);
    }

    public void addClientEndpoint(HttpClientEndpoint endpoint) {
        clientEndpoints.add(endpoint);
    }

    public void addRoutingRule(RoutingRule rule) {
        routingRules.add(rule);
    }

    public void addServerEndpoints(List<HttpServerEndpoint> serverEndpoints) {
        serverEndpoints.addAll(serverEndpoints);
    }

    public void addClientEndpoints(List<HttpClientEndpoint> clientEndpoints) {
        clientEndpoints.addAll(clientEndpoints);
    }
}
