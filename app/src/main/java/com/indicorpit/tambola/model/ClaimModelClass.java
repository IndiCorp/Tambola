package com.indicorpit.tambola.model;

public class ClaimModelClass {
    public ClaimModelClass ( String claimTicket , String claimType ,String originalTicket,String claimStatus,String playerId,String ticketId) {
        this.claimTicket = claimTicket;
        this.claimType = claimType;
        this.originalTicket=originalTicket;
        this.claimStatus=claimStatus;
        this.playerId=playerId;
        this.ticketId=ticketId;
    }

    public String getClaimStatus () {
        return claimStatus;
    }

    public void setClaimStatus ( String claimStatus ) {
        this.claimStatus = claimStatus;
    }

    private String claimTicket;
    private String claimStatus;

    public String getTicketId () {
        return ticketId;
    }

    public void setTicketId ( String ticketId ) {
        this.ticketId = ticketId;
    }

    private String ticketId;

    public String getPlayerId () {
        return playerId;
    }

    public void setPlayerId ( String playerId ) {
        this.playerId = playerId;
    }

    private String playerId;

    public ClaimModelClass () {
    }

    public String getOriginalTicket () {
        return originalTicket;
    }

    public void setOriginalTicket ( String originalTicket ) {
        this.originalTicket = originalTicket;
    }

    private String originalTicket;

    public String getClaimTicket () {
        return claimTicket;
    }

    public void setClaimTicket ( String claimTicket ) {
        this.claimTicket = claimTicket;
    }

    public String getClaimType () {
        return claimType;
    }

    public void setClaimType ( String claimType ) {
        this.claimType = claimType;
    }

    private String  claimType;
}
