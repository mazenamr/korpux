package persistence;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.List;

import org.rocksdb.*;

public class RocksDBManager implements Manager<String, List<Entry>> {
    private static final String DB_PATH = "./data/"; 
    private static final String DB_NAME = "korpux-db";

    private static volatile File dbDir;
    private static volatile RocksDB db = null;

    static {
        RocksDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);
        dbDir = new File(DB_PATH, DB_NAME);

        try {
            Files.createDirectories(dbDir.getParentFile().toPath());
            Files.createDirectories(dbDir.getAbsoluteFile().toPath());
            db = RocksDB.open(options, dbDir.getAbsolutePath());
        } catch (IOException | RocksDBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, List<Entry> value) {
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            out = new ObjectOutputStream(b);
            out.writeObject(value);
            db.put(key.getBytes(), b.toByteArray());
        } catch (IOException | RocksDBException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Entry> get(String key) {
        ObjectInputStream in = null;
        try {
            byte[] value = db.get(key.getBytes());
            if (value == null) {
                return null;
            }
            in = new ObjectInputStream(new ByteArrayInputStream(value));

            @SuppressWarnings("unchecked")
            List<Entry> entries = (List<Entry>) in.readObject();
            return entries;
        } catch (IOException | ClassNotFoundException | RocksDBException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void delete(String key) {
        try {
            db.delete(key.getBytes());
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }
}
