package de.nevergate.city;

import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

/** World-wide ledger, shared between dimensions; retained on death and relog. */
public final class StarterLedger extends SavedData {
    private final Set<UUID> recipients = new HashSet<>();
    public boolean has(UUID id) { return recipients.contains(id); }
    public void mark(UUID id) { recipients.add(id); setDirty(); }
    public static StarterLedger get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(StarterLedger::new, StarterLedger::load, net.minecraft.util.datafix.DataFixTypes.LEVEL), "ng_city_starters");
    }
    public static StarterLedger load(CompoundTag tag) {
        StarterLedger ledger = new StarterLedger();
        ListTag list = tag.getList("recipients", Tag.TAG_STRING);
        for (Tag entry : list) { try { ledger.recipients.add(UUID.fromString(entry.getAsString())); } catch (IllegalArgumentException ignored) { } }
        return ledger;
    }
    @Override public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        recipients.stream().sorted().forEach(id -> list.add(StringTag.valueOf(id.toString())));
        tag.put("recipients", list); return tag;
    }
}
