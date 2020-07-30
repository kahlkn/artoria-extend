package artoria.property;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static artoria.common.Constants.ONE_HUNDRED;

public class SimplePropertyProvider extends AbstractPropertyProvider {
    private static Logger log = LoggerFactory.getLogger(SimplePropertyProvider.class);
    private Map<String, List<String>> groupNameListMap;
    private Map<String, List<String>> nameNameListMap;
    private Map<String, Object> nameValueMap;

    public SimplePropertyProvider() {
        this.groupNameListMap = new ConcurrentHashMap<String, List<String>>();
        this.nameNameListMap = new ConcurrentHashMap<String, List<String>>();
        this.nameValueMap = new ConcurrentHashMap<String, Object>();
    }

    @Override
    public boolean containsProperty(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        return nameValueMap.containsKey(name);
    }

    @Override
    public Map<String, Object> getProperties(String group) {
        if (StringUtils.isBlank(group)) {
            return Collections.emptyMap();
        }
        List<String> list = groupNameListMap.get(group);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = new HashMap<String, Object>(list.size());
        for (String name : list) {
            result.put(name, nameValueMap.get(name));
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        if (StringUtils.isBlank(name)) { return null; }
        Object value = nameValueMap.get(name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Object removeProperty(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        List<String> list = nameNameListMap.get(name);
        list.remove(name);
        nameNameListMap.remove(name);
        return nameValueMap.remove(name);
    }

    @Override
    public Object setProperty(String group, String name, Object value) {
        Assert.notBlank(group, "Parameter \"group\" must not blank. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Assert.notNull(value, "Parameter \"value\" must not null. ");
        List<String> list = groupNameListMap.get(group);
        if (list == null) {
            list = new CopyOnWriteArrayList<String>();
            groupNameListMap.put(group, list);
        }
        if (!list.contains(name)) {
            list.add(name);
        }
        nameNameListMap.put(name, list);
        return nameValueMap.put(name, value);
    }

    @Override
    public void reload(Map<String, Map<String, Object>> data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        Map<String, List<String>> newGroupNameListMap = new HashMap<String, List<String>>(data.size());
        Map<String, List<String>> newNameNameListMap = new HashMap<String, List<String>>(ONE_HUNDRED);
        Map<String, Object> newNameValueMap = new HashMap<String, Object>(ONE_HUNDRED);
        for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
            Map<String, Object> properties = entry.getValue();
            String group = entry.getKey();
            if (MapUtils.isEmpty(properties)) { continue; }
            if (StringUtils.isBlank(group)) { continue; }
            List<String> list = newGroupNameListMap.get(group);
            if (list == null) {
                list = new CopyOnWriteArrayList<String>();
                newGroupNameListMap.put(group, list);
            }
            newNameValueMap.putAll(properties);
            for (Map.Entry<String, Object> objectEntry : properties.entrySet()) {
                String name = objectEntry.getKey();
                if (StringUtils.isBlank(name)) { continue; }
                list.add(name);
                newNameNameListMap.put(name, list);
            }
        }
        nameValueMap.clear();
        nameValueMap.putAll(newNameValueMap);
        groupNameListMap.clear();
        groupNameListMap.putAll(newGroupNameListMap);
        nameNameListMap.clear();
        nameNameListMap.putAll(newNameNameListMap);
        if (PropertyUtils.getBooleanProperty(DISPLAY_RELOAD_LOG)) {
            log.info("The simple property provider reload data success. ");
        }
    }

}
