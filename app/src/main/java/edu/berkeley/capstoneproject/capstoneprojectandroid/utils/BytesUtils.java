package edu.berkeley.capstoneproject.capstoneprojectandroid.utils;

import android.util.Log;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

/**
 * Created by Alex on 26/10/2017.
 */

public class BytesUtils {

    private static final String TAG = BytesUtils.class.getSimpleName();

    public static final int BYTES_INT16     = 2;
    public static final int BYTES_TIMESTAMP = 4;
    public static final int BYTES_FLOAT     = 4;

    public static int bytesToInt16(byte[] bytes) {
        return bytesToInt16(bytes, 0);
    }

    public static int bytesToInt16(byte[] bytes, int offset) {
        int value = 0;
        for (int i = 0; i < BYTES_INT16; i++) {
            value |= ((bytes[offset + i]) & 0xFF) << i * 8;
        }
        return value;
    }

    public static float bytesToFloat(byte[] bytes) { return bytesToFloat(bytes, 0); }

    public static float bytesToFloat(byte[] bytes, int offset) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset, BYTES_FLOAT);
        try {
            return buffer.getFloat();
        }
        catch (BufferUnderflowException e) {
            Log.e(TAG, "Byte buffer error: " + buffer.toString(), e);
            return 0;
        }
    }


    public static long bytesToDate(byte[] bytes) {
        return bytesToDate(bytes, 0);
    }

    public static long bytesToDate(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 0; i < BYTES_TIMESTAMP; i++) {
            value |= ((bytes[offset + i] & 0xFF) << 8*i);
        }
        return value;
    }
}
