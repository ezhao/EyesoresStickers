package com.emilyzebra.eyesores;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;

class Eyesore {

    static final String ASSET_PATH = "eyesores";

    private static final String CONTENT_URI_ROOT = String.format("content://%s/", StickerProvider.class.getName());

    private final String filename;
    private final String keyword;
    private final String urlPattern;
    private final int index;

    static AssetFileDescriptor getAsset(Context context, Uri uri) throws FileNotFoundException {
        AssetManager assets = context.getAssets();
        String filename = uri.getLastPathSegment();
        if (filename == null) {
            throw new FileNotFoundException();
        }

        AssetFileDescriptor assetFileDescriptor = null;
        try {
            assetFileDescriptor = assets.openFd(ASSET_PATH + "/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return assetFileDescriptor;
    }

    Eyesore(String filename, String keyword, String urlPattern, int index) {
        this.filename = filename;
        this.keyword = keyword;
        this.urlPattern = urlPattern;
        this.index = index;
    }

    String getUrl() {
        return String.format(urlPattern, index);
    }

    String getImage() {
        return Uri.parse(CONTENT_URI_ROOT + filename).toString();
    }

    String getKeyword() {
        return keyword;
    }
}
