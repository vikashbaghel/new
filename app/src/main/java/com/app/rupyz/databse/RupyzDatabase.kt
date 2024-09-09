package com.app.rupyz.databse

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.rupyz.databse.customer.CustomerDao
import com.app.rupyz.databse.customer.CustomerTypeConverters
import com.app.rupyz.databse.customer.DeviceInfoTypeConverter
import com.app.rupyz.databse.customer.IntListConverter
import com.app.rupyz.databse.expense.ExpenseDao
import com.app.rupyz.databse.lead.LeadDao
import com.app.rupyz.databse.lead.LeadTypeConverters
import com.app.rupyz.databse.order.OrderDao
import com.app.rupyz.databse.order.OrderTypeConverter
import com.app.rupyz.databse.order.PicMapListTypeConverter
import com.app.rupyz.databse.payments.PaymentDao
import com.app.rupyz.databse.payments.PaymentTypeConverter
import com.app.rupyz.databse.product.ProductDao
import com.app.rupyz.databse.product.ProductListTypeConverters
import com.app.rupyz.databse.staff.StaffDao
import com.app.rupyz.databse.staff.StaffInfoListConverter
import com.app.rupyz.databse.staff.StringListTypeConverter
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.AssignedRoleItem
import com.app.rupyz.model_kt.BrandDataItem
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.LeadCategoryDataItem
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.OrgBeatModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.dashboard.DashboardData
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.model_kt.order.sales.StaffData

@Database(
        entities = [
            DashboardData::class,
            ProductList::class,
            BrandDataItem::class,
            AllCategoryResponseModel::class,
            CustomerData::class,
            CustomerTypeDataItem::class,
            OrderData::class,
            StaffData::class,
            RecordPaymentData::class,
            LeadLisDataItem::class,
            ExpenseTrackerDataItem::class,
            ExpenseDataItem::class,
            CustomerAddressDataItem::class,
            AssignedRoleItem::class,
            LeadCategoryDataItem::class,
            DispatchedOrderModel::class,
            CustomerFeedbackStringItem::class,
            CustomerFollowUpDataItem::class,
            OrgBeatModel::class,
            AddCheckInOutModel::class
        ],
        version = 3,
        exportSchema = false
)
@TypeConverters(
        ProductListTypeConverters::class,
        IntListConverter::class,
        OrderTypeConverter::class,
        PaymentTypeConverter::class,
        StringListTypeConverter::class,
        PicMapListTypeConverter::class,
        CustomerTypeConverters::class,
        LeadTypeConverters::class,
        StaffInfoListConverter::class,
        DeviceInfoTypeConverter::class,
        NameAndValueSetInfoListConverter::class
)
abstract class RupyzDatabase : RoomDatabase() {

    abstract fun dashboardDao(): DashboardDao
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun orderDao(): OrderDao
    abstract fun staffDao(): StaffDao
    abstract fun paymentDao(): PaymentDao
    abstract fun leadDao(): LeadDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: RupyzDatabase? = null

        fun getDatabase(context: Context): RupyzDatabase {
            val tempInstance = INSTANCE

//            val factory = SupportFactory(SQLiteDatabase.getBytes(passphrase.toCharArray()))
            //  check if there is any existing instance is present for our room database
            //  if there exist an existing instance then we'll return that instance
            if (tempInstance != null) {
                return tempInstance
            }

            //  If there is no any instance present for our database then we'll create a new instance
            //  WHY SYNCHRONIZED ?? --> Because everything inside the synchronized block will be protected
            //  by concurrent execution on multiple threads
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RupyzDatabase::class.java,
                        "rupyz_database"
                ).addMigrations(MIGRATION_2_3).build()

                instance.openHelper.writableDatabase     //<<<<< FORCE OPEN
                INSTANCE = instance
                return instance
            }
        }
    }
}