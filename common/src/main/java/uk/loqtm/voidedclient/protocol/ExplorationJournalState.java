package uk.loqtm.voidedclient.protocol;

import java.util.ArrayList;
import java.util.List;

public record ExplorationJournalState(
        long totalKills,
        int highestRarityTier,
        String contractName,
        int contractProgress,
        int contractTarget,
        boolean contractComplete,
        List<MobEntry> mobs
) {
    public static ExplorationJournalState parse(String payload) {
        if (payload == null) return null;
        String[] parts = payload.split("\\|", -1);
        if (parts.length < 9 || !"VC1".equals(parts[0]) || !"EXPLORATION_JOURNAL".equals(parts[1])) return null;
        try {
            List<MobEntry> mobs = new ArrayList<>();
            if (!parts[8].isEmpty()) {
                for (String encoded : parts[8].split(";")) {
                    String[] fields = encoded.split(",", 4);
                    if (fields.length != 4) continue;
                    mobs.add(new MobEntry(fields[0], fields[1].replace('_', ' '),
                            "1".equals(fields[2]), Math.max(0L, Long.parseLong(fields[3]))));
                }
            }
            return new ExplorationJournalState(
                    Math.max(0L, Long.parseLong(parts[2])),
                    Math.max(0, Integer.parseInt(parts[3])),
                    parts[4].replace('_', ' '),
                    Math.max(0, Integer.parseInt(parts[5])),
                    Math.max(1, Integer.parseInt(parts[6])),
                    "1".equals(parts[7]),
                    List.copyOf(mobs));
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public int discoveredCount() {
        int count = 0;
        for (MobEntry mob : mobs) if (mob.discovered()) count++;
        return count;
    }

    public record MobEntry(String id, String name, boolean discovered, long kills) {}
}
