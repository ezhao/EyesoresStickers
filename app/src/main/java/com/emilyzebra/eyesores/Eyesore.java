package com.emilyzebra.eyesores;

import android.net.Uri;

public class Eyesore {
    private static final String STICKER_FILENAME_PATTERN = "sticker%s.png";
    private static final String CONTENT_URI_ROOT = String.format("content://%s/", StickerProvider.class.getName());

    private final int color;
    private final int index;
    private final String urlPattern;
    private final String keyword;

    Eyesore(int color, int index, String urlPattern, String keyword) {
        this.color = color;
        this.index = index;
        this.urlPattern = urlPattern;
        this.keyword = keyword;
    }

    public int getColor() {
        return color;
    }

    String getFilename() {
        return String.format(STICKER_FILENAME_PATTERN, index);
    }

    String getUrl() {
        return String.format(urlPattern, index);
    }

    String getImage() {
        return Uri.parse(CONTENT_URI_ROOT + getFilename()).toString();
    }

    String getKeyword() {
        return keyword;
    }
}
