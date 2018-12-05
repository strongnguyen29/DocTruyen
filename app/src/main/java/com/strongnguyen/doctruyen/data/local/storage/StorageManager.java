package com.strongnguyen.doctruyen.data.local.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.strongnguyen.doctruyen.util.LogUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The Class
 * Created by pc on 5/22/2017.
 */

public class StorageManager {
    private static final String TAG = StorageManager.class.getSimpleName();

    private Context mContext;

    public StorageManager(Context mContext) {
        this.mContext = mContext;
    }

    public boolean deleteFile(String directoryName, String fileName) {
        String path = buildPath(directoryName, fileName);
        File file = new File(path);
        boolean success = file.delete();
        LogUtils.d(TAG, "deleteFile path : " + path + " | Success : " + success);
        return success;
    }

    public void deleteAll(String directoryName) {
        String path = buildPath(directoryName);
        File filePath = new File(path);
        deleteRecursive(filePath);
        for (File file : filePath.listFiles()) {
            boolean success = file.delete();
            LogUtils.d(TAG, "deleteAll path : " + path + " | Name : " + file.getName() +" | Success : " + success);
        }
    }

    public boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }
        return fileOrDirectory.delete();
    }

    public List<File> getFiles(String directoryName, final String matchRegex) {
        String buildPath = buildPath(directoryName);
        LogUtils.d(TAG, "buildPath : " + buildPath);
        File file = new File(buildPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        List<File> out = null;
        if (matchRegex != null) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String fileName) {
                    return fileName.matches(matchRegex);
                }
            };
            out = Arrays.asList(file.listFiles(filter));
        } else {
            out = Arrays.asList(file.listFiles());
        }
        return out;
    }

    public List<File> getFiles(String directoryName, OrderType orderType) {
        List<File> files = getFiles(directoryName, (String) null);
        Collections.sort(files, orderType.getComparator());
        return files;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public float getFreeSpace(SizeUnit sizeUnit) {
        String path = buildAbsolutePath();
        StatFs statFs = new StatFs(path);
        long availableBlocks;
        long blockSize;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = statFs.getAvailableBlocks();
            blockSize = statFs.getBlockSize();
        } else {
            availableBlocks = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
        }
        long freeBytes = availableBlocks * blockSize;
        float sz = (float) (((double)freeBytes) / sizeUnit.inBytes());
        LogUtils.d(TAG, "getFreeSpace : " + String.valueOf(sz));
        return sz;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public float getUsedSpace(SizeUnit sizeUnit) {
        String path = buildAbsolutePath();
        StatFs statFs = new StatFs(path);
        long availableBlocks;
        long blockSize;
        long totalBlocks;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = statFs.getAvailableBlocks();
            blockSize = statFs.getBlockSize();
            totalBlocks = statFs.getBlockCount();
        } else {
            availableBlocks = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
            totalBlocks = statFs.getBlockCountLong();
        }
        long usedBytes = totalBlocks * blockSize - availableBlocks * blockSize;
        float sz = (float) (((double)usedBytes) / sizeUnit.inBytes());
        LogUtils.d(TAG, "getUsedSpace : " + sz);
        return sz;
    }

    public float getFolderSize(String directoryName, SizeUnit sizeUnit) {
        long size = 0;
        String path = buildPath(directoryName);
        File dir = new File(path);
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                size += file.length();
            }
        }
        float sz = (float) (((double)size) / sizeUnit.inBytes());
        LogUtils.d(TAG, "getFolderSize : " + sz);
        return sz;
    }

    public float getFolderSize(List<File> files, SizeUnit sizeUnit) {
        if (files == null && files.size() == 0) {
            return 0;
        }

        long size = 0;
        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            }
        }
        float sz = (float) (((double)size) / sizeUnit.inBytes());
        LogUtils.d(TAG, "getFolderSize : " + sz);
        return sz;
    }

    /**
     * Checks if external storage is available for read and write. <br>
     *
     * @return <code>True</code> if external storage writable, otherwise return
     *         <code>False</code>
     */
    public boolean isWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private String buildAbsolutePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    /**
     * Build path of folder or file on the external storage location. <br>
     * <b>Note: </b> <li>For directory use regular name</li> <li>For file name
     * use name with .extension like <i>abc.png</i></li><br>
     * <br>
     *
     * @param name
     *            The name of the directory
     * @return
     */
    public String buildPath(String name) {
        String path = buildAbsolutePath();
        path = path + File.separator + name;
        return path;
    }

    /**
     * Build folder + file on the external storage location. <br>
     * <b>Note: </b> <li>For directory use regular name</li> <li>For file name
     * use name with .extension like <i>abc.png</i></li><br>
     * <br>
     *
     * @param directoryName
     *            The directory name
     * @param fileName
     *            The file name
     * @return
     */
    public String buildPath(String directoryName, String fileName) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        path = path + File.separator + directoryName + File.separator + fileName;
        return path;
    }
}
