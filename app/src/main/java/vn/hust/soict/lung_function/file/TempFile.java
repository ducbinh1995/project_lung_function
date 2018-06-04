package vn.hust.soict.lung_function.file;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

public class TempFile {
    private Context mContext;

    private String mNameTempFile;
    private OutputStream mOutputStream;

    private boolean isWrite = false;

    public TempFile(Context context) throws FileNotFoundException {
        mContext = context;
        mNameTempFile = mContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()
                + "/TMP_" + (new Date().getTime());
        mOutputStream = new FileOutputStream(mNameTempFile);
        isWrite = true;
    }

    public void write(byte[] bytes) throws IOException {
        if (isWrite)
            mOutputStream.write(bytes, 0, bytes.length);
    }

    public void close() throws IOException {
        isWrite = false;
        mOutputStream.close();
    }

    public boolean deleteFile() {
        if (mNameTempFile == null) return false;
        return new File(mNameTempFile).delete();
    }

    public String getPathTempFile() {
        return mNameTempFile;
    }
}
