package com.app.rupyz.generic.base

import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref

abstract class BaseFragment : Fragment() {

    fun disableTouch() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }

    fun enableTouch() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    open fun replaceFragment(container: Int?, fragment: Fragment?) {
        //switching fragment
        if (fragment != null) {
            childFragmentManager
                .beginTransaction()
                .replace(container!!, fragment)
                .commit()
        }
    }

    open fun startDialog(message: String?) {
        if (isAdded) {
            (requireActivity() as BaseActivity).startDialog(message)
        }
    }

    open fun stopDialog() {
        if (isAdded) {
            (requireActivity() as BaseActivity).stopDialog()
        }
    }

    open fun logout() {
        if (isAdded) {
            (requireActivity() as BaseActivity).logout()
        }
    }

    fun showToast(message: String?) {
        if (isAdded) {
            (requireActivity() as BaseActivity).showToast(message)
        }
    }

    open fun isStaffUser(): Boolean {
        val appAccessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
        return appAccessType != AppConstant.ACCESS_TYPE_MASTER
    }

    fun hideKeyboard() {
        val view = requireActivity().findViewById<View>(android.R.id.content)
        if (view != null) {
            val imm =
                requireContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hasInternetConnection(): Boolean {
        return (requireActivity() as BaseActivity).hasInternetConnection()
    }
}