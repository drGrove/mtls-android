package com.dannygrove.mtls;

import android.database.Cursor;

public class Server {
    public long id;
    public String name;
    public String email;
    public String fingerprint;
    public String country;
    public String state;
    public String locality;
    public String organization_name;
    public String url;
    public String issuer;
    public String lifetime;

    public Server(String name) {
        this.name = name;
    }

    public Server() {
    }

    public static Server fromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(ServerEntry._ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_SERVER_NAME));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_EMAIL));
        String fingerprint = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_FINGERPRINT));
        String country = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_COUNTRY));
        String state = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_STATE));
        String locality = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_LOCALITY));
        String organization_name = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_ORGANIZATION_NAME));
        String url = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_URL));
        String issuer = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_ISSUER));
        String lifetime = cursor.getString(cursor.getColumnIndexOrThrow(ServerEntry.COLUMN_NAME_LIFETIME));
        Server server = new Server();
        server.name = name;
        server.email = email;
        server.fingerprint = fingerprint;
        server.country = country;
        server.state = state;
        server.locality = locality;
        server.organization_name = organization_name;
        server.issuer = issuer;
        server.lifetime = lifetime;
        return server;
    }
}
