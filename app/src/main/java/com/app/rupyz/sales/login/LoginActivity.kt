package com.app.rupyz.sales.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityLoginBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.base.BrowserActivity
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.network.ApiClient
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.generic.utils.Utils
import com.app.rupyz.model_kt.LoginModel
import com.app.rupyz.model_kt.WhatsAppPreferences
import com.google.firebase.analytics.FirebaseAnalytics

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginModelForAccess: LoginViewModel
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var userActionOnCheck: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginModelForAccess = ViewModelProvider(this)[LoginViewModel::class.java]

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        initObserver()
        initLayout()
        requestSmsPermission()

        val params = Bundle()
        params.putString("mobile_number_screen", "open mobile number screen")
        mFirebaseAnalytics!!.logEvent("mobile_number", params)
    }

    private fun initLayout() {

        SharedPref.getInstance()
            .putString(AppConstant.APP_ACCESS_TYPE, AppConstant.ACCESS_TYPE_STAFF)

        binding.tvAdmin.setOnClickListener {
            binding.tvAdmin.setBackgroundResource(R.drawable.btn_gredient)
            binding.tvStaff.setBackgroundResource(R.drawable.white_round_product_bg)

            binding.tvAdmin.setTextColor(resources.getColor(R.color.white))
            binding.tvStaff.setTextColor(resources.getColor(R.color.black))

            SharedPref.getInstance()
                .putString(AppConstant.APP_ACCESS_TYPE, AppConstant.ACCESS_TYPE_MASTER)

            binding.chbWhatsapp.setOnCheckedChangeListener(null)
            binding.chbWhatsapp.isChecked = userActionOnCheck ?: true

            binding.chbWhatsapp.setOnCheckedChangeListener { _, isChecked ->
                userActionOnCheck = isChecked
            }
            binding.llWhatsAppConsent.visibility = View.VISIBLE

        }

        binding.tvStaff.setOnClickListener {
            binding.tvAdmin.setBackgroundResource(R.drawable.white_round_product_bg)
            binding.tvStaff.setBackgroundResource(R.drawable.btn_gredient)

            binding.tvAdmin.setTextColor(resources.getColor(R.color.black))
            binding.tvStaff.setTextColor(resources.getColor(R.color.white))

            SharedPref.getInstance()
                .putString(AppConstant.APP_ACCESS_TYPE, AppConstant.ACCESS_TYPE_STAFF)

            binding.chbWhatsapp.setOnCheckedChangeListener(null)
            binding.chbWhatsapp.isChecked = userActionOnCheck ?: false

            binding.chbWhatsapp.setOnCheckedChangeListener { _, isChecked ->
                userActionOnCheck = isChecked
            }
            binding.llWhatsAppConsent.visibility = View.GONE
        }

        binding.txtMobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.toString().isNotEmpty() && charSequence.toString().length == 10) {
                    Utils.hideKeyboard(this@LoginActivity)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        binding.btnTerms.setOnClickListener {
            initOpenBrowser(ApiClient.TERMS_URL, "Terms of Service")
        }

        binding.btnPolicy.setOnClickListener {
            initOpenBrowser(ApiClient.POLICY_URL, "Privacy Policy")
        }

        binding.btnLogin.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.txtMobileNumber.text.toString().isEmpty()
                .not() && binding.txtMobileNumber.text.toString().length == 10
        ) {
            if (binding.chbTerms.isChecked) {
                doLogin()
            } else {
                showToast(resources.getString(R.string.terms_conditions_validation))
            }
        } else {
            showToast(resources.getString(R.string.mobile_number_validation))
        }
    }

    private fun doLogin() {
        ButtonStyleHelper(this).initButton(
            false, binding.btnLogin,
            "Please wait..."
        )


        val model = LoginModel()

        model.username = binding.txtMobileNumber.text.toString()
        model.accessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
        model.isSmartMatch = true

        val pref = WhatsAppPreferences()
        pref.whatsappOtpIn = binding.chbWhatsapp.isChecked
        model.preferences = pref

        loginModelForAccess.initiateLogin(model)
    }

    private fun initObserver() {
        loginModelForAccess.initiateLoginLiveData.observe(this) {
            ButtonStyleHelper(this).initButton(
                true, binding.btnLogin,
                resources.getString(R.string.str_continue)
            )

            if (it.error == false) {
                it.data?.let { mUserData ->
                    val intent = Intent(this@LoginActivity, OtpActivityKt::class.java)
                    intent.putExtra("otp_ref", mUserData.otpRef)
                    intent.putExtra("username", binding.txtMobileNumber.text.toString())
                    intent.putExtra(
                        AppConstant.IS_WHATS_APP_NOTIFICATION,
                        binding.chbWhatsapp.isChecked
                    )
                    startActivity(intent)
                }
            } else {
                showToast(it.message ?: resources.getString(R.string.something_went_wrong))
            }
        }
    }

    private fun requestSmsPermission() {
        val permission = Manifest.permission.RECEIVE_SMS
        val grant = ContextCompat.checkSelfPermission(this, permission)
        //check if read SMS permission is granted or not
        if (grant != PackageManager.PERMISSION_GRANTED) {
            val permissionList = arrayOfNulls<String>(1)
            permissionList[0] = permission
            ActivityCompat.requestPermissions(this, permissionList, 1)
        }
    }

    private fun initOpenBrowser(url: String, title: String) {
        val intent = Intent(this, BrowserActivity::class.java)
        intent.putExtra("url", url)
        intent.putExtra("title", title)
        startActivity(intent)
    }
}