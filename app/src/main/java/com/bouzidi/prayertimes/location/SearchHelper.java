package com.bouzidi.prayertimes.location;

import com.bouzidi.prayertimes.location.photon.Feature;
import com.bouzidi.prayertimes.location.photon.PhotonAPIResponse;
import com.bouzidi.prayertimes.location.photon.PhotonAPIService;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class SearchHelper {

    public static Single<List<Feature>> searchForLocation(final String str, final int limit) {

        return Single.create(emitter -> {
            Thread thread = new Thread(() -> {

                try {
                    PhotonAPIResponse photonAPIResponse = PhotonAPIService.getInstance().search(str, limit);
                    emitter.onSuccess(photonAPIResponse.getFeatures());
                } catch (IOException e) {
                    emitter.onError(e);
                }
            });
            thread.start();
        });
    }
}
