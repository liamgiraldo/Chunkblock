package me.liamgiraldo.chunkblock.util;

import java.util.UUID;

public class Invite {
    private final UUID invitee;
    private final UUID islandId;
    private final UUID ownerId;
    private final long creation;
    public Invite(UUID invitee, UUID islandId, UUID ownerId){
        this.invitee = invitee;
        this.islandId = islandId;
        this.ownerId = ownerId;
        this.creation = System.currentTimeMillis();
    }

    public UUID invitee(){ return invitee; }
    public UUID islandId(){ return islandId; }
    public UUID ownerId(){ return ownerId; }
    public long creation(){ return creation; }
}
