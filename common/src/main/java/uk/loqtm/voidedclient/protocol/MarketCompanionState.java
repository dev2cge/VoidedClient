package uk.loqtm.voidedclient.protocol;

import java.util.*;

public record MarketCompanionState(
        int networkListings,int ownListings,int buyListings,int sellListings,
        String received,String spent,String fees,int transactions,List<Listing> listings) {

    public record Listing(String type,String item,int amount,String price,String server) {}

    public static MarketCompanionState parse(String payload){
        if(payload==null||!payload.startsWith("VC1|MARKET_COMPANION|"))return null;
        String[] p=payload.split("\\|",11);
        if(p.length<11)return null;
        try{
            List<Listing> rows=new ArrayList<>();
            if(!p[10].isEmpty())for(String raw:p[10].split(";")){
                String[] v=raw.split("~",5);
                if(v.length==5)rows.add(new Listing(v[0],v[1],Integer.parseInt(v[2]),v[3],v[4]));
            }
            return new MarketCompanionState(
                    Integer.parseInt(p[2]),Integer.parseInt(p[3]),Integer.parseInt(p[4]),Integer.parseInt(p[5]),
                    p[6],p[7],p[8],Integer.parseInt(p[9]),Collections.unmodifiableList(rows));
        }catch(RuntimeException ignored){return null;}
    }
}
