package com.abdullahsajjad.i212477

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MySqliteHelper(c: Context, private val tableName: String, private val columns: Map<String, String>) : SQLiteOpenHelper(c, "myexDB", null, 1) {
    val CREATE_TABLE = "CREATE TABLE $tableName (" +
            columns.entries.joinToString { "${it.key} ${it.value}" } +
            ");"
    private val DROP_TABLE = "DROP TABLE IF EXISTS $tableName"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }

    fun insert(values: ContentValues): Long {
        val db = writableDatabase
        return db.insert(tableName, null, values)
    }

    fun read(whereClause: String? = null, whereArgs: Array<String>? = null, vararg columnNames: String): List<Map<String, String>> {
        val db = readableDatabase
        val cursor = if (whereClause != null && whereArgs != null) {
            db.query(tableName, columnNames, whereClause, whereArgs, null, null, null)
        } else {
            db.query(tableName, columnNames, null, null, null, null, null)
        }

        val result = mutableListOf<Map<String, String>>()


        while (cursor.moveToNext()) {
            val row = mutableMapOf<String, String>()
            for (columnName in columnNames) {
                val columnIndex = cursor.getColumnIndex(columnName)
                if (columnIndex != -1) {
                    row[columnName] = cursor.getString(columnIndex)
                }
            }
            result.add(row)
        }
        cursor.close()
        return result
    }
}