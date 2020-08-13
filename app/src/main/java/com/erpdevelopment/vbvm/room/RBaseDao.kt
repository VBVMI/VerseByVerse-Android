package com.erpdevelopment.vbvm.room

import androidx.room.*

@Dao
interface RBaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOver(vararg obj: T)

    @Delete
    suspend fun delete(vararg obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg obj: RStudy)

}