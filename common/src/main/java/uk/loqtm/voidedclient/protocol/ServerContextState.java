package uk.loqtm.voidedclient.protocol;

/** Backend identity/capability snapshot. Updated whenever Velocity moves the player. */
public record ServerContextState(String serverId, String serverType, String edition, boolean rpgEnabled, boolean gameplayEnabled) {
    private static volatile ServerContextState current = new ServerContextState("unknown", "UNKNOWN", "unknown", false, false);
    private static volatile boolean known;

    public static ServerContextState parse(String message) {
        if(message==null||!message.startsWith("VC1|SERVER_CONTEXT|"))return null;
        String[] p=message.split("\\|",-1);
        if(p.length<7)return null;
        return new ServerContextState(p[2],p[3],p[4],"1".equals(p[5])||Boolean.parseBoolean(p[5]),"1".equals(p[6])||Boolean.parseBoolean(p[6]));
    }
    public static void update(ServerContextState state){if(state!=null){current=state;known=true;}}
    public static void awaitingBackend(){known=false;current=new ServerContextState("switching","UNKNOWN","unknown",false,false);}
    public static void clear(){awaitingBackend();}
    public static boolean isKnown(){return known;}
    public static boolean rpgEnabled(){return known&&current.rpgEnabled;}
    public static boolean gameplayEnabled(){return known&&current.gameplayEnabled;}
    public static ServerContextState current(){return current;}
}
