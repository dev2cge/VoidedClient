package uk.loqtm.voidedclient.protocol;

public record LinkedAccountsState(String minecraftName,boolean discordLinked,String discordName,String linkCode){
    public static LinkedAccountsState parse(String payload){if(payload==null)return null;String[]p=payload.split("\\|",6);if(p.length<6||!"VC1".equals(p[0])||!"LINKED_ACCOUNTS".equals(p[1]))return null;return new LinkedAccountsState(p[2],Boolean.parseBoolean(p[3]),p[4],p[5]);}
}
