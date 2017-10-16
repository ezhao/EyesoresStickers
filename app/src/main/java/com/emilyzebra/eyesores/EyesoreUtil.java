package com.emilyzebra.eyesores;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class EyesoreUtil {
    private static final String INTENT_SCHEME = "eyesores"; // matches AndroidManifest.xml intent-filter scheme
    private static final String STICKER_URL_PATTERN = INTENT_SCHEME + "://sticker/%s";
    private static final String STICKER_PACK_URL_PATTERN = INTENT_SCHEME + "://sticker/pack/%s";

    /**
     * Writes a simple bitmap to local storage. The image is a solid color with size 400x400
     */
    private static void writeSolidColorBitmapToFile(File file, int color) throws IOException {
        Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(color);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

    }

    static Eyesore createCoverEyesore(File stickersDir) throws IOException {
        int COVER_INDEX = 99999; // might break if there are more than this many stickers
        if (!stickersDir.exists() && !stickersDir.mkdirs()) {
            throw new IOException("Stickers directory does not exist");
        }

        int stickerCoverInt = Color.CYAN;

        Eyesore eyesore = new Eyesore(stickerCoverInt, COVER_INDEX, STICKER_PACK_URL_PATTERN, "cyan");
        File stickerFile = new File(stickersDir, eyesore.getFilename());
        writeSolidColorBitmapToFile(stickerFile, eyesore.getColor());

        return eyesore;
    }

    static List<Eyesore> createEyesores(File stickersDir) throws IOException {
        int[] stickerColorInts = new int[] {Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.MAGENTA};
        String[] stickerColorNames = new String[] {"GREEN", "RED", "BLUE", "YELLOW", "MAGENTA"};

        if (!stickersDir.exists() && !stickersDir.mkdirs()) {
            throw new IOException("Stickers directory does not exist");
        }

        List<Eyesore> eyesores = new ArrayList<>();
        for (int i = 0; i < stickerColorInts.length; i++) {
            Eyesore eyesore = new Eyesore(stickerColorInts[i], i, STICKER_URL_PATTERN, stickerColorNames[i]);

            File stickerFile = new File(stickersDir, eyesore.getFilename());
            writeSolidColorBitmapToFile(stickerFile, eyesore.getColor());

            eyesores.add(eyesore);
        }
        return eyesores;
    }
}
