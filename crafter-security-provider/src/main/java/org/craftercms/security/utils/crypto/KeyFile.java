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

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.security.Key;

/**
 * Represents a file where a single key can be write to/read from.
 *
 * @author Alfonso Vásquez
 */
public class KeyFile {

    private File file;

    public KeyFile(File file) {
        this.file = file;
    }

    public Key readKey() throws IOException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        try {
            return (Key) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Class of a serialized object cannot be found", e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }
    }

    public void writeKey(Key key) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(FileUtils.openOutputStream(file));
        try {
            out.writeObject(out);
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
        }
    }

}
