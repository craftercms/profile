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
