package com.mazxn.korpux.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.mazxn.korpux.Constants;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

public class RocksDBManager {

    private String name = "korpux-db";
    private RocksDB db;

    static {
        RocksDB.loadLibrary();
    }

    public RocksDBManager(String dbName) {
        name = dbName;
        final Options options = new Options();
        options.setCreateIfMissing(true);
        File dir = new File(Constants.DB_PATH, name);

        try {
            Files.createDirectories(dir.getParentFile().toPath());
            db = RocksDB.open(options, dir.getAbsolutePath());
        } catch (IOException | RocksDBException e) {
            e.printStackTrace();
        }
    }

    public void put(final byte[] key, final byte[] value) {
        try {
            db.put(key, value);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public byte[] get(final byte[] key) {
        try {
            return db.get(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<byte[]> getRange(final byte[] prefix) {
        RocksIterator it = db.newIterator();
        it.seek(prefix);
        List<byte[]> result = new ArrayList<>();
        for (it.seek(prefix); it.isValid(); it.next()) {
            Boolean isPrefix = true;
            byte[] key = it.key();
            for (int i = 0; i < prefix.length; i++) {
                if (key[i] != prefix[i]) {
                    isPrefix = false;
                }
            }
            if (isPrefix) {
                result.add(it.value());
            } else {
                break;
            }
        }
        return result;
    }

    public List<byte[]> getKeys(final byte[] prefix) {
        RocksIterator it = db.newIterator();
        it.seek(prefix);
        List<byte[]> result = new ArrayList<>();
        for (it.seek(prefix); it.isValid(); it.next()) {
            Boolean isPrefix = true;
            byte[] key = it.key();
            for (int i = 0; i < prefix.length; i++) {
                if (key[i] != prefix[i]) {
                    isPrefix = false;
                }
            }
            if (isPrefix) {
                result.add(key);
            } else {
                break;
            }
        }
        return result;
    }

    public void delete(final byte[] key) {
        try {
            db.delete(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }
}
