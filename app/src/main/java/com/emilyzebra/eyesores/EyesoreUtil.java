package com.emilyzebra.eyesores;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class EyesoreUtil {
    private static final String INTENT_SCHEME = "eyesores"; // matches AndroidManifest.xml intent-filter scheme
    private static final String STICKER_URL_PATTERN = INTENT_SCHEME + "://sticker/%s";
    private static final String STICKER_PACK_URL_PATTERN = INTENT_SCHEME + "://sticker/pack/%s";
    private static final String KEYWORD = "eyesore";

    /**
     * For now just use first sticker as cover
     */
    static Eyesore getCover(Context context) throws IOException {
        String[] stickers = getStickers(context);
        return new Eyesore(stickers[0], KEYWORD, STICKER_PACK_URL_PATTERN, 99999);
    }

    static List<Eyesore> getList(Context context) throws IOException {
        String[] stickers = getStickers(context);
        List<Eyesore> eyesores = new ArrayList<>();
        for (int i = 0; i < stickers.length; i++) {
            Eyesore eyesore = new Eyesore(stickers[i], KEYWORD, STICKER_URL_PATTERN, i);
            eyesores.add(eyesore);
        }
        return eyesores;
    }

    private static String[] getStickers(Context context) throws IOException {
        AssetManager assets = context.getAssets();
        return assets.list(Eyesore.ASSET_PATH);
    }
}
