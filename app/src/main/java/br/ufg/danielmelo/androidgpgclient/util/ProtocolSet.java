package br.ufg.danielmelo.androidgpgclient.util;

import java.util.LinkedHashMap;
import java.util.UUID;

import br.ufg.danielmelo.androidgpgclient.entity.Response;

import static android.R.attr.id;

/**
 * Created by daniel on 03/12/16.
 */

public class ProtocolSet {

    private static LinkedHashMap<UUID, Response> pendingRequests = new LinkedHashMap<>();

    public static Response getPendingRequest(UUID id, boolean purge) {
        Response response = pendingRequests.get(id);
        if (purge) {
            pendingRequests.remove(id);
        }
        return response;
    }

    public static UUID addPendingRequest() {
        UUID reqId = UUID.randomUUID();
        pendingRequests.put(reqId, null);
        return reqId;
    }

    public static void setRequestResult(UUID id, Response response) {
        pendingRequests.put(id, response);
    }

}
