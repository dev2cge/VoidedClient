package uk.loqtm.voidedclient.protocol;

import java.util.*;

public record ServicesState(
        boolean enderChestUnlocked,String enderChestCost,
        boolean personalVaultUnlocked,String personalVaultCost,
        boolean vaultReady,int bossesDefeated,int bossesRequired,String nextBoss,
        List<Boss> bosses) {

    public record Boss(String id,String name,boolean defeated,boolean available) {}

    public static ServicesState parse(String payload){
        if(payload==null||!payload.startsWith("VC1|SERVICES|"))return null;
        String[] p=payload.split("\\|",11);
        if(p.length<11)return null;
        try{
            List<Boss> bosses=new ArrayList<>();
            if(!p[10].isEmpty())for(String raw:p[10].split(";")){
                String[] b=raw.split("~",4);
                if(b.length==4)bosses.add(new Boss(text(b[0]),text(b[1]),"1".equals(b[2]),"1".equals(b[3])));
            }
            return new ServicesState(
                    "1".equals(p[2]),text(p[3]),"1".equals(p[4]),text(p[5]),"1".equals(p[6]),
                    Integer.parseInt(p[7]),Integer.parseInt(p[8]),text(p[9]),Collections.unmodifiableList(bosses));
        }catch(RuntimeException ignored){return null;}
    }

    private static String text(String s){return s==null?"":s.replace('_',' ');}
}
