package uk.loqtm.voidedclient.protocol;

public record CompanionHomeState(String player, String server, int online, String balance, int rpgLevel, boolean discordLinked) {
    public static CompanionHomeState parse(String payload) {
        if (payload == null) return null;
        String[] p=payload.split("\\|",8);
        if (p.length<8||!"VC1".equals(p[0])||!"COMPANION_HOME".equals(p[1]))return null;
        try{return new CompanionHomeState(p[2],p[3],Integer.parseInt(p[4]),p[5],Integer.parseInt(p[6]),Boolean.parseBoolean(p[7]));}
        catch(RuntimeException ignored){return null;}
    }
}
