package uk.loqtm.voidedclient.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record LeaderboardState(String category,int page,int pages,int selfRank,List<Entry> entries){
    public record Entry(int rank,String name,String value){}
    public static LeaderboardState parse(String payload){
        if(payload==null)return null;String[]p=payload.split("\\|",7);if(p.length<7||!"VC1".equals(p[0])||!"LEADERBOARD".equals(p[1]))return null;
        try{List<Entry>out=new ArrayList<>();if(!p[6].isEmpty())for(String row:p[6].split(";")){String[]v=row.split("~",3);if(v.length==3)out.add(new Entry(Integer.parseInt(v[0]),v[1],v[2]));}
            return new LeaderboardState(p[2],Integer.parseInt(p[3]),Integer.parseInt(p[4]),Integer.parseInt(p[5]),Collections.unmodifiableList(out));}
        catch(RuntimeException ignored){return null;}
    }
}
