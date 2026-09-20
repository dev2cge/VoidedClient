package uk.loqtm.voidedclient.protocol;

/** Server-authoritative Wave 4B Nether progression snapshot. */
public record NetherCompanionState(
        int level, long xp, long needed, long kills, long variantKills, long ores,
        int variantDiscoveries, int landmarks, int landmarkTotal,
        String contract, int contractProgress, int contractTarget, boolean contractComplete,
        String bossState, long bossRespawnSeconds) {

    public static NetherCompanionState parse(String payload) {
        if (payload == null) return null;
        String[] p = payload.split("\\|", 17);
        if (p.length < 17 || !"VC1".equals(p[0]) || !"NETHER_COMPANION".equals(p[1])) return null;
        try {
            return new NetherCompanionState(
                    Integer.parseInt(p[2]), Long.parseLong(p[3]), Long.parseLong(p[4]),
                    Long.parseLong(p[5]), Long.parseLong(p[6]), Long.parseLong(p[7]),
                    Integer.parseInt(p[8]), Integer.parseInt(p[9]), Integer.parseInt(p[10]),
                    p[11], Integer.parseInt(p[12]), Integer.parseInt(p[13]), Boolean.parseBoolean(p[14]),
                    p[15], Long.parseLong(p[16]));
        } catch (RuntimeException ignored) { return null; }
    }
}
