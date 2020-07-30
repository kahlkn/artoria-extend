package artoria.zookeeper;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * Zookeeper simple connection state listener.
 * @author Kahle
 */
public class ZkConnectionStateListener implements ConnectionStateListener {
    public static Logger log = LoggerFactory.getLogger(ZkConnectionStateListener.class);
    private String listenerName;

    public ZkConnectionStateListener(String listenerName) {
        Assert.notBlank(listenerName, "Parameter \"listenerName\" must not blank. ");
        this.listenerName = listenerName;
    }

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState state) {
        if (state == ConnectionState.LOST) {
            log.debug(this.listenerName + " : A client lost session with zookeeper. ");
        }
        else if (state == ConnectionState.CONNECTED) {
            log.debug(this.listenerName + " : A client connected with zookeeper. ");
        }
        else if (state == ConnectionState.RECONNECTED) {
            log.debug(this.listenerName + " : A client reconnected with zookeeper. ");
        }
    }

}
