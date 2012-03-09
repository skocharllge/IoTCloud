package cgl.iotcloud.core.stream;

import cgl.iotcloud.core.SCException;
import cgl.iotcloud.core.Sender;
import cgl.iotcloud.core.message.SensorMessage;
import cgl.iotcloud.core.message.data.StreamDataMessage;
import cgl.iotcloud.streaming.http.client.core.HttpCoreClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class StreamingSender implements Sender {
    private Logger log = LoggerFactory.getLogger(StreamingSender.class);

    /** HTTPClient used to send the data */
    private HttpCoreClient client = null;
    /** port to be used */
    private int port = 80;
    /** host name of the server */
    private String host;
    /** The path to be used */
    private String path;

    public StreamingSender(String path, String host, int port) {
        this.path = path;
        this.host = host;
        this.port = port;
    }

    public void start() {
    }

    public void stop() {
    }

    public String getState() {
        return null;
    }

    public void init() {
        client = new HttpCoreClient(host, port, path);
        try {
            client.init();
        } catch (IOReactorException e) {
            handleError("Error initializing the client", e);
        }
    }

    public void destroy() {
        // nothing to do for now
        try {
            client.destroy();
        } catch (IOException e) {
            handleError("Error while stopping the streaming client", e);
        }
    }

    public void send(SensorMessage msg) {
        if (!(msg instanceof StreamDataMessage)) {
            throw new IllegalArgumentException("Sensor message should be of type stream message");
        }

        StreamDataMessage message = (StreamDataMessage) msg;
        OutputStream outputStream = message.getOutputStream();

        if (outputStream == null) {
            handleError("Output stream cannot be null");
        }

        try {
            client.send(message.getInputStream(), new DefaultSendCallback());
        } catch (Exception e) {
            handleError("Error sending message to :" + "http://" + host + ":" + port + "/" + path);
        }
    }

    private class DefaultSendCallback implements HttpCoreClient.SendCallBack {
        public void completed() {
        }

        public void failed(Exception e) {
        }

        public void cancelled() {
        }
    }

    private void handleError(String msg) {
        log.error(msg);
        throw new SCException(msg);
    }

    private void handleError(String msg, IOException e) {
        log.error(msg, e);
        throw new SCException(msg, e);
    }
}
