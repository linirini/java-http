package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Session {
    private final String id;
    private final Map<String, Object> values;

    private Session(String id) {
        this.id = id;
        this.values = new HashMap<>();
    }

    public static Session createRandomSession() {
        return new Session(UUID.randomUUID().toString());
    }

    public String getId() {
        return id;
    }

    public Object getAttribute(String id) {
        return values.get(id);
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }
}
