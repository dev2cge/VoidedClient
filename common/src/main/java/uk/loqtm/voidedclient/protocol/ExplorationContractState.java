package uk.loqtm.voidedclient.protocol;

public record ExplorationContractState(
        String type,
        String name,
        String description,
        int progress,
        int target,
        boolean complete,
        long rewardXp,
        String rewardItem,
        int rewardAmount,
        int minimumRarityTier,
        String date
) {
    public static ExplorationContractState parse(String payload) {
        if (payload == null) return null;
        String[] parts=payload.split("\\|",-1);
        if(parts.length<13||!"VC1".equals(parts[0])||!"EXPLORATION_CONTRACT".equals(parts[1]))return null;
        try {
            return new ExplorationContractState(text(parts[2]),text(parts[3]),text(parts[4]),
                    Math.max(0,Integer.parseInt(parts[5])),Math.max(1,Integer.parseInt(parts[6])),"1".equals(parts[7]),
                    Math.max(0L,Long.parseLong(parts[8])),text(parts[9]),Math.max(0,Integer.parseInt(parts[10])),
                    Math.max(1,Integer.parseInt(parts[11])),parts[12]);
        } catch(RuntimeException ignored){return null;}
    }

    public double progressRatio(){return complete?1D:Math.min(1D,progress/(double)Math.max(1,target));}
    private static String text(String value){return value==null?"":value.replace('_',' ');}
}
