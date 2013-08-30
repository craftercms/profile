/*
 * Copyright (C) 2007-2013 Rivet Logic Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.utils.crypto;

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

