package com.dannygrove.mtls;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserSettingsDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(UserSettings userSettings);

    @Query("DELETE FROM user_settings")
    void deleteAll();

    @Query("SELECT * FROM user_settings")
    LiveData<List<UserSettings>> getUserSettings();

    @Query("SELECT * FROM user_settings WHERE _id=:id")
    LiveData<UserSettings> getUserSettings(long id);
}
