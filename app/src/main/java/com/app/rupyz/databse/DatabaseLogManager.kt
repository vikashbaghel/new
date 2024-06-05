package com.app.rupyz.databse

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.app.rupyz.generic.helper.DateFormatHelper
import com.app.rupyz.generic.model.profile.product.ProductInfoModel
import com.app.rupyz.generic.model.profile.product.ProductList
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharePrefConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.model_kt.AddCheckInOutModel
import com.app.rupyz.model_kt.AddExpenseResponseModel
import com.app.rupyz.model_kt.AddLeadResponseModel
import com.app.rupyz.model_kt.AddTotalExpenseResponseModel
import com.app.rupyz.model_kt.AllCategoryInfoModel
import com.app.rupyz.model_kt.AllCategoryResponseModel
import com.app.rupyz.model_kt.AssignRolesResponseModel
import com.app.rupyz.model_kt.AssignedRoleItem
import com.app.rupyz.model_kt.BranListResponseModel
import com.app.rupyz.model_kt.BrandDataItem
import com.app.rupyz.model_kt.CheckInOutListResponseModel
import com.app.rupyz.model_kt.CustomerAddressApiResponseModel
import com.app.rupyz.model_kt.CustomerAddressDataItem
import com.app.rupyz.model_kt.CustomerAddressListResponseModel
import com.app.rupyz.model_kt.CustomerFeedbackListResponseModel
import com.app.rupyz.model_kt.CustomerFeedbackStringItem
import com.app.rupyz.model_kt.CustomerFollowUpDataItem
import com.app.rupyz.model_kt.CustomerFollowUpListResponseModel
import com.app.rupyz.model_kt.CustomerFollowUpResponseModel
import com.app.rupyz.model_kt.CustomerTypeDataItem
import com.app.rupyz.model_kt.CustomerTypeResponseModel
import com.app.rupyz.model_kt.DispatchedOrderDetailsModel
import com.app.rupyz.model_kt.DispatchedOrderModel
import com.app.rupyz.model_kt.ExpenseDataItem
import com.app.rupyz.model_kt.ExpenseResponseModel
import com.app.rupyz.model_kt.ExpenseTrackerDataItem
import com.app.rupyz.model_kt.ExpenseTrackerResponseModel
import com.app.rupyz.model_kt.GenericResponseModel
import com.app.rupyz.model_kt.LeadCategoryDataItem
import com.app.rupyz.model_kt.LeadCategoryListResponseModel
import com.app.rupyz.model_kt.LeadLisDataItem
import com.app.rupyz.model_kt.LeadListResponseModel
import com.app.rupyz.model_kt.NameAndIdSetInfoModel
import com.app.rupyz.model_kt.OrgBeatListResponseModel
import com.app.rupyz.model_kt.OrgBeatModel
import com.app.rupyz.model_kt.ProductDetailsResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerAddResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerData
import com.app.rupyz.model_kt.order.customer.CustomerDeleteResponseModel
import com.app.rupyz.model_kt.order.customer.CustomerInfoModel
import com.app.rupyz.model_kt.order.customer.UpdateCustomerInfoModel
import com.app.rupyz.model_kt.order.dashboard.DashboardData
import com.app.rupyz.model_kt.order.dashboard.DashboardIndoModel
import com.app.rupyz.model_kt.order.order_history.Address
import com.app.rupyz.model_kt.order.order_history.CreateOrderResponseModel
import com.app.rupyz.model_kt.order.order_history.OrderData
import com.app.rupyz.model_kt.order.order_history.OrderDetailsInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderInfoModel
import com.app.rupyz.model_kt.order.order_history.OrderUpdateResponseModel
import com.app.rupyz.model_kt.order.payment.PaymentRecordResponseModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentData
import com.app.rupyz.model_kt.order.payment.RecordPaymentDetailModel
import com.app.rupyz.model_kt.order.payment.RecordPaymentInfoModel
import com.app.rupyz.model_kt.order.sales.StaffData
import com.app.rupyz.model_kt.order.sales.StaffInfoModel
import com.app.rupyz.model_kt.order.sales.StaffListWithCustomerMappingModel
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar


class DatabaseLogManager {
    private val pageSize = 30
    private val successMessage = "Data saved successfully"
    private val successDeleteMessage = "Data deleted successfully"
    private val successUpdateMessage = "Data updated successfully"

    companion object {
        private lateinit var databaseLogManager: DatabaseLogManager
        private var database: RupyzDatabase? = null


        fun getInstance(): DatabaseLogManager {
            if (!::databaseLogManager.isInitialized) {
                synchronized(this) {
                    databaseLogManager = DatabaseLogManager()
                }
            }
            return databaseLogManager
        }
    }

    fun initializedDB(context: Context): RupyzDatabase? {
        if (database == null) {
            synchronized(this) {
                database = RupyzDatabase.getDatabase(context)
            }
        }
        return database
    }

    // Deleting all table at once
    fun deleteAllDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database?.productDao()?.deleteAllBrand()
                database?.productDao()?.deleteAllCategory()
                database?.productDao()?.deleteAllCategory()
                database?.productDao()?.deleteAllProduct()

                database?.customerDao()?.deleteAllCustomer()
                database?.customerDao()?.deleteAllCustomerType()
                database?.customerDao()?.deleteAllCustomerAddress()
                database?.customerDao()?.deleteAllActivity()

                database?.orderDao()?.deleteAllOrder()
                database?.orderDao()?.deleteAllOrderDispatchList()

                database?.staffDao()?.deleteAllStaff()
                database?.staffDao()?.deleteAllStaffRoles()

                database?.expenseDao()?.deleteAllExpense()
                database?.expenseDao()?.deleteAllExpenseList()

                database?.paymentDao()?.deleteAllPayment()

                database?.leadDao()?.deleteAllLead()
                database?.leadDao()?.deleteAllLeadCategory()
            }
        }
    }

    fun insertDashBoardData(data: DashboardData) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.dashboardDao().deleteDashboardData()
                database!!.dashboardDao().insertDashboardData(data)
            }
        }
    }


    // Insert all list into database
    fun insertCustomerData(data: List<CustomerData>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.customerDao().insertCustomerListData(data)
            }
        }
    }

    fun insetOrderData(data: List<OrderData>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.orderDao().insertOrderListData(data)
            }
        }
    }

    fun insertOrderDispatchListData(data: List<DispatchedOrderModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.orderDao().insertOrderDispatchListData(data)
            }
        }
    }

    fun insertBeatList(data: List<OrgBeatModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.dashboardDao().insertBeatData(data)
            }
        }
    }


    fun insertStaffData(data: List<StaffData>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.staffDao().insertStaffListData(data)
            }
        }
    }

    fun insertStaffRoleData(data: List<AssignedRoleItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.staffDao().insertStaffRoleData(data)
            }
        }
    }

    fun insertCustomerTypeData(data: List<CustomerTypeDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.customerDao().insertCustomerTypeListData(data)
            }
        }
    }

    fun addOfflineActivity(liveData: MutableLiveData<CustomerFollowUpResponseModel>?, feedbackModel: CustomerFollowUpDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = CustomerFollowUpResponseModel()
                model.error = false
                database!!.customerDao().insertCustomerFeedbackData(feedbackModel)
                model.message = successMessage
                liveData?.postValue(model)
            }
        }
    }

    fun addOfflineAttendance(liveData: MutableLiveData<GenericResponseModel>?, feedbackModel: AddCheckInOutModel) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = GenericResponseModel()
                model.error = false
                database!!.customerDao().insertOfflineAttendance(feedbackModel)
                model.message = successMessage
                liveData?.postValue(model)
            }
        }
    }

    fun insertAddressList(data: List<CustomerAddressDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.customerDao().insertCustomerAddress(data)
            }
        }
    }

    fun insertPaymentData(data: List<RecordPaymentData>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.paymentDao().insertPaymentListData(data)
            }
        }
    }

    fun insertLeadCategoryData(data: List<LeadCategoryDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.leadDao().insertLeadCategoryData(data)
            }
        }
    }

    fun insertLeadData(data: List<LeadLisDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.leadDao().insertLeadData(data)
            }
        }
    }

    fun insertExpenseHeadList(data: List<ExpenseTrackerDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.expenseDao().insertExpenseHeadData(data)
            }
        }
    }

    fun insertExpenseList(data: List<ExpenseDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.expenseDao().insertExpenseListData(data)
            }
        }
    }

    fun insertProductListData(productList: ArrayList<ProductList>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.productDao().insertProductListData(productList)
            }
        }
    }

    fun insertBrandListData(source: ArrayList<BrandDataItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.productDao().insertBrandListData(source)
            }
        }
    }

    fun insertCategoryListData(source: ArrayList<AllCategoryResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.productDao().insertCategoryData(source)
            }
        }
    }

    fun getFollowUpList(liveData: MutableLiveData<CustomerFeedbackListResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = CustomerFeedbackListResponseModel()
                val feedbackList = ArrayList<CustomerFeedbackStringItem>()
                model.error = false
                if (database!!.dashboardDao().getFeedbackListData().isNotEmpty()) {
                    feedbackList.addAll(database!!.dashboardDao().getFeedbackListData())
                    model.data = feedbackList
                }

                liveData.postValue(model)
            }
        }
    }

    fun insetFeedbackList(model: List<CustomerFeedbackStringItem>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.dashboardDao().deleteFeedbackListData()
                database!!.dashboardDao().insertFeedbackListData(model)
            }
        }
    }

    fun getDashBoardData(dashboardLiveData: MutableLiveData<DashboardIndoModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = DashboardIndoModel()
                model.error = false
                if (database!!.dashboardDao().getDashboardData().isEmpty().not()) {
                    model.data = database!!.dashboardDao().getDashboardData()[0]
                }
                dashboardLiveData.postValue(model)
            }
        }
    }

    fun getProductListData(productLiveData: MutableLiveData<ProductInfoModel>, name: String, brand: List<String?>, category: String, customerId: Int?, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            var filterCategory: ArrayList<String?> = ArrayList()
            val stringBuilder = StringBuilder()

            brand.forEachIndexed { index, s ->
                stringBuilder.append(s)

                if (index < brand.size - 1) {
                    stringBuilder.append(",")
                }
            }

            var applyFilterForCategory = false

            if (customerId != null &&
                    SharedPref.getInstance().getBoolean(SharePrefConstant.ENABLE_CUSTOMER_CATEGORY_MAPPING,
                            false)) {
                if (category.isEmpty()) {
                    applyFilterForCategory = true
                    val customer = database!!.customerDao().getCustomerIdData(customerId)
                    if (customer.isNotEmpty()) {
                        val categoryListAssignToCustomer: List<Int?> = customer[0].productCategory
                                ?: ArrayList()

                        if (categoryListAssignToCustomer.isNotEmpty()) {
                            val categoryList = database!!.productDao().getCategoryData(name) as ArrayList<AllCategoryResponseModel>

                            val filterCategoryList = categoryList.filter { it.id in categoryListAssignToCustomer }

                            filterCategoryList.forEach {
                                filterCategory.add(it.name)
                            }
                        } else {
                            applyFilterForCategory = false
                            filterCategory = ArrayList()
                        }
                    }
                } else {
                    filterCategory.add(category)
                }
            } else {
                filterCategory = ArrayList()
            }

            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = ProductInfoModel()
                model.error = false

                model.data = if (applyFilterForCategory) {
                    database!!.productDao().getProductDataWIthCategoryMapping(name, stringBuilder.toString(),
                            filterCategory, pageSize, offset) as ArrayList<ProductList>
                } else {
                    database!!.productDao().getProductData(name, stringBuilder.toString(),
                            category, pageSize, offset) as ArrayList<ProductList>
                }

                productLiveData.postValue(model)
            }
        }
    }

    fun getOfflineProductDetailsById(liveData: MutableLiveData<ProductDetailsResponseModel>, id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = ProductDetailsResponseModel()
                model.error = false
                liveData.postValue(model)
            }
        }
    }

    fun getBrandListData(brandListLiveData: MutableLiveData<BranListResponseModel>, name: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = BranListResponseModel()
                model.error = false
                model.data = database!!.productDao().getBrandData(name, pageSize, offset) as ArrayList<BrandDataItem>

                brandListLiveData.postValue(model)
            }
        }
    }

    fun getCategoryListData(categoryLiveData: MutableLiveData<AllCategoryInfoModel>, customerId: Int?, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = AllCategoryInfoModel()

                model.error = false
                val categoryList = database!!.productDao().getCategoryData(name) as ArrayList<AllCategoryResponseModel>

                if (customerId != null && customerId != 0) {
                    val customer = database!!.customerDao().getCustomerIdData(customerId)
                    if (customer.isNotEmpty()) {
                        val categoryListAssignToCustomer: List<Int?> = customer[0].productCategory
                                ?: ArrayList()

                        val filterCategoryList = categoryList.filter { it.id in categoryListAssignToCustomer }
                        model.data = filterCategoryList as java.util.ArrayList<AllCategoryResponseModel>?
                    }
                } else {
                    model.data = categoryList
                }

                categoryLiveData.postValue(model)
            }
        }
    }

    fun getProductCategoryListAssignToCustomer(categoryLiveData: MutableLiveData<AllCategoryInfoModel>, customerId: Int?, name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = AllCategoryInfoModel()

                model.error = false
                val categoryList = database!!.productDao().getCategoryData(name) as ArrayList<AllCategoryResponseModel>

                if (customerId != null && customerId != 0) {
                    val customer = database!!.customerDao().getCustomerIdData(customerId)
                    if (customer.isNotEmpty()) {
                        val categoryListAssignToCustomer: List<Int?> = customer[0].productCategory
                                ?: ArrayList()

                        for (category in categoryList) {
                            if (categoryListAssignToCustomer.contains(category.id)) {
                                category.isSelected = true
                            }
                        }
                        model.data = categoryList
                    }
                } else {
                    model.data = categoryList
                }

                categoryLiveData.postValue(model)
            }
        }
    }

    fun getOfflineCustomerList(liveData: MutableLiveData<CustomerInfoModel>, customerParentID: Int?, name: String, customerLevel: String, filterCustomerType: ArrayList<CustomerTypeDataItem>, sortByOrder: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            val stringBuilder = StringBuilder()

            var sortingOrder = AppConstant.SORTING_LEVEL_DESCENDING
            var sortBy = ""
            if (sortByOrder.isNotEmpty()) {
                sortBy = AppConstant.NAME
                sortingOrder = sortByOrder
            } else {
                sortBy = "createdAt"
            }

            filterCustomerType.forEachIndexed { index, s ->
                stringBuilder.append(s.name)
                if (index < filterCustomerType.size) {
                    stringBuilder.append(",")
                }
            }

            val filter = stringBuilder.toString()

            val offset = (page - 1) * pageSize

            val queryString = "SELECT * FROM customer_table WHERE name LIKE '%' || '$name' || '%' AND customerLevel LIKE '%' || '$customerLevel' || '%' AND (customerParent = $customerParentID OR $customerParentID IS NULL) AND ('$filter' IS NULL OR '$filter' = '' OR customer_type IN ('$filter')) ORDER BY $sortBy COLLATE NOCASE $sortingOrder LIMIT $pageSize OFFSET $offset"

            val query: SupportSQLiteQuery = SimpleSQLiteQuery(queryString)

            if (database != null) {
                val model = CustomerInfoModel()

                model.error = false

                val customerList = ArrayList<CustomerData>()

                customerList.addAll(database!!.customerDao().getCustomerData(query) as ArrayList<CustomerData>)

                model.data = customerList

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineCustomerDetailsById(liveData: MutableLiveData<UpdateCustomerInfoModel>, customerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = UpdateCustomerInfoModel()

                model.error = false
                val customer = database!!.customerDao().getCustomerIdData(customerId)
                if (customer.isNotEmpty()) {
                    model.data = customer[0]
                }
                liveData.postValue(model)
            }
        }
    }

    fun getOfflineCustomerTypeList(liveData: MutableLiveData<CustomerTypeResponseModel>, name: String, currentPage: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (currentPage - 1) * pageSize

            if (database != null) {
                val model = CustomerTypeResponseModel()

                model.error = false
                model.data = database!!.customerDao().getCustomerTypeData(name, currentPage, pageSize) as ArrayList<CustomerTypeDataItem>

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineOrderList(liveData: MutableLiveData<OrderInfoModel>, status: String, fullFilledById: Int?, customerLevel: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = OrderInfoModel()
                model.error = false
                model.data = database!!.orderDao().getOrderList(status, fullFilledById, customerLevel, pageSize, offset) as ArrayList<OrderData>

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineOrderDetails(liveData: MutableLiveData<OrderDetailsInfoModel>, orderId: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            if (database != null) {
                val model = OrderDetailsInfoModel()
                model.error = false
                if (database!!.orderDao().getOrderDetailsById(orderId).isEmpty().not()) {
                    model.data = database!!.orderDao().getOrderDetailsById(orderId)[0]
                }

                liveData.postValue(model)
            }
        }
    }

    fun deleteOfflineOrderData(liveData: MutableLiveData<OrderUpdateResponseModel>, orderId: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            if (database != null) {
                database!!.orderDao().deleteOfflineOrderData(orderId)
                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery("orders", AppConstant.MINUS_COUNT))
                val model = OrderUpdateResponseModel()
                model.error = false
                model.message = successDeleteMessage

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineCustomerOrderListById(liveData: MutableLiveData<OrderInfoModel>, customerId: Int, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize
            if (database != null) {
                val model = OrderInfoModel()
                model.error = false
                val orderList = database!!.orderDao().getOrderListWithCustomerMapping(customerId, pageSize, offset) as ArrayList<OrderData>

                model.data = orderList
                liveData.postValue(model)
            }
        }
    }

    fun getOfflinePaymentList(liveData: MutableLiveData<RecordPaymentInfoModel>, status: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = RecordPaymentInfoModel()
                model.error = false
                model.data = database!!.paymentDao().getPaymentList(status, pageSize, offset) as ArrayList<RecordPaymentData>

                liveData.postValue(model)
            }
        }
    }

    fun getAddressListForCustomer(liveData: MutableLiveData<CustomerAddressListResponseModel>, customerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = CustomerAddressListResponseModel()
                model.error = false
                model.data = database!!.customerDao().getAddressListForCustomer(customerId) as ArrayList<CustomerAddressDataItem>

                liveData.postValue(model)
            }
        }
    }

    fun addAddressForOfflineCustomer(liveData: MutableLiveData<CustomerAddressApiResponseModel>, address: CustomerAddressDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.customerDao().saveOfflineCustomerAddress(address)

                val model = CustomerAddressApiResponseModel()
                model.error = false
                liveData.postValue(model)
            }
        }
    }

    fun getOfflineStaffList(liveData: MutableLiveData<StaffInfoModel>, role: String, name: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = StaffInfoModel()
                model.error = false
                val staffList = database!!.staffDao().getStaffList(name, pageSize, offset) as ArrayList<StaffData>

                if (role.isNotEmpty()) {
                    val filterStaffList = ArrayList<StaffData>()
                    staffList.forEach { staffData ->
                        if (staffData.roles.isNullOrEmpty().not() && staffData.roles!!.contains(role)) {
                            filterStaffList.add(staffData)
                        }
                    }

                    model.data = filterStaffList
                } else {
                    model.data = staffList
                }

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineStaffListForAddCustomer(liveData: MutableLiveData<StaffListWithCustomerMappingModel>, name: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = StaffListWithCustomerMappingModel()
                model.error = false

                val staffList = database!!.staffDao().getStaffList(name, pageSize, offset) as ArrayList<StaffData>

                val filterList = ArrayList<NameAndIdSetInfoModel>()
                staffList.forEach {
                    val idModel = NameAndIdSetInfoModel()
                    idModel.id = it.id
                    idModel.name = it.name
                    filterList.add(idModel)
                }

                model.data = filterList

                liveData.postValue(model)
            }
        }
    }

    fun getStaffRoles(liveData: MutableLiveData<AssignRolesResponseModel>, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize

            if (database != null) {
                val model = AssignRolesResponseModel()
                model.error = false
                model.data = database!!.staffDao().getStaffRolesList(pageSize, offset) as ArrayList<AssignedRoleItem>

                liveData.postValue(model)
            }
        }
    }


    fun getLeadCategoryList(liveData: MutableLiveData<LeadCategoryListResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {

            if (database != null) {
                val model = LeadCategoryListResponseModel()
                model.error = false
                model.data = database!!.leadDao().getLeadCategoryList() as ArrayList<LeadCategoryDataItem>

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineLeadList(liveData: MutableLiveData<LeadListResponseModel>, name: String, category: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize
            if (database != null) {
                val model = LeadListResponseModel()
                model.error = false
                model.data = database!!.leadDao().getLeadList(name, category, pageSize, offset) as ArrayList<LeadLisDataItem>

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineTotalExpenseList(liveData: MutableLiveData<ExpenseTrackerResponseModel>, status: String, page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val offset = (page - 1) * pageSize
            var filterStatus = ""
            if (status != "All") {
                filterStatus = status
            }

            if (database != null) {
                val model = ExpenseTrackerResponseModel()
                model.error = false
                model.data = database!!.expenseDao().getExpenseHeadList(filterStatus, pageSize, offset) as ArrayList<ExpenseTrackerDataItem>

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineExpenseList(liveData: MutableLiveData<ExpenseResponseModel>, rtId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = ExpenseResponseModel()
                model.error = false
                model.data = database!!.expenseDao().getExpenseList(rtId) as ArrayList<ExpenseDataItem>

                liveData.postValue(model)
            }
        }
    }

    fun getOfflineExpenseHeadDetails(liveData: MutableLiveData<AddTotalExpenseResponseModel>, rtId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = AddTotalExpenseResponseModel()
                model.error = false
                val details = database!!.expenseDao().getExpenseHeadDetail(rtId) as ArrayList<ExpenseTrackerDataItem>

                if (details.isNotEmpty()) {
                    model.data = details[0]
                }
                liveData.postValue(model)
            }
        }
    }

    fun getOfflineExpenseDetails(liveData: MutableLiveData<AddExpenseResponseModel>, remId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                val model = AddExpenseResponseModel()
                model.error = false
                val details = database!!.expenseDao().getExpenseDetail(remId) as ArrayList<ExpenseDataItem>

                if (details.isNotEmpty()) {
                    model.data = details[0]
                }
                liveData.postValue(model)
            }
        }
    }

    fun getOfflineOrderDispatchedDetails(liveData: MutableLiveData<DispatchedOrderDetailsModel>, orderId: Int, dispatchId: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            if (database != null) {
                val model = DispatchedOrderDetailsModel()
                model.error = false
                if (database!!.orderDao().getOrderDispatchDetailsById(orderId, dispatchId).isEmpty().not()) {
                    model.data = database!!.orderDao().getOrderDispatchDetailsById(orderId, dispatchId)[0]
                }

                liveData.postValue(model)
            }
        }
    }

    fun addOrderToDatabase(liveData: MutableLiveData<CreateOrderResponseModel>, cartListResponseModel: OrderData) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.orderDao().addOrderToDatabase(cartListResponseModel)
                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery("orders", AppConstant.ADD_COUNT))
            }
            val createOrderResponseModel = CreateOrderResponseModel()
            createOrderResponseModel.error = false
            createOrderResponseModel.message = successMessage
            liveData.postValue(createOrderResponseModel)
        }
    }

    fun updateOfflineOrder(liveData: MutableLiveData<OrderDetailsInfoModel>, model: OrderData?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.orderDao().updateOrder(model)
            }
            val createOrderResponseModel = OrderDetailsInfoModel()
            createOrderResponseModel.error = false
            createOrderResponseModel.message = successMessage
            liveData.postValue(createOrderResponseModel)
        }
    }

    fun saveOfflineCustomer(liveData: MutableLiveData<CustomerAddResponseModel>, addCustomerModel: CustomerData) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.customerDao().saveOfflineCustomer(addCustomerModel)
                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery("customers", AppConstant.ADD_COUNT))
            }
            val addResponseModel = CustomerAddResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun updateOfflineCustomer(liveData: MutableLiveData<CustomerAddResponseModel>, customerData: CustomerData, customerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            customerData.id = customerId
            customerData.isSyncedToServer = false
            customerData.createdAt = DateFormatHelper.convertDateToIsoUTCFormat(Calendar.getInstance().time)

            val customer = database!!.customerDao().getCustomerIdData(customerId)[0]

            var productCategory = customer.productCategory

            if (customerData.selectCategory != null) {

                if (customerData.selectCategory?.addSet.isNullOrEmpty().not()) {
                    if (productCategory.isNullOrEmpty().not()) {
                        for (index in customerData.selectCategory?.addSet!!) {
                            if (productCategory!!.contains(index).not()) {
                                productCategory.add(index)
                            }
                        }
                    } else {
                        productCategory = ArrayList()
                        productCategory.addAll(customerData.selectCategory?.addSet!!)
                    }
                }

                if (customerData.selectCategory?.removeSet.isNullOrEmpty().not()) {
                    if (productCategory.isNullOrEmpty().not()) {
                        for (index in customerData.selectCategory?.removeSet!!) {
                            if (productCategory!!.contains(index)) {
                                productCategory.remove(index)
                            }
                        }
                    }
                }

                customerData.productCategory = productCategory
            }

            if (database != null) {
                database!!.customerDao().updateOfflineCustomer(customerData)
            }
            val addResponseModel = CustomerAddResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successUpdateMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun deleteOfflineCustomer(liveData: MutableLiveData<CustomerDeleteResponseModel>?, customerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.customerDao().deleteOfflineCustomerColumn(customerId)
                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery(
                        "customers", AppConstant.MINUS_COUNT))
            }
            val addResponseModel = CustomerDeleteResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successDeleteMessage
            liveData?.postValue(addResponseModel)
        }
    }

    fun createOfflineLead(liveData: MutableLiveData<AddLeadResponseModel>, jsonObject: LeadLisDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.leadDao().saveOfflineLead(jsonObject)

                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery("leadCount", AppConstant.ADD_COUNT))
            }
            val addResponseModel = AddLeadResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun updateOfflineLeadDetails(liveData: MutableLiveData<AddLeadResponseModel>, jsonObject: LeadLisDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.leadDao().updateOfflineLead(jsonObject)
            }
            val addResponseModel = AddLeadResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successUpdateMessage
            liveData.postValue(addResponseModel)
        }
    }


    fun deleteOfflineLead(liveData: MutableLiveData<AddLeadResponseModel>?, leadId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.leadDao().deleteOfflineLead(leadId)
                database!!.dashboardDao().updateDashboardData(
                        getUpdateDashboardQuery("leadCount",
                                AppConstant.MINUS_COUNT))
            }
            val addResponseModel = AddLeadResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successUpdateMessage
            liveData?.postValue(addResponseModel)
        }
    }

    fun getOfflineLeadDetails(liveData: MutableLiveData<AddLeadResponseModel>, leadId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val addResponseModel = AddLeadResponseModel()
            if (database != null) {
                val details = database!!.leadDao().getLeadList(leadId)
                if (details.isNotEmpty()) {
                    addResponseModel.data = details[0]
                }
            }
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun addOfflinePaymentData(liveData: MutableLiveData<PaymentRecordResponseModel>, paymentData: RecordPaymentData) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.paymentDao().saveOfflinePaymentData(paymentData)
                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery("payment", AppConstant.ADD_COUNT))
            }
            val addResponseModel = PaymentRecordResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun getOfflinePaymentDetails(liveData: MutableLiveData<RecordPaymentDetailModel>, id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val addResponseModel = RecordPaymentDetailModel()
            addResponseModel.error = false
            if (database != null && database!!.paymentDao().getOfflinePaymentDetails(id).isNotEmpty()) {
                addResponseModel.data = database!!.paymentDao().getOfflinePaymentDetails(id)[0]
            }
            liveData.postValue(addResponseModel)
        }
    }

    fun deleteOfflinePayment(liveData: MutableLiveData<GenericResponseModel>, id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.paymentDao().deleteOfflinePaymentColumn(id)
                database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery("payment", AppConstant.MINUS_COUNT))
            }
            val addResponseModel = GenericResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successDeleteMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun addOfflineExpenseHead(liveData: MutableLiveData<AddTotalExpenseResponseModel>, model: ExpenseTrackerDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.expenseDao().saveOfflineExpenseHeadData(model)
            }
            val addResponseModel = AddTotalExpenseResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }


    fun addOfflineExpense(liveData: MutableLiveData<AddExpenseResponseModel>, model: ExpenseDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.expenseDao().saveOfflineExpenseData(model)
            }

            database!!.expenseDao().updateExpenseHead(model.reimbursementtracker, model.amount, 1)
            val addResponseModel = AddExpenseResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun updateOfflineExpense(liveData: MutableLiveData<AddExpenseResponseModel>, model: ExpenseDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            var totalAmountToAdd: Double? = 0.0
            if (database != null) {
                val remList = database!!.expenseDao().getExpenseDetail(model.id!!) as ArrayList<ExpenseDataItem>
                if (remList.isNotEmpty()) {
                    val remDetail = remList[0]
                    val decreaseAmount = remDetail.amount
                    totalAmountToAdd = model.amount?.minus(decreaseAmount!!)
                }

                database!!.expenseDao().updateOfflineExpenseData(model)
                database!!.expenseDao().updateExpenseHeadAmount(model.reimbursementtracker, totalAmountToAdd)
            }

            val addResponseModel = AddExpenseResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun deleteOfflineExpenseHead(liveData: MutableLiveData<AddTotalExpenseResponseModel>, rtId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                database!!.expenseDao().deleteExpenseHeadColumn(rtId)
            }

            val addResponseModel = AddTotalExpenseResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successDeleteMessage
            liveData.postValue(addResponseModel)
        }
    }

    fun deleteOfflineExpense(liveData: MutableLiveData<AddExpenseResponseModel>, remData: ExpenseDataItem) {
        CoroutineScope(Dispatchers.IO).launch {
            var totalAmountToAdd: Double? = 0.0
            if (database != null) {

                val rtList = database!!.expenseDao().getExpenseHeadDetail(remData.reimbursementtracker!!)
                        as ArrayList<ExpenseTrackerDataItem>
                if (rtList.isNotEmpty()) {
                    val rtDetail = rtList[0]
                    val rtAmount: Double? = rtDetail.totalAmount
                    totalAmountToAdd = rtAmount!!.minus(remData.amount!!)

                    database!!.expenseDao().updateExpenseHead(remData.reimbursementtracker, totalAmountToAdd, -1)

                    database!!.expenseDao().deleteExpenseColumn(remData.id!!)
                }

            }

            val addResponseModel = AddExpenseResponseModel()
            addResponseModel.error = false
            addResponseModel.message = successDeleteMessage
            liveData.postValue(addResponseModel)
        }
    }

    private fun getUpdateDashboardQuery(fieldName: String, method: String): SimpleSQLiteQuery {
        val queryString = if (method == AppConstant.ADD_COUNT) {
            "UPDATE dashboard_table SET $fieldName = $fieldName + 1"
        } else {
            "UPDATE dashboard_table SET $fieldName = $fieldName - 1"
        }
        return SimpleSQLiteQuery(queryString)
    }


    fun getCustomerNotSyncedData(liveData: MutableLiveData<CustomerInfoModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = CustomerInfoModel()
                    val customerList = ArrayList<CustomerData>()

                    customerList.addAll(database!!.customerDao().getCustomerNotSyncedData() as ArrayList<CustomerData>)
                    model.data = customerList
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getLeadNotSyncedData(liveData: MutableLiveData<LeadListResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = LeadListResponseModel()
                    val list = ArrayList<LeadLisDataItem>()

                    list.addAll(database!!.leadDao().getLeadNotSyncedData() as ArrayList<LeadLisDataItem>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getOrderNotSyncedData(liveData: MutableLiveData<OrderInfoModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = OrderInfoModel()
                    val orderList = ArrayList<OrderData>()

                    orderList.addAll(database!!.orderDao().getOrderNotSyncedData() as ArrayList<OrderData>)
                    model.data = orderList
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getPaymentNotSyncedData(liveData: MutableLiveData<RecordPaymentInfoModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = RecordPaymentInfoModel()
                    val list = ArrayList<RecordPaymentData>()

                    list.addAll(database!!.paymentDao()
                            .getPaymentNotSyncedListWithUpdatedCustomerID()
                            as ArrayList<RecordPaymentData>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getOfflineCustomerFeedbackList(liveData: MutableLiveData<CustomerFollowUpListResponseModel>, customerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = CustomerFollowUpListResponseModel()
                    val list = ArrayList<CustomerFollowUpDataItem>()

                    list.addAll(database!!.customerDao().getCustomerFeedbackList(customerId) as ArrayList<CustomerFollowUpDataItem>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getCustomerActivityList(liveData: MutableLiveData<CustomerFollowUpListResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = CustomerFollowUpListResponseModel()
                    val list = ArrayList<CustomerFollowUpDataItem>()

                    list.addAll(database!!.customerDao()
                            .getCustomerActivityNotSyncedData(AppConstant.ATTENDANCE)
                            as ArrayList<CustomerFollowUpDataItem>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getExpenseHeadList(liveData: MutableLiveData<ExpenseTrackerResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = ExpenseTrackerResponseModel()
                    val list = ArrayList<ExpenseTrackerDataItem>()

                    list.addAll(database!!.expenseDao().getExpenseHeadNotSyncedData()
                            as ArrayList<ExpenseTrackerDataItem>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getExpenseList(liveData: MutableLiveData<ExpenseResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = ExpenseResponseModel()
                    val list = ArrayList<ExpenseDataItem>()

                    list.addAll(database!!.expenseDao().getExpenseListNotSyncedData()
                            as ArrayList<ExpenseDataItem>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getOffLineBeatList(liveData: MutableLiveData<OrgBeatListResponseModel>, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = OrgBeatListResponseModel()
                    val list = ArrayList<OrgBeatModel>()

                    list.addAll(database!!.dashboardDao().getBeatListData(value)
                            as ArrayList<OrgBeatModel>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getCustomerAddressNotSyncedData(liveData: MutableLiveData<CustomerAddressListResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = CustomerAddressListResponseModel()
                    val list = ArrayList<CustomerAddressDataItem>()

                    list.addAll(database!!.customerDao()
                            .getCustomerAddressNotSyncedDataWithUpdatedCustomerID()
                            as ArrayList<CustomerAddressDataItem>)
                    model.data = list
                    model.error = false
                    liveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun checkOfflineDataAvailable(liveData: MutableLiveData<Pair<Boolean, Int>>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {

                    val boolean = database!!.customerDao().getAttendanceData().isNullOrEmpty().not() ||
                            database!!.customerDao().getAllCustomerFeedbackList().isNotEmpty() ||
                            database!!.orderDao().getOrderNotSyncedData().isNotEmpty() ||
                            database!!.paymentDao().getPaymentNotSyncedList().isNotEmpty() ||
                            database!!.leadDao().getLeadNotSyncedData().isNotEmpty() ||
                            database!!.customerDao().getCustomerNotSyncedData().isNotEmpty() ||
                            database!!.customerDao().getCustomerAddressNotSyncedData().isNotEmpty() ||
                            database!!.expenseDao().getExpenseHeadNotSyncedData().isNotEmpty() ||
                            database!!.expenseDao().getExpenseListNotSyncedData().isNotEmpty()

                    val count = (database!!.customerDao().getAttendanceData()?.size ?: 0) +
                            database!!.customerDao().getAllCustomerFeedbackList().size +
                            database!!.orderDao().getOrderNotSyncedData().size +
                            database!!.paymentDao().getPaymentNotSyncedList().size +
                            database!!.leadDao().getLeadNotSyncedData().size +
                            database!!.customerDao().getCustomerNotSyncedData().size +
                            database!!.customerDao().getCustomerAddressNotSyncedData().size +
                            database!!.expenseDao().getExpenseHeadNotSyncedData().size +
                            database!!.expenseDao().getExpenseListNotSyncedData().size


                    liveData.postValue(Pair(boolean, count))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun checkOfflineCustomerWithErrorAvailable(liveData: MutableLiveData<Pair<Boolean, CustomerInfoModel>>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val boolean = database!!.customerDao().getCustomerDataWithError().isNotEmpty()

                    val response = CustomerInfoModel()
                    response.error = false
                    val list = database!!.customerDao().getCustomerDataWithError()
                    response.data = list

                    liveData.postValue(Pair(boolean, response))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun checkOfflineAttendanceAvailable(liveData: MutableLiveData<CheckInOutListResponseModel>) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val response = CheckInOutListResponseModel()
                    response.error = false
                    val list = database!!.customerDao().getAttendanceData()
                    response.data = list

                    liveData.postValue(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateCustomerRelatedActivity(updateCustomerLiveData: MutableLiveData<GenericResponseModel>, oldCustomerId: Int, newCustomerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = GenericResponseModel()
                    database!!.customerDao().updateCustomerActivity(oldCustomerId, newCustomerId)
                    database!!.customerDao().updateCustomerAddress(oldCustomerId, newCustomerId)
                    database!!.orderDao().updateCustomerOrder(oldCustomerId, newCustomerId)
                    database!!.paymentDao().updateCustomerPayment(oldCustomerId, newCustomerId)

                    if (database != null) {
                        database!!.customerDao().deleteOfflineCustomerColumn(oldCustomerId)
                        database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery(
                                "customers", AppConstant.MINUS_COUNT))
                    }

                    model.error = false
                    model.message = successMessage
                    updateCustomerLiveData.postValue(model)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateLeadRelatedActivity(updateCustomerLiveData: MutableLiveData<GenericResponseModel>, oldCustomerId: Int, newCustomerId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = GenericResponseModel()
                    database!!.leadDao().updateLeadActivity(oldCustomerId, newCustomerId)

                    if (database != null) {
                        database!!.leadDao().deleteOfflineLead(oldCustomerId)
                        database!!.dashboardDao().updateDashboardData(getUpdateDashboardQuery(
                                "leadCount", AppConstant.MINUS_COUNT))
                    }

                    model.error = false
                    model.message = successMessage
                    updateCustomerLiveData.postValue(model)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteOfflineOrder(orderId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    database!!.orderDao().deleteOfflineOrderData(orderId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteCustomerActivity(id: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    database!!.customerDao().deleteCustomerActivity(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateAddressIdInOrders(updateAddressLiveData: MutableLiveData<GenericResponseModel>, oldAddressId: Int?, newAddressId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = GenericResponseModel()
                    val orders = database!!.orderDao().getOrderNotSyncedData()
                    orders.forEach { order ->
                        updateAddressIdInJson(order.orderId, order.address, oldAddressId, newAddressId)
                    }
                    model.error = false
                    model.message = successMessage
                    updateAddressLiveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun updateExpenseHeadRelatedModel(updateExpenseLiveData: MutableLiveData<GenericResponseModel>, oldId: Int?, newId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    val model = GenericResponseModel()
                    database!!.expenseDao().updateExpenseListWithNewTrackerId(oldId, newId)
                    database!!.expenseDao().deleteExpense(oldId)
                    model.error = false
                    model.message = successMessage
                    updateExpenseLiveData.postValue(model)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteExpenseList(id: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    database!!.expenseDao().deleteExpenseList(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateAddressIdInJson(orderId: String?, address: Address?, oldAddressId: Int?, newAddressId: Int?) {
        if (address?.id == oldAddressId) {
            val updatedAddress = address?.copy(id = newAddressId)
            database!!.orderDao().updateOrderJsonData(orderId, Gson().toJson(updatedAddress))

            database!!.customerDao().deleteCustomerAddress(oldAddressId)
        }
    }

    fun updateCustomerError(customerId: Int, message: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    database!!.customerDao().updateCustomerError(customerId, message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deleteAttendanceData() {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    database!!.customerDao().deleteAttendanceData()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun deletePaymentRecords(id: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (database != null) {
                try {
                    database!!.paymentDao().deletePayment(id)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}