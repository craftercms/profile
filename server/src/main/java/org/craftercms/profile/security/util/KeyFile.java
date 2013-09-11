package org.craftercms.profile.security.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Represents a file where a single encryption key can be write to/read from.
 *
 * @author Alfonso Vásquez
 */
public class KeyFile {

    private File file;

    public KeyFile(File file) {
        this.file = file;
    }

    /**
     * Reads the key from the file through an {@link ObjectInputStream}.
     */
    public Key readKey() throws IOException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        try {
            return (Key)in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Class of a serialized object cannot be found", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Writes the given key to the files through an {@link ObjectOutputStream}.
     */
    public void writeKey(Key key) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(FileUtils.openOutputStream(file));
        try {
            out.writeObject(key);
            out.flush();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}