package goldenbrother.gbmobile.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by asus on 2016/6/27.
 */
public class BitmapHelper {

    public static final int MAX_WIDTH = 768;
    public static final int MAX_HEIGHT = 1024;

    public static Bitmap resize(Bitmap bmp, int maxW, int maxH) {
        if (bmp == null)
            return null;
        // old w , h
        int oldWidth = bmp.getWidth();
        int oldHeight = bmp.getHeight();
        // conform upload format
        if (oldWidth <= maxW && oldHeight <= maxH)
            return bmp;
        // new format
        int newWidth = oldWidth;
        int newHeight = oldHeight;
        do {
            newWidth *= 0.9;
            newHeight *= 0.9;
        } while (newWidth > maxW || newHeight > maxH);
        // scale %
        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        // scale matrix params
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bmp, 0, 0, oldWidth, oldHeight, matrix, true);
    }

    public static String bitmap2String(Bitmap bmp) {
        return Base64.encodeToString(bitmap2Byte(bmp), Base64.DEFAULT);
    }

    public static byte[] bitmap2Byte(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap byte2Bitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static Bitmap getUploadServerBitmap(Context context, Uri uri) throws IOException {
        return BitmapHelper.getLimitBitmap(MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri), MAX_WIDTH, MAX_HEIGHT);
    }

    public static Bitmap getLimitBitmap(Bitmap bmp, int max_width, int max_height) {
        if (bmp == null) return null;
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        if (h > w) { // portrait
            while (h > max_height || w > max_width) {
                h *= 0.9;
                w *= 0.9;
            }
        } else { // landscape
            while (h > max_width || w > max_height) {
                h *= 0.9;
                w *= 0.9;
            }
        }

        return Bitmap.createScaledBitmap(bmp, w, h, true);
    }

    public static String getUploadServerBitmapString(Context context, Uri uri) throws IOException {
        return bitmap2String(getUploadServerBitmap(context, uri));
    }

    public static File convertBitmapToFile(Context context, Bitmap bitmap) {
        try {
            File f = new File(context.getCacheDir(), getRandomName());
            f.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File getCatcheJPG(Context context) {
        try {
            File f = new File(context.getCacheDir(), getRandomName());
            f.createNewFile();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap comvertFileToBitmap(File f) {
        return BitmapFactory.decodeFile(f.getAbsolutePath());
    }

    public static String getRandomName() {
        return UUID.randomUUID().toString();
    }


}
