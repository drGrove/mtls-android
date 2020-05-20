package com.dannygrove.mtls;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_settings")
public class UserSettings {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "_id")
    public Long id;

    @ColumnInfo(name = "organization_name")
    public String organizationName;

    public String email;
    public String state;
    public String country;

    public UserSettings() {}
}
