package com.mazxn.korpux.Persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class EntryManager {
    private static final ReentrantLock mutex = new ReentrantLock();

    private static final String ENTRY_DB_NAME = "entry-db";
    private static final String COUNT_DB_NAME = "count-db";

    private static final RocksDBManager entryDBManager = new RocksDBManager(ENTRY_DB_NAME);
    private static final RocksDBManager countDBManager = new RocksDBManager(COUNT_DB_NAME);

    public static void add(final String key, final Entry value) {
        try {
            mutex.lock();
            ObjectOutputStream out = null;
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                out = new ObjectOutputStream(b);
                out.writeObject(value);
                entryDBManager.put(key.getBytes(), b.toByteArray());
                String url = value.URL();
                int count = byteToInt(countDBManager.get(url.getBytes()));
                countDBManager.put(url.getBytes(), intToByte(count + 1));
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            mutex.unlock();
        }
    }

    public static Entry get(String key) {
        try {
            mutex.lock();
            ObjectInputStream in = null;
            try {
                byte[] value = entryDBManager.get(key.getBytes());
                if (value == null) {
                    return null;
                }
                in = new ObjectInputStream(new ByteArrayInputStream(value));
                return (Entry) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public static List<Entry> getRange(final String prefix) {
        try {
            mutex.lock();
            ObjectInputStream in = null;
            try {
                List<byte[]> values = entryDBManager.getRange(prefix.getBytes());
                List<Entry> entries = new ArrayList<>();
                for (byte[] value : values) {
                    in = new ObjectInputStream(new ByteArrayInputStream(value));
                    entries.add((Entry) in.readObject());
                }
                return entries;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        } finally {
            mutex.unlock();
        }
    }

    public static int getCount(String url) {
        try {
            mutex.lock();
            return byteToInt(countDBManager.get(url.getBytes()));
        } finally {
            mutex.unlock();
        }
    }

    public static void resetCount(String url) {
        try {
            mutex.lock();
            countDBManager.put(url.getBytes(), intToByte(0));
        } finally {
            mutex.unlock();
        }
    }

    private static byte[] intToByte(int value) {
        return new byte[] {
                (byte) ((value >> 24) & 0xff),
                (byte) ((value >> 16) & 0xff),
                (byte) ((value >> 8) & 0xff),
                (byte) ((value >> 0) & 0xff),
        };
    }

    private static int byteToInt(byte[] value) {
        if (value == null || value.length != 4) {
            return 0x0;
        }
        return (int) ((0xff & value[0]) << 24 |
                (0xff & value[1]) << 16 |
                (0xff & value[2]) << 8 |
                (0xff & value[3]) << 0);
    }
}
