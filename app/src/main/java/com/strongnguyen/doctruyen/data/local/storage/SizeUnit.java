package com.strongnguyen.doctruyen.data.local.storage;

/**
 * The Class
 * Created by pc on 5/22/2017.
 */

public enum SizeUnit {
    B(1),
    KB(SizeUnit.BYTES),
    MB(SizeUnit.BYTES * SizeUnit.BYTES),
    GB(SizeUnit.BYTES * SizeUnit.BYTES * SizeUnit.BYTES),
    TB(SizeUnit.BYTES * SizeUnit.BYTES * SizeUnit.BYTES * SizeUnit.BYTES);

    private long inBytes;
    private static final int BYTES = 1024;

    private SizeUnit(long bytes) {
        this.inBytes = bytes;
    }

    public long inBytes() {
        return inBytes;
    }
}
