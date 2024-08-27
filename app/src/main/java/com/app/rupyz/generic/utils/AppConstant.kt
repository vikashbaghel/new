package com.app.rupyz.generic.utils

import com.app.rupyz.BuildConfig
import com.app.rupyz.model_kt.NatureOfBusinessModel
import com.google.android.play.core.install.model.AppUpdateType

object AppConstant {

    const val SELECTED_CUSTOMER_FILTER ="selected_customer_filter"
    const val CLEAR_TOP_DONE = "clearTopDone"
    const val SUPER_STOCKIST_LEVEL = "Super Stockist"
    const val DISTRIBUTOR_LEVEL = "Distributor"
    const val RETAILERS_LEVEL = "Retailers"
    const val MODULE_CUSTOMER = "CUSTOMER"
    const val USER_PREFERENCE = "USER_PREFERENCE"
    const val TAG = "DEBUG_TAG"
    const val SELECTED_DISTRIBUTOR = "selected_distributor"
    const val FILTER_LOCATION = "FILTER_LOCATION"

    const val FILTER_BADGE = "FILTER_BADGE"

    const val TERMS_URL = "https://rupyz.com/terms-and-conditions/"
    const val POLICY_URL = "https://rupyz.com/privacy/"
    const val IMAGE_UPLOAD_TAG = "IMAGE_UPLOAD_FRAGMENT"

    const val CONNECTION_TYPE = "connection_type"
    const val MY_CONNECTION = "my_connection"

    const val MY_CONNECTION_COUNT = "my_connection_count"
    const val INVITATION_COUNT = "invitation_count"
    const val INVITATION = "invitation"
    const val PROFILE = "profile"
    const val PROFILE_SLUG = "profile_slug"
    const val EDIT_PRODUCT = "edit_product"
    const val PRODUCT_INFO = "product_info"
    const val PRODUCT_ID = "product_id"
    const val EDIT_TEAM = "edit_team"
    const val TEAM_INFO = "team_info"
    const val STAFF_TC_INFO = "staff_tc_info"
    const val STAFF_PC_INFO = "staff_pc_info"
    const val EDIT_TESTIMONIAL = "edit_testimonial"
    const val TESTIMONIAL_INFO = "testimonial_info"
    const val EDIT_ACHIEVEMENT = "edit_achievement"
    const val IMAGE_POSITION = "image_position"
    const val IMAGE_URL = "image_url"
    const val PDF_URL = "pdf_url"
    const val PENDING = "PENDING"

    const val ACCEPTED = "ACCEPTED"
    const val DECLINED = "DECLINED"
    const val BLOCKED = "BLOCKED"
    const val STORE_FRONT = "STOREFRONT"

    const val APP_ACCESS_TYPE = "APP_ACCESS_TYPE"
    const val NAME = "name"
    const val CREATED_AT = "created_at"
    const val PENDING_BEAT_STATUS = "Pending"

    const val ORGANIZATION = "organization"

    const val NEW_ADMIN = "new_admin"
    const val PRODUCT = "product"
    const val PRODUCT_DETAILS = "product details"
    const val DISCOVERY = "DISCOVERY"

    const val TAB_NAME = "TAB_NAME"

    const val IMAGE_TYPE = "image_type"

    const val IMAGE_TYPE_PROFILE = "image_type_profile"
    const val IMAGE_TYPE_BANNER = "image_type_banner"
    const val IMAGE_PREVIEW = "image_preview"
    const val LOCATION = "location"
    const val CAPTURE_BY = "Captured By"

    const val BADGE = "badge"
    const val SEARCH_STRING = "search_string"
    const val HEADING = "heading"
    const val MESSAGE = "message"
    const val DATA_TYPE_STRING = "string"
    const val DATA_TYPE_INTEGER = "integer"
    const val DATA_TYPE_LIST_OF_INTEGER = "list_of_integer"
    const val SEND_CONNECTION_REQUEST = "SEND"

    const val VISIBLE = "visible"
    const val REMOVE_CONNECTION_REQUEST = "REMOVE"

    const val BOTTOM_SHEET_PREVIEW_TYPE = "bottom_sheet_preview_type"

    const val CUSTOMER = "Customer"
    const val CUSTOMER_ID = "customer_id"
    const val DISTRIBUTOR_SELECTOR = "distributor_selector"
    const val CUSTOMER_CHECKED_IN_STATUS = "customer_checked_in_status"
    const val CUSTOMER_NAME = "customer_name"
    const val CUSTOMER_TYPE = "customer_type"
    const val ORDER_ADDRESS_ID = "order_address_id"
    const val ORDER_ID = "order_id"
    const val ORDER_STATUS = "order_status"
    const val ORDER_MESSAGE = "order_message"
    const val ORDER = "Order"
    const val TOTAL_AMOUNT = "total_amount"
    const val NEW_PAYMENT = "New Payment"
    const val NO_ORDER = "No Order"
    const val ACTIVITY = "Activity"
    const val MODULE_TYPE = "module_type"
    const val SUB_MODULE_TYPE = "sub_module_type"
    const val DISPATCH_ID = "dispatch_id"
    const val DISPATCH_MODEL = "dispatch_model"
    const val SEGMENT_ID = "segment_id"
    const val SEGMENT_NAME = "segment_name"
    const val SEGMENT_DISCOUNT = "segment_discount"
    const val DISCOUNT_TYPE_PERCENT = "PERCENT"
    const val DISCOUNT_TYPE_RUPEES = "AMOUNT"
    const val DISCOUNT_TYPE_OFFER_PRICE = "OFFER_PRICE"
    const val CART_ITEM = "cart_item"

    const val ORDER_EDIT = "order_edit"

    const val LOCATION_TRACKING = "LOCATION_TRACKING"

    const val STAFF = "staff"
    const val STAFF_ID = "staff_id"
    const val STAFF_NAME = "staff_name"
    const val STAFF_DETAILS = "staff_details"

    const val STATUS_PENDING = "Pending"
    const val STATUS_APPROVED = "Approved"
    const val STATUS_DISHONOUR = "Dishonour"
    const val STATUS_CONVERTED_TO_CUSTOMER = "Converted To Customer"

    const val APPROVE = "Approve"
    const val CHECK_IN = "check-in"
    const val TELEPHONIC_ORDER = "telephonic-order"
    const val CUSTOMER_LEVEL_ORDER = "enable_customer_level_order"
    const val LBL_CHECK_OUT = "CheckOut"
    const val CHECK_IMAGE_REQUIRED = "check-in required"
    const val CHECK_IMAGE_INPUT = "check-in input"
    const val RECEIVED_ORDER = "Received"
    const val APPROVED_ORDER = "Approved"
    const val DELIVERED_ORDER = "Delivered"
    const val READY_TO_DISPATCH_ORDER = "Ready To Dispatch"
    const val PROCESSING_ORDER = "Processing"
    const val SHIPPED_ORDER = "Shipped"
    const val DISPATCHED_ORDER = "Dispatched"
    const val PARTIAL_SHIPPED_ORDER = "Partial Shipped"
    const val PARTIAL_DISPATCHED_ORDER = "Partial Dispatched"
    const val ORDER_REJECTED = "Rejected"
    const val ORDER_CLOSE = "Close"
    const val UPDATE_ORDER_STATUS = "Update Order Status"
    const val REJECT = "Reject"

    const val KNOW_MORE_TYPE = "know_more_type"
    const val TOTAL_SALES = "total_sales"
    const val TOTAL_AMOUNT_RECEIVE = "total_amount_receive"

    const val OTHERS = "others"
    const val DISTRIBUTOR = "DISTRIBUTOR"

    const val IS_RETAIL_MASKED = "is_retail_masked"

    const val BUYERS_UNIT = "BUYERS_UNIT"
    const val MRP_UNIT = "MRP_UNIT"
    const val PACKAGING_UNIT = "PACKAGING_UNIT"

    const val PRODUCT_CATEGORY = "PRODUCT_CATEGORY"

    const val DAILY = "DAILY"
    const val MONTHLY = "MONTHLY"
    const val WEEKLY = "WEEKLY"
    const val CUSTOMER_DATE_FILTER = "CUSTOMER_DATE_FILTER"

    const val CURRENT_MONTH = "Current  Month"
    const val LAST_THREE_MONTH = "Last 3 Months"
    const val LAST_SIX_MONTH = "Last 6 Months"
    const val LAST_TWELVE_MONTH = "Last 12 Months"
    const val CURRENT_FY = "Current F.Y."
    const val CUSTOM = "Custom"
    const val CUSTOM_RANGE = "CUSTOM"

    const val DATE_FILTER = "DATE_FILTER"

    const val TOP_CATEGORY = "TOP_CATEGORY"

    const val TOP_PRODUCT = "TOP_PRODUCT"

    const val ADD_DISCOUNT = "ADD_DISCOUNT"
    const val ADD_OFFER = "ADD_OFFER"
    const val ADD_DELIVERY_CHARGES = "ADD_DELIVERY_CHARGES"
    const val GET_PRODUCT_INFO = "GET_PRODUCT_INFO"

    const val REMAINING_ORDER = "REMAINING_ORDER"

    const val ORGANIZATION_LEVEL = "My Company"
    const val CUSTOMER_LEVEL = "CUSTOMER_LEVEL"
    const val CUSTOMER_LEVEL_0 = ""
    const val CUSTOMER_LEVEL_ALL = "All Order"
    const val CUSTOMER_LEVEL_1 = "LEVEL-1"
    const val CUSTOMER_LEVEL_2 = "LEVEL-2"
    const val CUSTOMER_LEVEL_3 = "LEVEL-3"

    const val IS_WHATS_APP_NOTIFICATION = "IS_WHATS_APP_NOTIFICATION"

    const val REQUEST_CODE = 781
    const val FLEXIBLE = AppUpdateType.FLEXIBLE
    const val IMMEDIATE = AppUpdateType.IMMEDIATE

    const val PAYMENT = "Payment"
    const val FULL_PAYMENT_IN_ADVANCE = "Full Payment in Advance"
    const val PARTIAL_PAYMENT = "Partial Payment"
    const val PAYMENT_ON_DELIVERY = "Payment On Delivery"
    const val CREDIT_DAYS = "Credit Days"
    const val ADVANCE = "Advance"
    const val PAYMENT_ON_NEXT_ORDER = "Payment On Next Order"
    const val PAYMENT_ON_NEXT_ORDER_API = "PAYMENT_ON_NEXT_ORDER"

    const val FULL_PAYMENT_IN_ADVANCE_API = "FULL_ADVANCE"
    const val PARTIAL_PAYMENT_API = "PARTIAL_ADVANCE"
    const val PAYMENT_ON_DELIVERY_API = "PAY_ON_DELIVERY"
    const val CREDIT_DAYS_API = "CREDIT_DAYS"

    const val PAYMENT_INFO = "PAYMENT_INFO"
    const val IS_TELEPHONIC_ORDER = "isTelephonicOrder"
    const val PAYMENT_ID = "PAYMENT_ID"

    const val USER_ID = "USER_ID"
    const val USER_NAME = "USER_NAME"
    const val PROFILE_IMAGE = "profile_image"
    const val STAFF_HIERARCHY = "hierarchy"
    const val DISABLE_GALLERY_PHOTO = "DISABLE_GALLERY_PHOTO"

    const val OTP_REF = "OTP_REF"

    const val STAFF_ROLE = "STAFF_ROLE"

    const val LEAD_CATEGORY = "LEAD_CATEGORY"
    const val CATEGORY = "category"

    const val EXPENSE_STATUS = "EXPENSE_STATUS"
    const val ADMIN = "ADMIN"
    const val ACCESS_TYPE_MASTER = "SARE360"
    const val ACCESS_TYPE_STAFF = "STAFF"

    const val EXPENSE = "EXPENSE"
    const val EXPENSE_LIST = "EXPENSE_LIST"
    const val EXPENSE_ID = "EXPENSE_ID"
    const val TOTAL_EXPENSE_DETAILS = "TOTAL_EXPENSE_DETAILS"
    const val EXPENSE_DETAILS = "EXPENSE_DETAILS"

    const val LEAD = "Lead"
    const val LEAD_ID = "Lead_Id"
    const val LEAD_INFO = "LEAD_INFO"

    const val FAKE_LOCATION_DETECTED = "FAKE_LOCATION_DETECTED"
    const val FAKE_LOCATION_UPDATE_SEND = "FAKE_LOCATION_UPDATE_SEND"

    const val HOME = "Home"

    const val ACTIVITY_TYPE = "ACTIVITY_TYPE"
    const val ACTIVITY_FILTER = "Activity Type"
    const val CUSTOMER_FILTER = "customerFilter"
    const val ACTIVITY_TYPE_REGULAR_BEAT = "REGULAR_BEAT"
    const val ACTIVITY_TYPE_JOINT_ACTIVITY = "JOINT_ACTIVITY"
    const val ACTIVITY_TYPE_MARK_LEAVE = "MARK_LEAVE"
    const val ACTIVITY_TYPE_OFFICE_VISIT = "OFFICE_VISIT"
    const val ACTIVITY_TYPE_DISTRIBUTOR_VISIT = "DISTRIBUTOR_VISIT"
    const val ACTIVITY_TYPE_OTHERS = "OTHERS"
    const val ACTIVITY_TYPE_HALF_DAY = "HALF_DAY"
    const val ACTIVITY_TYPE_FULL_DAY = "FULL_DAY"
    const val GEO_ACTIVITY_TYPE_ACTIVITY_MAP = "ACTIVITY_MAP"
    const val GEO_ACTIVITY_TYPE_LIVE_LOCATION = "LIVE_LOCATION"
    const val MY_ACTIVITY = "MY_ACTIVITY"
    const val ACTIVITY_ID = "ACTIVITY_ID"
    const val CUSTOMER_FEEDBACK = "Customer Feedback"
    const val LEAD_FEEDBACK = "Lead Feedback"
    const val ORDER_DISPATCH = "Order Dispatch"
    const val ORDER_MAP = "Order Map"
    const val ATTENDANCE = "Attendance"
    const val ATTENDANCE_ID = "AttendanceId"
    const val IsStartDay = "IsStartDay"
    const val BULK_ATTENDANCE = "Bulk_Attendance"
    const val SAVE_ATTENDANCE_PREF = "SAVE_ATTENDANCE_PREF"
    const val DAY_START_END = "Attendance"

    const val ATTENDANCE_TYPE_PRESENT = "Present"
    const val ATTENDANCE_TYPE_CASUAL_LEAVE = "Casual Leave"
    const val ATTENDANCE_TYPE_HALF_CASUAL_LEAVE = "Half Casual Leave"
    const val ATTENDANCE_TYPE_UNPAID_LEAVE = "Unpaid Leave"
    const val ATTENDANCE_TYPE_HALF_UNPAID_LEAVE = "Half Unpaid Leave"

    const val ATTENDANCE_CHECK_IN = "Check In"
    const val ATTENDANCE_CHECK_OUT = "Check Out"
    const val ATTENDANCE_START_DAY = "start_day"
    const val ATTENDANCE_END_DAY = "end_day"
    const val ATTENDANCE_HALF_DAY = "half_day"
    const val ATTENDANCE_FULL_DAY = "full_day"
    const val OFFICE_VISIT = "office_visit"

    const val BEAT_PLAN = "Beat Plan"
    const val MY_BEAT_PLAN = "MY_BEAT_PLAN"
    const val ALL_BEAT_PLAN = "ALL_BEAT_PLAN"
    const val BEAT_PLAN_HISTORY = "BEAT_PLAN_HISTORY"
    const val DUPLICATE_BEAT_PLAN = "DUPLICATE_BEAT_PLAN"
    const val ALL = "All"
    const val RANGE = "Range -"
    const val SELECT_DATE = "Select a date range"
    const val THIS = "This "
    const val RANGE_DATE = " - "
    const val SORT_BY = "created_at"
    const val PARENT_NAME = "Parent Name"

    const val ACTIVE = "Active"
    const val INSIGHTS = "Insights"
    const val INVENTORY = "Inventory"
    const val APPROVAL = "Approval"
    const val PENDING_EXPENSE = "Pending"
    const val SUBMITTED = "Submitted"
    const val APPROVED = "Approved"
    const val PAID = "Paid"
    const val REJECTED = "Rejected"
    const val COMPLETED = "Completed"
    const val CLOSED = "Closed"
    const val QR_CODE = "QR_CODE"


    const val INACTIVE = "Inactive"
    const val LEAVE = "Leave"

    const val LOG_IN = "LOGIN"
    const val LOG_OUT = "LOGOUT"

    var selected_item = 0
    var selected_enable = false
    const val STATE_ITEM = 1
    const val CUSTOMER_ITEM = 2
    const val STAFF_ITEM = 3
    const val ACTIVITY_ITEM = 0
    const val STATE = "State"
    const val ROLE = "Role"
    const val REPORTING_TO = "Reporting To"
    const val CUSTOMER_MAP = "checkboxCustomerMap"
    const val PAYMENT_MAP = "checkboxPaymentMap"
    const val STATE_MAP = "checkboxStateMap"
    const val STAFF_MAP = "checkboxStaffMap"
    const val ACTIVITY_MAP = "checkboxActivityMap"
    const val FULLFILLED_MAP = "checkboxFullFilledMap"
    const val FULLFILLED = "checkboxFullFilled"
    const val RECEIVED_MAP = "checkboxReceivedMap"
    const val RECEIVED = "checkboxReceived"

    const val TARGET = "Targets"

    const val TARGET_PRODUCTS = "Products"
    const val TARGET_PRODUCTS_LIST = "TARGET_PRODUCTS_LIST"
    const val TARGET_PRODUCTS_TYPE_AMOUNT = "AMOUNT"
    const val TARGET_PRODUCTS_TYPE_COUNT = "COUNT"

    const val DOCUMENT = "DOCUMENT"
    const val TITLE = "title"
    const val URL = "url"

    const val MAX_DIGIT_AFTER_DECIMAL = 2
    const val FOUR_DIGIT_AFTER_DECIMAL = 4

    const val FIND_ORDER_KEY_FROM_VALUE = "order_key"
    const val FIND_ORDER_VALUE_FROM_KEY = "order_value"

    const val NOTIFICATION = "NOTIFICATION"

    const val ACTIVE_TARGET = "Active"
    const val UPCOMING_TARGET = "Upcoming"
    const val CLOSED_TARGET = "Closed"

    const val TARGET_SALES = "Sales"
    const val TARGET_COLLECTION = "Collection"
    const val TARGET_LEADS = "Leads"
    const val TARGET_CUSTOMER = "Customer"
    const val TARGET_VISITS = "Visits"

    const val WHATSAPP = "whatsapp"
    const val EMAIL = "email"
    const val ADDRESS = "Address"


    const val LOCATION_TRACKING_TAG = 101
    const val OPEN_LOCATION_SETTINGS = 102
    const val OPEN_GPS_SETTINGS = 103

    const val FOUR_DECIMAL_POINTS = 4
    const val TWO_DECIMAL_POINTS = 2

    const val LIVE_LOCATION_START_TIME = 7 // 7:00 AM
    const val LIVE_LOCATION_END_TIME = 20 // 8:00 PM
    const val MARK_END_DAY_TIME = 23 // 11:00 PM

    const val BEAT_TYPE_LOCATION = "LOCATION"
    const val BEAT = "BEAT"
    const val BEAT_ID = "BEAT_ID"
    const val BEAT_ROUTE_PLAN_ID = "BEAT_ROUTE_PLAN_ID"
    const val HOLIDAY = "HOLIDAY"
    const val BEAT_ROUTE = "Beat Route"
    const val BEAT_PLAN_NOTES_READ = "BEAT_PLAN_NOTES_READ"

    const val TARGET_CUSTOMERS_FILTER_FOR_BEAT = "TARGET_CUSTOMERS"
    const val VISITED_CUSTOMERS_FILTER_FOR_BEAT = "VISITED_CUSTOMERS"
    const val UNEXPECTED_CUSTOMERS_FILTER_FOR_BEAT = "UNEXPECTED_CUSTOMERS"

    const val GEO_FENCING_ENABLE = "GEO_FENCING_ENABLE"
    const val GEO_FENCING = "GEO_FENCING"
    const val ENABLE_ORG_OFFLINE_MODE = "ENABLE_ORG_OFFLINE_MODE"
    const val SELECT_COUNT = 2
    const val START_DAY = "START_DAY"
    const val START_DAY_TIME = "START_DAY_TIME"
    const val END_DAY_TIME = "END_DAY_TIME"
    const val DELETE_CUSTOMER = "DELETE_CUSTOMER"

    const val NOT_ASSIGNED = "Not Assigned"
    const val NOT_ASSIGNED_STATUS = "NOT_ASSIGNED"
    const val ASSIGNED = "Assigned"

    const val STAFF_DETAILS_FOR_BEAT = "STAFF_DETAILS_FOR_BEAT"
    const val BEAT_ID_FOR_ASSIGN_STAFF = "BEAT_ID_FOR_ASSIGN_STAFF"

    const val VIEW_BEAT_PLAN = "VIEW_BEAT_PLAN"

    const val ADD_COUNT = "ADD_COUNT"
    const val MINUS_COUNT = "MINUS_COUNT"

    const val LIVE_LOCATION = "LIVE_LOCATION"
    const val SHARE_LOCATION_DATA_FOR_FIRST_TIME = "SHARE_LOCATION_DATA_FOR_FIRST_TIME"
    const val HUNDRED_PERCENT = "100%"

    const val ANDROID_OFFLINE_TAG = "android_offline"
    const val ANDROID = "ANDROID"

    const val THIS_FEATURE_DOES_NOT_SUPPORT_IN_OFFLINE_MODE =
        "This feature does not support offline mode"

    const val TRANSFER_CUSTOMER = "TRANSFER_CUSTOMER"

    const val DELETE_LEAD = "DELETE_LEAD"

    const val DEVICE_LOGS = "DEVICE_LOGS"

    object Delete {
        const val DELETE_CUSTOMER = "delete_customer"
    }

    const val SHARE_LOCATION_PERMISSION_CHANGE_TO_API = "SHARE_LOCATION_PERMISSION_CHANGE_TO_API"

    object LocationPermissionType {
        const val BACKGROUND = "BACKGROUND"
        const val FOREGROUND = "FOREGROUND"
        const val NOT_PROVIDED = "NOT_PROVIDED"
    }

    fun getNatureOfBusinessList(): List<String> {
        val list: MutableList<String> = ArrayList()
        list.add("Manufacturing")
        list.add("Wholesale / Distribution")
        list.add("Dealer / Retailer / Reseller")
        list.add("Service - Professional")
        list.add("Service - Freelancer")
        list.add("Service - Other")
        return list
    }


    fun getAttendanceStatus(key: String): String? {
        val hashMap = HashMap<String, String>()
        hashMap[ATTENDANCE_TYPE_PRESENT] = "Present"
        hashMap[ATTENDANCE_TYPE_CASUAL_LEAVE] = "Leave"
        hashMap[ATTENDANCE_TYPE_HALF_CASUAL_LEAVE] = "Half Leave"
        hashMap[ATTENDANCE_TYPE_UNPAID_LEAVE] = "Leave"
        hashMap[ATTENDANCE_TYPE_HALF_UNPAID_LEAVE] = "Half Leave"

        return hashMap[key]
    }

    fun getOrderStatus(value: String, findValue: String): String? {
        val hashMap = HashMap<String, String>()
        hashMap[RECEIVED_ORDER] = "Received"
        hashMap[APPROVED_ORDER] = "Approved"
        hashMap[PROCESSING_ORDER] = "Processing"
        hashMap[SHIPPED_ORDER] = "Dispatched"
        hashMap[PARTIAL_SHIPPED_ORDER] = "Partial Shipped"
        hashMap[PARTIAL_DISPATCHED_ORDER] = "Partial Shipped"
        hashMap[ORDER_REJECTED] = "Reject"
        hashMap[ORDER_CLOSE] = "Close"
        hashMap[DELIVERED_ORDER] = "Delivered"
        hashMap[READY_TO_DISPATCH_ORDER] = "Ready To Dispatch"

        return when (findValue) {
            FIND_ORDER_KEY_FROM_VALUE -> {
                val result = hashMap.filterValues { it == value }.keys
                if (result.isNotEmpty() && result.size > 1) {
                    result.last()
                } else if (result.size == 1) {
                    result.first()
                } else {
                    ""
                }
            }

            FIND_ORDER_VALUE_FROM_KEY -> {
                hashMap[value]
            }

            else -> {
                ""
            }
        }
    }


    fun getOrderStatusForApiFilter(value: String): String? {
        val hashMap = HashMap<String, String>()
        hashMap[RECEIVED_ORDER] = "Received"
        hashMap[APPROVED_ORDER] = "Approved"
        hashMap[PROCESSING_ORDER] = "Processing"
        hashMap[DISPATCHED_ORDER] = "Dispatched"
        hashMap[PARTIAL_DISPATCHED_ORDER] = "Partial Shipped"
        hashMap[ORDER_REJECTED] = "Rejected"
        hashMap[ORDER_CLOSE] = "Closed"
        hashMap[DELIVERED_ORDER] = "Delivered"
        hashMap[READY_TO_DISPATCH_ORDER] = "Ready To Dispatch"

        return hashMap[value]

    }

    fun getOrderStatusForUpdate(value: String): String? {
        val hashMap = HashMap<String, String>()
        hashMap["Approve"] = APPROVED_ORDER
        hashMap["Processing"] = PROCESSING_ORDER
        hashMap["Dispatch"] = SHIPPED_ORDER
        hashMap["Partial Shipped"] = PARTIAL_SHIPPED_ORDER
        hashMap["Reject"] = ORDER_REJECTED
        hashMap["Close"] = ORDER_CLOSE
        hashMap["Delivered"] = DELIVERED_ORDER
        hashMap["Ready To Dispatch"] = READY_TO_DISPATCH_ORDER


        return hashMap[value]
    }

    fun getPaymentTermsForApi(value: String, findValue: String): String? {
        val hashMap = HashMap<String, String>()
        hashMap["Payment On Delivery"] = PAYMENT_ON_DELIVERY_API
        hashMap["Payment On Next Order"] = PAYMENT_ON_NEXT_ORDER_API
        hashMap["Full Payment in Advance"] = FULL_PAYMENT_IN_ADVANCE_API
        hashMap["Partial Payment"] = PARTIAL_PAYMENT_API
        hashMap["Credit Days"] = CREDIT_DAYS_API


        return when (findValue) {
            FIND_ORDER_KEY_FROM_VALUE -> {
                val result = hashMap.filterValues { it == value }.keys
                if (result.isNotEmpty() && result.size > 1) {
                    result.last()
                } else if (result.size == 1) {
                    result.first()
                } else {
                    ""
                }
            }

            FIND_ORDER_VALUE_FROM_KEY -> {
                hashMap[value]
            }

            else -> {
                ""
            }
        }
    }


    fun getNatureOfBusinessModelList(): List<NatureOfBusinessModel> {
        val list: MutableList<NatureOfBusinessModel> = ArrayList()
        list.add(NatureOfBusinessModel("Manufacturing", "Manufacturing"))
        list.add(NatureOfBusinessModel("Wholesale / Distribution", "Wholesale / Distribution"))
        list.add(
            NatureOfBusinessModel(
                "Dealer / Retailer / Reseller",
                "Dealer / Retailer / Reseller"
            )
        )
        list.add(NatureOfBusinessModel("Service - Professional", "Service - Professional"))
        list.add(NatureOfBusinessModel("Service - Freelancer", "Service - Freelancer"))
        list.add(NatureOfBusinessModel("Service - Other", "Service - Other"))
        return list
    }

    fun getShareProductUrlForDiscovery(productSlug: String?, productNanoId: String?): String {
        return BuildConfig.PROFILE_BASE_URL + productSlug + "/products/" + productNanoId
    }

    const val GET_CUSTOMER_PERMISSION = "VIEW_CUSTOMER"
    const val CREATE_CUSTOMER_PERMISSION = "CREATE_CUSTOMER"
    const val EDIT_CUSTOMER_PERMISSION = "EDIT_CUSTOMER"
    const val DELETE_CUSTOMER_PERMISSION = "DELETE_CUSTOMER"

    const val VIEW_CUSTOMER_CATEGORY_PERMISSION = "VIEW_CUSTOMER_CATEGORY"
    const val CREATE_CUSTOMER_CATEGORY_PERMISSION = "CREATE_CUSTOMER_CATEGORY"
    const val EDIT_CUSTOMER_CATEGORY_PERMISSION = "EDIT_CUSTOMER_CATEGORY"
    const val DELETE_CUSTOMER_CATEGORY_PERMISSION = "DELETE_CUSTOMER_CATEGORY"

    const val VIEW_ORDER_PERMISSION = "VIEW_ORDER"
    const val CREATE_ORDER_PERMISSION = "CREATE_ORDER"
    const val APPROVE_ORDER_PERMISSION = "APPROVE_ORDER"
    const val PROCESS_ORDER_PERMISSION = "PROCESS_ORDER"
    const val READY_TO_DISPATCH_ORDER_PERMISSION = "READY_TO_DISPATCH_ORDER"
    const val DISPATCH_ORDER_PERMISSION = "DISPATCH_ORDER"
    const val DELIVER_ORDER_PERMISSION = "DELIVER_ORDER"
    const val CLOSE_ORDER_PERMISSION = "CLOSE_ORDER"
    const val REJECT_ORDER_PERMISSION = "REJECT_ORDER"
    const val EDIT_ORDER_PERMISSION = "EDIT_ORDER"
    const val DELETE_ORDER_PERMISSION = "DELETE_ORDER"

    const val VIEW_PAYMENT_PERMISSION = "VIEW_PAYMENT"
    const val CREATE_PAYMENT_PERMISSION = "CREATE_PAYMENT"
    const val EDIT_PAYMENT_PERMISSION = "EDIT_PAYMENT"
    const val PAYMENT_STATUS_UPDATE_PERMISSION = "PAYMENT_STATUS_UPDATE"
    const val DELETE_PAYMENT_PERMISSION = "DELETE_PAYMENT"

    const val VIEW_STAFF_PERMISSION = "VIEW_STAFF"
    const val CREATE_STAFF_PERMISSION = "CREATE_STAFF"
    const val EDIT_STAFF_PERMISSION = "EDIT_STAFF"
    const val DEACTIVATE_STAFF_PERMISSION = "DEACTIVATE_STAFF"

    const val VIEW_PRODUCT_PERMISSION = "VIEW_PRODUCT"
    const val CREATE_PRODUCT_PERMISSION = "CREATE_PRODUCT"
    const val EDIT_PRODUCT_PERMISSION = "EDIT_PRODUCT"
    const val DELETE_PRODUCT_PERMISSION = "DELETE_PRODUCT"

    const val VIEW_PRODUCT_CATEGORY_PERMISSION = "VIEW_PRODUCT_CATEGORY"
    const val CREATE_PRODUCT_CATEGORY_PERMISSION = "CREATE_PRODUCT_CATEGORY"
    const val EDIT_PRODUCT_CATEGORY_PERMISSION = "EDIT_PRODUCT_CATEGORY"
    const val DELETE_PRODUCT_CATEGORY_PERMISSION = "DELETE_PRODUCT_CATEGORY"

    const val VIEW_UNIT_PERMISSION = "VIEW_UNIT"
    const val CREATE_UNIT_PERMISSION = "CREATE_UNIT"
    const val EDIT_UNIT_PERMISSION = "EDIT_UNIT"
    const val DELETE_UNIT_PERMISSION = "DELETE_UNIT"

    const val VIEW_LEAD_PERMISSION = "VIEW_LEAD"
    const val CREATE_LEAD_PERMISSION = "CREATE_LEAD"
    const val EDIT_LEAD_PERMISSION = "EDIT_LEAD"
    const val DELETE_LEAD_PERMISSION = "DELETE_LEAD"
    const val APPROVE_LEAD_PERMISSION = "APPROVE_LEAD"
    const val APPROVE_SELF_LEAD_PERMISSION = "APPROVE_SELF_LEAD"

    const val VIEW_LEAD_CATEGORY_PERMISSION = "VIEW_LEAD_CATEGORY"
    const val DELETE_LEAD_CATEGORY_PERMISSION = "DELETE_LEAD_CATEGORY"
    const val CREATE_LEAD_CATEGORY_PERMISSION = "CREATE_LEAD_CATEGORY"
    const val EDIT_LEAD_CATEGORY_PERMISSION = "EDIT_LEAD_CATEGORY"

    const val BEAT_PLAN_APPROVAL_PERMISSION = "BEAT_PLAN_APPROVAL"
    const val REIMBURSEMENT_APPROVAL_PERMISSION = "REIMBURSEMENT_APPROVAL"
    const val ASSIGN_MANAGER_PERMISSION = "ASSIGN_MANAGER"

    const val SORTING_LEVEL_ASCENDING = "ASC"
    const val SORTING_LEVEL_DESCENDING = "DESC"
    const val SORTING_EXPECTED_DELIVERY = "expected_delivery_date"
    const val UPDATE_AT = "updated_at"
    const val DELIVERY_AT = "expected_delivery_date"

    const val REMINDERS = "REMINDERS"
    const val DATE = "DATE"
    const val TODAY = "TODAY"
    const val TOMORROW = "Tomorrow"
    const val THIS_WEEK = "WEEK"
    const val THIS_MONTH = "MONTH"

    const val DATE_RANGE = "DATE_RANGE"

    const val SECTION_NAME_MOBILE_LENGTH = 10
    const val SECTION_NAME_CUSTOMER_LEVEL = "customer_level"
    const val SECTION_NAME_CUSTOMER_TYPE = "customer_type"
    const val SECTION_NAME_CUSTOMER_PARENT = "select_parents"

    //    const val SECTION_NAME_CUSTOMER_PARENT = "customer_parent"
    const val SECTION_NAME_GST_IN = "gstin"
    const val SECTION_NAME_BUSINESS_NAME = "name"
    const val SECTION_NAME_BUSINESS_OWNER_NAME = "contact_person_name"
    const val SECTION_NAME_ADDRESS_LINE_1 = "address_line_1"
    const val SECTION_NAME_ADDRESS_LINE_2 = "addres_line_2"
    const val SECTION_NAME_GEO_ADDRESS = "geo_address"
    const val SECTION_NAME_PIN_CODE = "pincode"
    const val SECTION_NAME_CITY = "city"
    const val SECTION_NAME_STATE = "state"
    const val SECTION_NAME_SELECT_STAFF = "select_staff"
    const val SECTION_NAME_MOBILE = "mobile"
    const val SECTION_NAME_WHATSAPP_MOBILE = "whatsapp_mobile"
    const val SECTION_NAME_SELECT_BEAT = "select_beat"
    const val SECTION_NAME_SELECT_PRICING_GROUP = "pricing_group"
    const val SECTION_NAME_SELECT_PRODUCT_CATEGORY = "select_category"
    const val SECTION_NAME_TERRITORY = "territory"
    const val SECTION_NAME_BUSINESS_LOGO_IMAGE = "logo_image"
    const val SECTION_NAME_BUSINESS_PAYMENT_TYPE = "payment_term"
    const val SECTION_NAME_BUSINESS_CREDIT_LIMIT = "credit_limit"
    const val SECTION_NAME_BUSINESS_OUTSTANDING_AMOUNT = "outstanding_amount"
    const val FEEDBACK_ID = "feedBackId"
    const val NO_INTERNET_CONNECTION = "No Internet Connection"
    const val MAX_RETRY_COUNT: Int = 5

}