package uk.loqtm.voidedclient.protocol;

/** Server-authoritative End progression snapshot. */
public record EndCompanionState(
        int level, long xp, long needed, long kills, long variantKills, long nodes,
        int variantDiscoveries, String contract, int contractProgress, int contractTarget,
        boolean contractComplete, String eventState, int eventRemaining,
        int landmarks, int landmarkTotal, String bossState, long bossRespawnSeconds) {

    public static EndCompanionState parse(String payload) {
        if (payload == null) return null;
        String[] p = payload.split("\\|", -1);
        if (p.length < 15 || !"VC1".equals(p[0]) || !"END_COMPANION".equals(p[1])) return null;
        try {
            int landmarks = p.length > 15 ? Integer.parseInt(p[15]) : 0;
            int landmarkTotal = p.length > 16 ? Integer.parseInt(p[16]) : 0;
            String bossState = p.length > 17 ? p[17] : "undiscovered";
            long bossRespawn = p.length > 18 ? Long.parseLong(p[18]) : 0L;
            return new EndCompanionState(
                    Integer.parseInt(p[2]), Long.parseLong(p[3]), Long.parseLong(p[4]),
                    Long.parseLong(p[5]), Long.parseLong(p[6]), Long.parseLong(p[7]),
                    Integer.parseInt(p[8]), p[9], Integer.parseInt(p[10]), Integer.parseInt(p[11]),
                    Boolean.parseBoolean(p[12]), p[13], Integer.parseInt(p[14]),
                    landmarks, landmarkTotal, bossState, bossRespawn);
        } catch (RuntimeException ignored) { return null; }
    }
}
