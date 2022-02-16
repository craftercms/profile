/*
 * Copyright (C) 2007-2022 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.profile.api.services;

public class ProfileAttachment {

    private String md5;
    private String contentType;
    private String fileSize;
    private String fileName;
    private long fileSizeBytes;
    private String id;

    public ProfileAttachment() {
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(final String md5) {
        this.md5 = md5;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(final String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(final long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ProfileAttachment{" +
            "md5='" + md5 + '\'' +
            ", contentType='" + contentType + '\'' +
            ", fileSize='" + fileSize + '\'' +
            ", fileName=" + fileName +
            ", fileSizeBytes=" + fileSizeBytes +
            ", id='" + id + '\'' +
            '}';
    }
}
