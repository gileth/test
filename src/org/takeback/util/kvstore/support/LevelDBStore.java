// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.util.kvstore.support;

import java.util.HashMap;
import java.util.Iterator;
import org.iq80.leveldb.WriteBatch;
import java.util.List;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.ReadOptions;
import java.util.Map;
import java.io.IOException;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.iq80.leveldb.CompressionType;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import org.iq80.leveldb.DB;
import java.io.File;
import org.iq80.leveldb.Options;
import org.takeback.util.kvstore.KVStore;

public class LevelDBStore implements KVStore
{
    private String name;
    private Options options;
    private File databaseDir;
    private DB db;
    private final Lock connectingLock;
    private AtomicBoolean initedStatus;
    
    public LevelDBStore(final String name) {
        this.name = this.getClass().getSimpleName();
        this.connectingLock = new ReentrantLock();
        this.initedStatus = new AtomicBoolean(false);
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    @Override
    public void start() {
        this.connectingLock.lock();
        try {
            if (!this.initedStatus.get()) {
                this.databaseDir = new File(System.getProperty("user.home") + File.separator + "leveldb" + File.separator + this.name);
                this.options = new Options().createIfMissing(true).compressionType(CompressionType.NONE);
                this.db = Iq80DBFactory.factory.open(this.databaseDir, this.options);
                this.initedStatus.set(true);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("init db error.", e);
        }
        finally {
            this.connectingLock.unlock();
        }
    }
    
    public DB db() {
        return this.db;
    }
    
    @Override
    public void close() {
        try {
            if (this.db != null) {
                this.db.close();
                this.initedStatus.set(false);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("close db error.", e);
        }
    }
    
    private void checkStatus() {
        if (!this.initedStatus.get()) {
            throw new IllegalStateException("Need to start the store at first.");
        }
    }
    
    @Override
    public void put(final String k, final String v) {
        this.checkStatus();
        this.db.put(Iq80DBFactory.bytes(k), Iq80DBFactory.bytes(v));
    }
    
    @Override
    public void put(final byte[] k, final byte[] v) {
        this.checkStatus();
        this.db.put(k, v);
    }
    
    @Override
    public String get(final String k) {
        this.checkStatus();
        return Iq80DBFactory.asString(this.db.get(Iq80DBFactory.bytes(k)));
    }
    
    @Override
    public byte[] get(final byte[] k) {
        this.checkStatus();
        return this.db.get(k);
    }
    
    @Override
    public Map.Entry<byte[], byte[]> seekFirst() {
        this.checkStatus();
        final ReadOptions ro = new ReadOptions();
        final DBIterator it = this.db.iterator(ro);
        it.seekToFirst();
        if (it.hasNext()) {
            final Map.Entry<byte[], byte[]> e = (Map.Entry<byte[], byte[]>)it.peekNext();
            return e;
        }
        return null;
    }
    
    @Override
    public void remove(final String k) {
        this.checkStatus();
        this.db.delete(Iq80DBFactory.bytes(k));
    }
    
    @Override
    public void remove(final byte[] k) {
        this.checkStatus();
        this.db.delete(k);
    }
    
    @Override
    public void removes(final List<String> ks) {
        this.checkStatus();
        WriteBatch batch = null;
        try {
            batch = this.db.createWriteBatch();
            for (final String k : ks) {
                batch.delete(Iq80DBFactory.bytes(k));
            }
            this.db.write(batch);
        }
        finally {
            try {
                if (batch != null) {
                    batch.close();
                }
            }
            catch (IOException e) {
                throw new IllegalStateException("Error occurs when close batch.", e);
            }
        }
    }
    
    @Override
    public void removesByByte(final List<byte[]> ks) {
        this.checkStatus();
        WriteBatch batch = null;
        try {
            batch = this.db.createWriteBatch();
            for (final byte[] k : ks) {
                batch.delete(k);
            }
            this.db.write(batch);
        }
        finally {
            try {
                if (batch != null) {
                    batch.close();
                }
            }
            catch (IOException e) {
                throw new IllegalStateException("Error occurs when close batch.", e);
            }
        }
    }
    
    @Override
    public void puts(final Map<String, String> kv) {
        this.checkStatus();
        WriteBatch batch = null;
        try {
            batch = this.db.createWriteBatch();
            for (final String k : kv.keySet()) {
                final String v = kv.get(k);
                batch.put(Iq80DBFactory.bytes(k), Iq80DBFactory.bytes(v));
            }
            this.db.write(batch);
        }
        finally {
            try {
                if (batch != null) {
                    batch.close();
                }
            }
            catch (IOException e) {
                throw new IllegalStateException("Error occurs when close batch.", e);
            }
        }
    }
    
    @Override
    public void putsByByte(final Map<byte[], byte[]> kv) {
        this.checkStatus();
        WriteBatch batch = null;
        try {
            batch = this.db.createWriteBatch();
            for (final byte[] k : kv.keySet()) {
                final byte[] v = kv.get(k);
                batch.put(k, v);
            }
            this.db.write(batch);
        }
        finally {
            try {
                if (batch != null) {
                    batch.close();
                }
            }
            catch (IOException e) {
                throw new IllegalStateException("Error occurs when close batch.", e);
            }
        }
    }
    
    @Override
    public Map<String, String> gets() {
        this.checkStatus();
        DBIterator iterator = null;
        try {
            iterator = this.db.iterator();
            final Map<String, String> map = new HashMap<String, String>();
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                final Map.Entry<byte[], byte[]> e = (Map.Entry<byte[], byte[]>)iterator.peekNext();
                map.put(Iq80DBFactory.asString((byte[])e.getKey()), Iq80DBFactory.asString((byte[])e.getValue()));
                iterator.next();
            }
            return map;
        }
        finally {
            try {
                if (iterator != null) {
                    iterator.close();
                }
            }
            catch (IOException e2) {
                throw new IllegalStateException("Error occurs when close iterator.", e2);
            }
        }
    }
    
    @Override
    public Map<byte[], byte[]> getsByByte() {
        this.checkStatus();
        DBIterator iterator = null;
        try {
            iterator = this.db.iterator();
            final Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                final Map.Entry<byte[], byte[]> e = (Map.Entry<byte[], byte[]>)iterator.peekNext();
                map.put(e.getKey(), e.getValue());
                iterator.next();
            }
            return map;
        }
        finally {
            try {
                if (iterator != null) {
                    iterator.close();
                }
            }
            catch (IOException e2) {
                throw new IllegalStateException("Error occurs when close iterator.", e2);
            }
        }
    }
    
    @Override
    public boolean containsKey(final String k) {
        return this.containsKey(Iq80DBFactory.bytes(k));
    }
    
    @Override
    public boolean containsKey(final byte[] k) {
        this.checkStatus();
        return this.db.get(k) != null;
    }
}
