package com.emilyzebra.eyesores;

import android.content.Context;
import android.util.Log;
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

    private static final String TAG = "AppIndexingUtil";
    private static final String STICKER_PACK_NAME = "Eyesores Stickers";

    static void clearStickers(FirebaseAppIndex firebaseAppIndex, AppIndexingService.ServiceCallbacks serviceCallbacks) {
        Task<Void> task = firebaseAppIndex.removeAll();

        task.addOnSuccessListener(aVoid -> serviceCallbacks.onStickerRemoveSuccess());
        task.addOnFailureListener(e -> {
            Log.w(TAG, "Failed to clear stickers", e);
            serviceCallbacks.onStickerRemoveFail();
        });
    }

    static void setStickers(final Context context, FirebaseAppIndex firebaseAppIndex, AppIndexingService.ServiceCallbacks serviceCallbacks) {
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

            OnSuccessListener<Void> onSuccessListener = aVoid -> {
                Log.i(TAG, "Added stickers: " + size);
                serviceCallbacks.onStickerAddSuccess();
            };

            OnFailureListener onFailureListener = e -> {
                Log.d(TAG, "Failed to add stickers", e);
                serviceCallbacks.onStickerAddFail();
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
