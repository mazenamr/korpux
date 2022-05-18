package persistence;

public interface Manager<K, V> {
    void put(K key, V value);
    V get (K key);
    void delete (K key);
}
