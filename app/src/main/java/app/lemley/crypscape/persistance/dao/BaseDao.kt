package app.lemley.crypscape.persistance.dao

import androidx.annotation.WorkerThread
import androidx.room.*

@Dao
abstract class BaseDao<in T> {

    @WorkerThread
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(t: T): Long

    @WorkerThread
    @Delete
    abstract fun delete(type : T)

    @WorkerThread
    @Update
    abstract fun update(type : T)

}