package uk.loqtm.voidedclient.protocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record ServerListState(List<Entry> entries){
    public record Entry(String id,String name,int online,boolean current){}
    public static ServerListState parse(String payload){if(payload==null||!payload.startsWith("VC1|SERVER_LIST|"))return null;String body=payload.substring("VC1|SERVER_LIST|".length());List<Entry>out=new ArrayList<>();try{if(!body.isEmpty())for(String row:body.split(";")){String[]v=row.split("~",4);if(v.length==4)out.add(new Entry(v[0],v[1],Integer.parseInt(v[2]),Boolean.parseBoolean(v[3])));}return new ServerListState(Collections.unmodifiableList(out));}catch(RuntimeException ignored){return null;}}
}
