package aub.hopin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.io.IOException;

public class BitmapDownloader {
    public static Bitmap downloadBitmap(String url, int downSample, int maxSize) throws IllegalArgumentException {
        if(downSample < 1) throw new IllegalArgumentException("Down sample cannot be less than 1");

        final int max = maxSize <= 0 ? 1000000 : maxSize; //1MB
        ErrorLogger.info("Downloading " + url);

        try {
            URL myURL = new URL(url);
            URLConnection connection = myURL.openConnection();
            InputStream is = connection.getInputStream();

            int size = connection.getContentLength();
            int sample = downSample;
            while (size / sample > max && sample <= 16) sample *= 2;

            Bitmap image;
            if (sample > 1) {
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = sample;
                image = BitmapFactory.decodeStream(new BufferedInputStream(is), null, o2);
            } else {
                image = BitmapFactory.decodeStream(new BufferedInputStream(is));
            }
            is.close();
            if (image == null && sample < 8) {
                return downloadBitmap(url, sample * 2, max);
            } else {
                return image;
            }
        } catch (FileNotFoundException e) {
            ErrorLogger.error("Error loading image: " + url);
            return null;
        } catch (MalformedURLException e) {
            ErrorLogger.error("Error loading image: " + e.getMessage());
            return null;
        } catch (IOException e) {
            ErrorLogger.info("Faced IO Exception, about to double down sampling.");
            if (downSample < 8) {
                return downloadBitmap(url, downSample * 2, max);
            } else {
                return null;
            }
        } catch (OutOfMemoryError th) {
            ErrorLogger.info("Out of Memory, about to double down sampling.");
            if (downSample < 8) {
                return downloadBitmap(url, downSample * 2, max);
            } else {
                return null;
            }
        } catch (Throwable t) {
            ErrorLogger.error("Error loading image: " + t.getMessage());
            return null;
        }
    }
}