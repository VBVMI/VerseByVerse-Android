package com.erpdevelopment.vbvm.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_table")
data class RStudy(
        @PrimaryKey
        val id: String,
        val bibleIndex: Int,
        val title: String
) {
}