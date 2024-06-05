package com.app.rupyz.generic.utils

import android.content.Context
import android.content.SharedPreferences
import com.app.rupyz.MyApplication.Companion.instance

class PermissionModel private constructor(context: Context) {
    private var sharedPref: SharedPreferences? = null
    private var prefsEditor: SharedPreferences.Editor? = null

    init {
        sharedPref = context.getSharedPreferences("salesPermissionPref", Context.MODE_PRIVATE)
        prefsEditor = sharedPref?.edit()
    }

    companion object {
        val INSTANCE: PermissionModel by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            PermissionModel(instance)
        }

        private const val KEY_PERMISSIONS = "permissions"
    }

    fun getPermission(permission: String, defValue: Boolean): Boolean {
        return if (SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE).equals(AppConstant.ACCESS_TYPE_MASTER)) {
            true
        } else {
            return getPermissions()?.contains(permission) ?: defValue
        }
    }

    fun setPermissions(permissions: Set<String>?) {
        prefsEditor?.putStringSet(KEY_PERMISSIONS, permissions)?.apply()
    }

    private fun getPermissions(): Set<String>? {
        return sharedPref?.getStringSet(KEY_PERMISSIONS, null)
    }

    fun hasRecordActivityPermission(): Boolean {
        return SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE) == AppConstant.ACCESS_TYPE_MASTER ||
                getPermissions()?.contains(AppConstant.CREATE_CUSTOMER_PERMISSION) ?: false ||
                getPermissions()?.contains(AppConstant.CREATE_LEAD_PERMISSION) ?: false ||
                getPermissions()?.contains(AppConstant.CREATE_ORDER_PERMISSION) ?: false ||
                getPermissions()?.contains(AppConstant.CREATE_PAYMENT_PERMISSION) ?: false
    }

    fun clearPermissionModel() {
        prefsEditor?.clear()?.apply()
    }
}