package com.takoy3466.emcbuilder.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** ネットワークパケットの登録。 */
public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    private ModNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(EmcSyncPayload.TYPE, EmcSyncPayload.STREAM_CODEC, EmcSyncPayload::handle);
    }
}
