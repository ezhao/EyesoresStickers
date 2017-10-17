package com.emilyzebra.eyesores;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseAppIndexingInvalidArgumentException;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Indexables;
import com.google.firebase.appindexing.builders.StickerBuilder;
import com.google.firebase.appindexing.builders.StickerPackBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AppIndexingUtil {
    private static final String STICKER_PACK_NAME = "Eyesores Stickers";
    private static final String TAG = "AppIndexingUtil";

    private static final String FAILED_CLEAR = "Failed to clear stickers";
    private static final String FAILED_INSTALL = "Failed to install stickers";
    private static final String SUCCESS_CLEAR = "Successfully cleared stickers";
    private static final String SUCCESS_INSTALL = "Successfully installed stickers ";

    static void clearStickers(final Context context, FirebaseAppIndex firebaseAppIndex) {
        Task<Void> task = firebaseAppIndex.removeAll();

        task.addOnSuccessListener(aVoid -> Toast.makeText(context, SUCCESS_CLEAR, Toast.LENGTH_SHORT).show());
        task.addOnFailureListener(e -> {
            Log.w(TAG, FAILED_CLEAR, e);
            Toast.makeText(context, FAILED_CLEAR, Toast.LENGTH_SHORT).show();
        });
    }

    static void setStickers(final Context context, FirebaseAppIndex firebaseAppIndex) {
        try {
            Eyesore cover = EyesoreUtil.getCover(context);
            StickerPackBuilder stickerPackBuilder = Indexables.stickerPackBuilder()
                    .setName(STICKER_PACK_NAME)
                    .setUrl(cover.getUrl())
                    .setImage(cover.getImage())
                    .setDescription("Indexable description TBD");

            List<StickerBuilder> stickers = getStickerBuilders(context, stickerPackBuilder);

            stickerPackBuilder
                    .setHasSticker(stickers.toArray(new StickerBuilder[stickers.size()]));

            List<Indexable> indexables = new ArrayList<>(stickers.size() + 1);
            for (StickerBuilder stickerBuilder : stickers) {
                indexables.add(stickerBuilder.build());
            }
            indexables.add(stickerPackBuilder.build());

            final int size = indexables.size();

            OnSuccessListener<Void> onSuccessListener = aVoid ->
                    Toast.makeText(context, SUCCESS_INSTALL + size, Toast.LENGTH_SHORT).show();

            OnFailureListener onFailureListener = e -> {
                Log.d(TAG, FAILED_INSTALL, e);
                Toast.makeText(context, FAILED_INSTALL, Toast.LENGTH_SHORT).show();
            };

            firebaseAppIndex
                    .update(indexables.toArray(new Indexable[indexables.size()]))
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener);

        } catch (IOException | FirebaseAppIndexingInvalidArgumentException e) {
            Log.e(TAG, "Unable to set stickers", e);
        }
    }

    private static List<StickerBuilder> getStickerBuilders(Context context, StickerPackBuilder stickerPackBuilder)
            throws IOException, FirebaseAppIndexingInvalidArgumentException {
        List<StickerBuilder> stickerBuilders = new ArrayList<>();

        List<Eyesore> eyesores = EyesoreUtil.getList(context);
        for (Eyesore eyesore : eyesores) {
            StickerBuilder stickerBuilder = Indexables.stickerBuilder()
                    .setName(STICKER_PACK_NAME)
                    .setUrl(eyesore.getUrl()) // unique and must match an intent-filter (e.g. eyesores://stickers/pack/0)
                    .setImage(eyesore.getImage())
                    .setDescription("Indexable description TBD") // used for accessibility
                    .put("keywords", eyesore.getKeyword()) // TODO: 10/15/17 emily verify
                    .setIsPartOf(stickerPackBuilder);
            stickerBuilders.add(stickerBuilder);
        }
        return stickerBuilders;
    }
}
