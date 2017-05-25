package com.torshare.types;

/**
 * Created by tyler on 5/25/17.
 */
public class FileDetail {

    private String path;
    private Long sizeBytes;
    private Integer index;

    private FileDetail(String path,
        Long sizeBytes,
        Integer index) {
        this.path = path;
        this.sizeBytes = sizeBytes;
        this.index = index;
    }

    public static FileDetail create(String path,
                                    Long sizeBytes,
                                    Integer index) {
        return new FileDetail(path,
                sizeBytes,
                index);
    }

    public String getPath() {
        return path;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public Integer getIndex() {
        return index;
    }
}
