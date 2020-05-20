package com.dannygrove.mtls;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ServerDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Server server);

    @Query("DELETE FROM servers where _id=:id")
    void delete(Long id);

    @Query("SELECT * FROM servers")
    LiveData<List<Server>> getServer();

    @Query("SELECT * FROM servers WHERE _id = :id LIMIT 1")
    Server getServer(Long id);

    @Update
    void update(Server server);
}
