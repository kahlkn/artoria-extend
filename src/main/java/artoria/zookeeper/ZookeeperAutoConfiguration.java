package artoria.zookeeper;

import artoria.lock.LockUtils;
import artoria.lock.Locker;
import artoria.lock.ZkReentrantLocker;
import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import artoria.util.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import java.io.Closeable;

/**
 * Zookeeper auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass(name = {"org.apache.curator.framework.CuratorFramework"})
public class ZookeeperAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String CURATOR_FRAMEWORK_CLASS = "org.apache.curator.framework.CuratorFramework";
    private static final String ZK_URL_PREFIX = "zookeeper://";
    private static Logger log = LoggerFactory.getLogger(ZookeeperAutoConfiguration.class);
    private Closeable curator;

    @Value("${zookeeper.url:null}")
    private String zkUrl = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (!ClassUtils.isPresent(CURATOR_FRAMEWORK_CLASS, classLoader)) {
            log.info("Can not find \"" + CURATOR_FRAMEWORK_CLASS + "\", so skip it. ");
            return;
        }
        this.handleZookeeperUrl();
    }

    @Override
    public void destroy() throws Exception {
        if (this.curator != null) {
            this.curator.close();
            log.info("Release curator object \"{}\" success. ", this.curator);
        }
    }

    /**
     * Handle key "zookeeper.url" in configuration file.
     * @return Judge curator object initialize success
     */
    private boolean handleZookeeperUrl() {
        if (StringUtils.isNotBlank(this.zkUrl)) {
            log.info("Find zookeeper url \"{}\". ", this.zkUrl);
            String url = this.zkUrl;
            if (url.startsWith(ZK_URL_PREFIX)) {
                url = url.substring(ZK_URL_PREFIX.length());
            }
            try {
                this.curator = CuratorFrameworkFactory.builder()
                        .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                        .sessionTimeoutMs(30000)
                        .connectionTimeoutMs(30000)
                        .connectString(url)
                        .build();
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
                return false;
            }
            Locker locker = new ZkReentrantLocker((CuratorFramework) this.curator);
            LockUtils.setLocker(locker);
            return true;
        }
        log.info("Can not find zookeeper url by key \"zookeeper.url\". ");
        return false;
    }

}
