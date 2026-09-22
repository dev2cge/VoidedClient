package uk.loqtm.voidedclient.protocol;

import java.util.*;

public record MissionsState(String date,String earned,List<Entry> entries) {
    public record Entry(String id,String name,String description,int progress,int target,String reward,boolean complete) {}

    public static MissionsState parse(String payload){
        if(payload==null||!payload.startsWith("VC1|MISSIONS|"))return null;
        String[] p=payload.split("\\|",5);
        if(p.length<5)return null;
        List<Entry> entries=new ArrayList<>();
        if(!p[4].isEmpty())for(String raw:p[4].split(";")){
            String[] e=raw.split("~",7);
            if(e.length<7)continue;
            try{
                entries.add(new Entry(text(e[0]),text(e[1]),text(e[2]),Integer.parseInt(e[3]),Integer.parseInt(e[4]),text(e[5]),"1".equals(e[6])));
            }catch(Exception ignored){}
        }
        return new MissionsState(text(p[2]),text(p[3]),Collections.unmodifiableList(entries));
    }
    private static String text(String s){return s==null?"":s.replace('_',' ');}
}
