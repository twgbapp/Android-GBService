package goldenbrother.gbmobile.helper;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;

/**
 * Created by asus on 2016/6/19.
 */

public class QRCodeHelper {
    public static Bitmap encodeAsBitmap(String contents, BarcodeFormat format,
                                        int desiredWidth, int desiredHeight) throws WriterException {
        if (contents.length() == 0)
            return null;
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;
        HashMap<EncodeHintType, String> hints = null;
        String encoding = null;
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                encoding = "UTF-8";
                break;
            }
        }
        if (encoding != null) {
            hints = new HashMap<EncodeHintType, String>(2);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = writer.encode(contents, format, desiredWidth,
                desiredHeight, hints);
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
