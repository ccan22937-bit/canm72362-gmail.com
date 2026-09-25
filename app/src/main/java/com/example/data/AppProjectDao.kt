package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppProjectDao {
    @Query("SELECT * FROM app_projects ORDER BY id DESC")
    fun getAllProjects(): Flow<List<AppProject>>

    @Query("SELECT * FROM app_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): AppProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: AppProject): Long

    @Update
    suspend fun updateProject(project: AppProject)

    @Delete
    suspend fun deleteProject(project: AppProject)

    @Query("DELETE FROM app_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)
}
