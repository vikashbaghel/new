package com.app.rupyz.sales.login

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.app.rupyz.R
import com.app.rupyz.databinding.ActivityOrganizationOtpBinding
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.helper.ButtonStyleHelper
import com.app.rupyz.generic.toast.MessageHelper
import com.app.rupyz.generic.utils.*
import com.app.rupyz.generic.utils.AppConstant.PROFILE_IMAGE
import com.app.rupyz.generic.utils.AppConstant.STAFF_HIERARCHY
import com.app.rupyz.generic.utils.AppConstant.USER_ID
import com.app.rupyz.generic.utils.SharePrefConstant.FCM_TOKEN
import com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN
import com.app.rupyz.generic.utils.SharePrefConstant.LEGAL_NAME
import com.app.rupyz.generic.utils.SharePrefConstant.ORG_ID
import com.app.rupyz.generic.utils.SharePrefConstant.TOKEN
import com.app.rupyz.generic.utils.SharePrefConstant.USER_INFO
import com.app.rupyz.model_kt.LoginModel
import com.app.rupyz.model_kt.WhatsAppPreferences
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.organization.ChooseOrganizationActivity
import com.app.rupyz.ui.more.MoreViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import java.util.regex.Pattern

class OtpActivityKt : BaseActivity() {
    private lateinit var binding: ActivityOrganizationOtpBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var moreViewModel: MoreViewModel

    private var mUtil: Utility? = null
    private var otpRef: String? = null
    private var smsBroadcastReceiver: SmsBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moreViewModel = ViewModelProvider(this)[MoreViewModel::class.java]
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        mUtil = Utility(this)
        otpRef = intent.getStringExtra("otp_ref")

        initObservers()
        initLayout()
        initTimer()
        startSmartUserConsent()
        openSomeActivityForResult()
    }

    private fun startSmartUserConsent() {
        SmsRetriever.getClient(this)
    }

    private fun openSomeActivityForResult() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent) {
                    someActivityResultLauncher.launch(intent)
                }

                override fun onFailure() {
                    Log.e("DEBUG", "Failed:: smsBroadcastReceiverListener")
                }
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    private fun initTimer() {
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished / 1000 > 0) {
                    binding.counterLayout.visibility = View.VISIBLE
                    binding.txtCounter.text = "" + millisUntilFinished / 1000
                    binding.btnResend.visibility = View.GONE
                }
            }

            override fun onFinish() {
                binding.counterLayout.visibility = View.INVISIBLE
                binding.btnResend.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun initLayout() {
        binding.txtMobileNumber.text = intent.getStringExtra("username")

        binding.txtOtpOne.otpListener = object : OTPListener {
            override fun onInteractionListener() {}
            override fun onOTPComplete(otp: String) {
                Utils.hideKeyboard(this@OtpActivityKt)
            }
        }

        binding.btnResend.setOnClickListener {

            binding.btnResend.isEnabled = false
            val model = LoginModel()
            model.username = binding.txtMobileNumber.text.toString()
            model.accessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
            model.isSmartMatch = true
            loginViewModel.initiateLogin(model)
        }

        binding.btnContinue.setOnClickListener {
            initRequest()
        }

        binding.btnGoBack.setOnClickListener {
            finish()
        }
    }

    private fun initRequest() {
        if (binding.txtOtpOne.otp.isEmpty()) {
            Toast.makeText(this, "OTP Required", Toast.LENGTH_SHORT).show()
        } else if (binding.txtOtpOne.otp.length == 4) {
            ButtonStyleHelper(this).initButton(false, binding.btnContinue, "Please wait...")

            val model = LoginModel()
            model.username = binding.txtMobileNumber.text.toString()
            model.otp = binding.txtOtpOne.otp
            model.otpRef = otpRef
            model.accessType = SharedPref.getInstance().getString(AppConstant.APP_ACCESS_TYPE)
            model.isSmartMatch = true
            model.termsCondition = true

            val pref = WhatsAppPreferences()
            pref.whatsappOtpIn =
                intent.getBooleanExtra(AppConstant.IS_WHATS_APP_NOTIFICATION, false)
            model.preferences = pref

            loginViewModel.verifyOtp(model)
        } else {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
        }
    }

    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                if (intent.hasExtra(SmsRetriever.EXTRA_SMS_MESSAGE)) {
                    val message = intent.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                    getOtpFromMessage(message!!)
                }
            }
        }
    }

    private fun getOtpFromMessage(message: String) {
        val otpPattern = Pattern.compile("(|^)\\d{4}")
        val matcher = otpPattern.matcher(message)
        if (matcher.find()) {
            if (message.contains("RUPYZ")) {
                binding.txtOtpOne.otp = matcher.group(0)
                binding.btnContinue.performClick()
            }
        } else {
            binding.txtOtpOne.otp = ""
        }
    }


    private fun initObservers() {
        moreViewModel.preferenceLiveData.observe(this) {
            if (it.error == false) {
                if (it.data?.locationTracking != null) {
                    SharedPref.getInstance().putBoolean(
                        AppConstant.LOCATION_TRACKING, it.data.locationTracking!!
                    )
                }
                val intent = Intent(this@OtpActivityKt, SalesMainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                showToast(it.message)
            }
        }

        loginViewModel.initiateLoginLiveData.observe(this) {
            binding.btnResend.isEnabled = true
            if (it.error == false) {
                it.data?.let { mUserData ->
                    initTimer()
                    otpRef = mUserData.otpRef
                }
            } else {
                showToast(it.message ?: resources.getString(R.string.something_went_wrong))
            }
        }

        loginViewModel.loggedInLiveData.observe(this) {
            ButtonStyleHelper(this).initButton(
                true, binding.btnContinue, resources.getString(R.string.str_continue)
            )

            if (it.error == false) {
                it.data?.let { mUserData ->
                    if (mUserData.credentials?.accessToken != null) {
                        SharedPref.getInstance().putString(TOKEN, mUserData.credentials.accessToken)
                    }

                    SharedPref.getInstance().putModelClass(USER_INFO, mUserData)

                    if (isStaffUser) {
                        if (mUserData.orgId != null) {
                            SharedPref.getInstance().putInt(ORG_ID, mUserData.orgId)
                        }
                        SharedPref.getInstance().putBoolean(IS_LOGIN, true)
                        SharedPref.getInstance()
                            .putString(LEGAL_NAME, mUserData.firstName + " " + mUserData.lastName)
                        SharedPref.getInstance().putString(USER_ID, mUserData.userId)
                        SharedPref.getInstance()
                            .putInt(AppConstant.STAFF_ID, mUserData.staffId ?: 0)

                        SharedPref.getInstance().putString(PROFILE_IMAGE, mUserData.profilePicUrl)
                        SharedPref.getInstance().putBoolean(STAFF_HIERARCHY, mUserData.hierarchy)

                        moreViewModel.getPreferencesInfo()
                    } else {
                        val intent =
                            Intent(this@OtpActivityKt, ChooseOrganizationActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    initFCM()
                }
            } else {
                showToast(it.message ?: resources.getString(R.string.something_went_wrong))
            }
        }
    }

    private fun initFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("DEBUG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            val json = JsonObject()
            json.addProperty("device_type", "Android")
            json.addProperty("device_manufacture", Build.MANUFACTURER)
            json.addProperty("os_type", "Android SDK ${Build.VERSION.RELEASE}")
            json.addProperty("device_model", Build.MODEL)

            json.addProperty("fcm_token", token)
            loginViewModel.saveFcm(json)
        })

    }

    override fun onStart() {
        super.onStart()
        openSomeActivityForResult()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }
}