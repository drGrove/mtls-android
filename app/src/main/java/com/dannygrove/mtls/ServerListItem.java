package com.dannygrove.mtls;

public class ServerListItem {
    private String head;
    private String subhead;
    private long id;

    public ServerListItem(long id, String head, String subhead) {
        this.id = id;
        this.head = head;
        this.subhead = subhead;
    }

    public String getHead() {
        return this.head;
    }

    public String getSubhead() {
        return this.subhead;
    }

    public long getId() {
        return this.id;
    }
}
