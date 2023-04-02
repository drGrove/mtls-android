package com.dannygrove.mtls;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "servers")
public class Server {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
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

    @Ignore
    public Server() {}
}
