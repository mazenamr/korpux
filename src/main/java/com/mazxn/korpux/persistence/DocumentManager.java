package com.mazxn.korpux.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.mazxn.korpux.Constants;

public class DocumentManager {
    private static final ReentrantLock mutex = new ReentrantLock();

    private static final RocksDBManager entryDBManager = new RocksDBManager(Constants.ENTRY_DB_NAME);

    public static void put(final String key, final String value) {
        try {
            mutex.lock();
            entryDBManager.put(key.getBytes(), value.getBytes());
        } finally {
            mutex.unlock();
        }
    }

    public static String get(final String key) {
        try {
            mutex.lock();
            byte[] value = entryDBManager.get(key.getBytes());
            if (value == null) {
                return null;
            }
            return new String(value);
        } finally {
            mutex.unlock();
        }
    }

    public static void delete(final String key) {
        try {
            mutex.lock();
            entryDBManager.delete(key.getBytes());
        } finally {
            mutex.unlock();
        }
    }

    public static List<String> getKeys(final String prefix) {
        try {
            mutex.lock();
            List<byte[]> values = entryDBManager.getKeys(prefix.getBytes());
            List<String> keys = new ArrayList<>();
            for (byte[] value : values) {
                keys.add(new String(value));
            }
            return keys;
        } finally {
            mutex.unlock();
        }
    }
}
