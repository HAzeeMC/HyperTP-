package com.hazee.hypertp.model;

import java.util.UUID;

public class TPARequest {
    
    private final UUID requester;
    private final UUID target;
    private final long timestamp;
    private final boolean tpaHere;
    
    public TPARequest(UUID requester, UUID target, long timestamp, boolean tpaHere) {
        this.requester = requester;
        this.target = target;
        this.timestamp = timestamp;
        this.tpaHere = tpaHere;
    }
    
    public UUID getRequester() {
        return requester;
    }
    
    public UUID getTarget() {
        return target;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public boolean isTpaHere() {
        return tpaHere;
    }
}