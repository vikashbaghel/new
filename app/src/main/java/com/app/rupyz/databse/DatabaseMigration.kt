package com.app.rupyz.databse

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE IF EXISTS record_activity_table")
        database.execSQL("DROP TABLE IF EXISTS customer_address_table")
        database.execSQL("DROP TABLE IF EXISTS lead_table")
        database.execSQL("DROP TABLE IF EXISTS customer_table")
        database.execSQL("DROP TABLE IF EXISTS order_table")
        database.execSQL("DROP TABLE IF EXISTS offline_attendance")
        database.execSQL("DROP TABLE IF EXISTS order_table")

//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN subModuleType TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN jointStaffIdsInfo TEXT")

//        database.execSQL("ALTER TABLE customer_address_table ADD COLUMN isDefault INTEGER NOT NULL DEFAULT 0")

//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN batteryOptimisation INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN locationPermission INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN deviceInformation TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN batteryPercent TEXT")

//        database.execSQL("ALTER TABLE lead_table ADD COLUMN batteryOptimisation INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE lead_table ADD COLUMN locationPermissionlocationPermission INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE lead_table ADD COLUMN deviceInformation TEXT")
//        database.execSQL("ALTER TABLE lead_table ADD COLUMN batteryPercent TEXT")
//
        database.execSQL("ALTER TABLE customer_table ADD COLUMN batteryOptimisation INTEGER DEFAULT 0")
        database.execSQL("ALTER TABLE customer_table ADD COLUMN locationPermission INTEGER DEFAULT 0")
        database.execSQL("ALTER TABLE customer_table ADD COLUMN deviceInformation TEXT")
        database.execSQL("ALTER TABLE customer_table ADD COLUMN batteryPercent TEXT")
        
        
        database.execSQL("ALTER TABLE customer_feedback_list_table ADD COLUMN icon TEXT")
        database.execSQL("ALTER TABLE order_list_table ADD COLUMN icon TEXT")
        database.execSQL("ALTER TABLE customer_table ADD COLUMN checkInTime TEXT")
        database.execSQL("ALTER TABLE order_table ADD COLUMN purchaseOrderNumber TEXT")
        database.execSQL("ALTER TABLE customer_table ADD COLUMN parentsCount INTEGER DEFAULT 0")
        database.execSQL("ALTER TABLE order_table ADD COLUMN isTelephonicOrder Boolean DEFAULT false")
//
//        database.execSQL("ALTER TABLE order_table ADD COLUMN batteryOptimisation INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE order_table ADD COLUMN locationPermission INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE order_table ADD COLUMN deviceInformation TEXT")
//        database.execSQL("ALTER TABLE order_table ADD COLUMN batteryPercent TEXT")
//
//        database.execSQL("ALTER TABLE offline_attendance ADD COLUMN batteryOptimisation INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE offline_attendance ADD COLUMN locationPermission INTEGER DEFAULT 0")
//        database.execSQL("ALTER TABLE offline_attendance ADD COLUMN deviceInformation TEXT")
//        database.execSQL("ALTER TABLE offline_attendance ADD COLUMN batteryPercent TEXT")
//
//
//        database.execSQL("ALTER TABLE order_table ADD COLUMN geoAddress TEXT")
//        database.execSQL("ALTER TABLE payment_table ADD COLUMN geoAddress TEXT")
//        database.execSQL("ALTER TABLE offline_attendance ADD COLUMN geoAddress TEXT")
//        database.execSQL("ALTER TABLE lead_table ADD COLUMN activityGeoAddress TEXT")
//        database.execSQL("ALTER TABLE customer_table ADD COLUMN activityGeoAddress TEXT")


//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN geoAddress TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN timeIn TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN timeOut TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN attendanceType TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN activityType TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN startDayComments TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN endDayComments TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN startDayImagesInfo TEXT")
//        database.execSQL("ALTER TABLE record_activity_table ADD COLUMN endDayImagesInfo TEXT")
        
        
//
//        database.execSQL(
//                "CREATE TABLE IF NOT EXISTS `offline_attendance` (" +
//                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                        "`geoLocationLong` REAL, " +
//                        "`geoLocationLat` REAL, " +
//                        "`startDayImages` TEXT, " + // Assuming this will be stored as text
//                        "`startDayImagesInfo` TEXT, " + // Assuming this will be stored as text
//                        "`endDayImages` TEXT, " + // Assuming this will be stored as text
//                        "`endDayImagesInfo` TEXT, " + // Assuming this will be stored as text
//                        "`jointStaffIds` TEXT, " + // Assuming this will be stored as text
//                        "`action` TEXT, " +
//                        "`startDayComments` TEXT, " +
//                        "`endDayComments` TEXT, " +
//                        "`attendanceType` TEXT, " +
//                        "`activityType` TEXT, " +
//                        "`createdAt` TEXT, " +
//                        "`createdByName` TEXT" +
//                        ")"
//        )
    }
}