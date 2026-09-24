package uk.loqtm.voidedclient.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ServerListState(List<Entry> entries){
    public record Entry(String id,String name,int online,boolean current,boolean available){}
    public static ServerListState parse(String payload){
        if(payload==null||!payload.startsWith("VC1|SERVER_LIST|"))return null;
        String body=payload.substring("VC1|SERVER_LIST|".length());
        List<Entry>out=new ArrayList<>();
        try{
            if(!body.isEmpty())for(String row:body.split(";")){
                String[]v=row.split("~",-1);
                if(v.length>=4){
                    String id=v[0];
                    String name=v[1];
                    int online=Integer.parseInt(v[2]);
                    boolean current=Boolean.parseBoolean(v[3]);
                    boolean available=v.length<5||Boolean.parseBoolean(v[4]);

                    // Compatibility guard for pre-Hub-1.5.3 packets. The old reserved
                    // factions slot is currently a neutral Coming Soon destination.
                    if("factions-1".equalsIgnoreCase(id)){
                        name="Coming Soon!";
                        online=0;
                        available=false;
                    }

                    out.add(new Entry(id,name,online,current,available));
                }
            }
            return new ServerListState(Collections.unmodifiableList(out));
        }catch(RuntimeException ignored){return null;}
    }
}
