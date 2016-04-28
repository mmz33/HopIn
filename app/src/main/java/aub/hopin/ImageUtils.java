package aub.hopin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.text.BreakIterator;

public class ImageUtils {

    final static int borderWidth = 10;

    // Takes a BitMap and creates a rounded version of it.
    public static Bitmap makeRounded(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private static Bitmap makeRoundBorder(Bitmap bitmap, String colorHex) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth() + borderWidth, bitmap.getHeight() + borderWidth, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setColor(Color.parseColor(colorHex));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);

        RectF rectF = new RectF(borderWidth/2.0f, borderWidth/2.0f, canvas.getWidth() - borderWidth/2.0f, canvas.getHeight() - borderWidth/2.0f);
        canvas.drawOval(rectF, paint);

        return output;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap overlayRoundBorder(Bitmap bitmap, String colorHex) {
        Bitmap border = makeRoundBorder(bitmap, colorHex);
        Bitmap result = Bitmap.createBitmap(border.getWidth(), border.getHeight(), border.getConfig());
        Bitmap mmm = makeRounded(bitmap);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(result);

        float left = canvas.getWidth()/2.0f  - mmm.getWidth()/2.0f;
        float top  = canvas.getHeight()/2.0f - mmm.getHeight()/2.0f;
        canvas.drawBitmap(mmm, left, top, paint);

        canvas.drawBitmap(border, new Matrix(), paint);

        border.recycle();
        return result;
    }

    // Encodes an image to Base64
    public static String encodeBase64(Bitmap bmp) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] imageBytes = os.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    // Decodes an image from Base64
    public static Bitmap decodeBase64(String str) {
        byte[] imageBytes = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
