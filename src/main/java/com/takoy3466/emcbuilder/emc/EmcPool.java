package com.takoy3466.emcbuilder.emc;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * 全 EMC ビルダーで共有されるワールド単位の EMC プール（元 {@code EMCWorldSave} 相当）。
 * <p>
 * 元実装の仕様:
 * <ul>
 *   <li>初期値 0、上限 {@value #LIMIT}</li>
 *   <li>NBT キー {@value #NBT_KEY}、データ名 {@value #DATA_NAME}</li>
 *   <li>EMC が上限以上なら投入を拒否し、加算後は上限で切り詰める</li>
 * </ul>
 * 元実装は static フィールドだったためワールドを切り替えると値が残る問題があったが、
 * 本実装はオーバーワールドの {@link SavedData} インスタンスとして保持する。
 * サーバースレッドからのみ操作すること。
 */
public final class EmcPool extends SavedData {
    /** 元 {@code EMCWorldSave.emcLimit} の値。 */
    public static final long LIMIT = 9_000_000_000_000_000_000L;
    public static final String DATA_NAME = "ce_world";
    public static final String NBT_KEY = "CE_EMCBuilder_EMC";

    private static final Factory<EmcPool> FACTORY = new Factory<>(EmcPool::new, EmcPool::load);

    private long emc;

    public EmcPool() {
    }

    /** サーバー全体で共有されるプールを取得する（存在しなければ 0 で作成）。 */
    public static EmcPool get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    private static EmcPool load(CompoundTag tag, HolderLookup.Provider registries) {
        EmcPool pool = new EmcPool();
        pool.emc = clamp(tag.getLong(NBT_KEY));
        return pool;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putLong(NBT_KEY, emc);
        return tag;
    }

    public long getEmc() {
        return emc;
    }

    public boolean isFull() {
        return emc >= LIMIT;
    }

    /**
     * 1個あたり {@code unitValue} のアイテムを最大 {@code count} 個 EMC に変換する。
     * <p>
     * 元の {@code addEmc} を 1 個ずつ繰り返すのと同じ結果になる（上限到達まで受け入れ、最後の 1 個で上限に切り詰める）。
     * ただし元実装と違い、受け入れなかったアイテムは消費扱いにしない。
     *
     * @return 受け入れた個数（呼び出し側はこの個数だけアイテムを減らす）
     */
    public int deposit(long unitValue, int count) {
        if (unitValue <= 0 || count <= 0 || isFull()) {
            return 0;
        }
        long room = LIMIT - emc;
        // 上限に達するのに必要な個数（切り上げ）
        long needed = room / unitValue + (room % unitValue == 0 ? 0 : 1);
        int accepted = (int) Math.min(count, needed);
        if (accepted >= needed) {
            emc = LIMIT;
        } else {
            emc += unitValue * accepted; // accepted < needed なので room 未満で溢れない
        }
        setDirty();
        return accepted;
    }

    /** EMC を消費する。足りなければ何もせず false。 */
    public boolean withdraw(long amount) {
        if (amount < 0 || amount > emc) {
            return false;
        }
        emc -= amount;
        setDirty();
        return true;
    }

    /** 建築に失敗した場合などに EMC を戻す（上限で切り詰め）。 */
    public void refund(long amount) {
        if (amount <= 0) {
            return;
        }
        emc = amount > LIMIT - emc ? LIMIT : emc + amount;
        setDirty();
    }

    /** 管理コマンド用。範囲外の値は 0〜上限に丸める。 */
    public void set(long value) {
        emc = clamp(value);
        setDirty();
    }

    private static long clamp(long value) {
        return Math.max(0, Math.min(LIMIT, value));
    }
}
