package com.takoy3466.emcbuilder.network;

import com.takoy3466.emcbuilder.EmcBuilder;
import com.takoy3466.emcbuilder.menu.EmcBuilderMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * GUI を開いているプレイヤーへ送る共有 EMC の残量（サーバー → クライアント）。
 * EMC は long のため、short しか送れないバニラの ContainerData ではなく独自パケットで同期する。
 *
 * @param containerId 対象メニューの ID
 * @param emc         共有 EMC プールの残量
 */
public record EmcSyncPayload(int containerId, long emc) implements CustomPacketPayload {
    public static final Type<EmcSyncPayload> TYPE = new Type<>(EmcBuilder.id("emc_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EmcSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, EmcSyncPayload::containerId,
            ByteBufCodecs.VAR_LONG, EmcSyncPayload::emc,
            EmcSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** クライアント側の処理。開いているメニューが一致すれば残量を反映する。 */
    static void handle(EmcSyncPayload payload, IPayloadContext context) {
        if (context.player().containerMenu instanceof EmcBuilderMenu menu && menu.containerId == payload.containerId) {
            menu.setEmc(payload.emc);
        }
    }
}
