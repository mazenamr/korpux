import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mazxn.korpux.persistence.RocksDBManager;

import org.junit.jupiter.api.Test;

public class TestRocksDBManager {
    @Test
    public void testRocksDBManager() {
        RocksDBManager rocksDBManager = new RocksDBManager("test-db");

        byte[] a = "a".getBytes();
        byte[] b = "b".getBytes();
        byte[] c = "c".getBytes();
        byte[] d = "d".getBytes();
        byte[] ab = "ab".getBytes();
        byte[] abc = "abc".getBytes();

        rocksDBManager.put(a, b);
        assertEquals(new String(b), new String(rocksDBManager.get(a)));

        rocksDBManager.delete(a);
        assertEquals(null, rocksDBManager.get(a));

        rocksDBManager.put(a, a);
        rocksDBManager.put(a, b);
        rocksDBManager.put(ab, c);
        rocksDBManager.put(abc, d);

        assertEquals(3, rocksDBManager.getRange(a).size());
        assertEquals(new String(b), new String(rocksDBManager.getRange(a).get(0)));
        assertEquals(new String(c), new String(rocksDBManager.getRange(a).get(1)));
        assertEquals(new String(d), new String(rocksDBManager.getRange(a).get(2)));
    }
}
