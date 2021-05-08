package com.company;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cache<K,V> {

    class  Value<V> {
        public long timeStamp;
        public V value;
    }

    long ttl;
    private Map<K,Value<V>> storage = new HashMap<>();
    int average;

    public Cache(long ttl) {
        this.ttl = ttl;
        this.average = 0;
    }

    public void put(K key, V value) {
      long timeStamp = System.currentTimeMillis();
      Value<V> valueObject = new Value<>();
      valueObject.timeStamp = timeStamp;
      valueObject.value = value;
      storage.put(key, valueObject);
      average = (average + (int)value)/2;
    }

    public V get(K key) {
        if(storage.containsKey(key)) {
            Value<V> valueObject = storage.get(key);
            if(isKeyExpired(valueObject)) {
                storage.remove(key);
                updateAverage(valueObject);
                System.out.println("TTL expired");
            } else {
                System.out.println("TTL not expired");
                return storage.get(key).value;
            }
        }
        return null;
    }

    private boolean isKeyExpired(Value<V> valueObject) {
        return System.currentTimeMillis() - valueObject.timeStamp > this.ttl;
    }

    private void updateAverage(Value<V> valueObject) {
        this.average = average * 2 - (int)valueObject.value;
    }

    private void clean() {
        Iterator iterator = storage.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)iterator.next();
            if(isKeyExpired((Value<V>) mapElement.getValue())) {
                updateAverage((Value<V> )mapElement.getValue());
                storage.remove((K) mapElement.getKey());
            }
        }
    }
    public int getAverage() {
        clean();
        return average;
    }


}
