package com.app.rupyz

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.app.rupyz.generic.base.BaseActivity
import com.app.rupyz.generic.model.forcedUpdate.UpdateInfoModel
import com.app.rupyz.generic.model.forcedUpdate.UpdateResponse
import com.app.rupyz.generic.model.maintenance.MaintenanceViewModel
import com.app.rupyz.generic.network.ApiClient
import com.app.rupyz.generic.network.ApiInterface
import com.app.rupyz.generic.utils.AppConstant
import com.app.rupyz.generic.utils.Connectivity
import com.app.rupyz.generic.utils.SharePrefConstant.IS_LOGIN
import com.app.rupyz.generic.utils.SharedPref
import com.app.rupyz.sales.customer.CustomerFeedbackDetailActivity
import com.app.rupyz.sales.home.SalesMainActivity
import com.app.rupyz.sales.lead.LeadDetailsActivity
import com.app.rupyz.sales.login.LoginActivity
import com.app.rupyz.sales.orderdispatch.OrderDispatchHistoryActivity
import com.app.rupyz.sales.orders.OrderDetailActivity
import com.app.rupyz.sales.payment.PaymentDetailsActivity
import com.app.rupyz.ui.common.ErrorMessageActivity
import com.app.rupyz.ui.common.MaintenanceActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.toString as toString1

class InitialActivity : AppCompatActivity() {
    private var mKeyguardManager: KeyguardManager? = null
    private var mApiInterface: ApiInterface? = null
    private var maintenanceViewModel: MaintenanceViewModel? = null
    var forceUpdateValue = false
    private var someActivityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mApiInterface = ApiClient.getRetrofit().create(
                ApiInterface::class.java
        )

        MyApplication.instance.setPerformedValue(false)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        mKeyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager

        someActivityResultLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        initMain()
                    } else {
                        finish()
                    }
                }
    }

    private fun forceUpdate() {
        val callF = mApiInterface!!.checkUpdate(BuildConfig.VERSION_CODE, "ANDROID")
        callF.enqueue(object : Callback<UpdateResponse?> {
            override fun onResponse(
                    call: Call<UpdateResponse?>,
                    response: Response<UpdateResponse?>
            ) {
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val updateResponse = response.body()
                        for (i in updateResponse!!.data.indices) {
                            if (updateResponse.data[i].isForcedUpdate) {
                                forceUpdateValue = updateResponse.data[i].isForcedUpdate
                                updateApp(updateResponse.data[i])
                                break
                            }
                        }
                        if (!forceUpdateValue) {
                            checkHealth()
                        }
                    } else {
                        Toast.makeText(
                                this@InitialActivity,
                                "Something went wrong in this version of the app. Please update the app!!",
                                Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateResponse?>, t: Throwable) {
                val mainIntent = Intent(this@InitialActivity, ErrorMessageActivity::class.java)
                this@InitialActivity.startActivity(mainIntent)
                finish()
            }
        })
    }

    private fun checkHealth() {
        val call1 = mApiInterface!!.maintenance
        call1.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                if (response.code() == 200) {
                    val jsonParser = JsonParser()
                    val jsonObj = jsonParser.parse(response.body()) as JsonObject
                    val gson = Gson()
                    maintenanceViewModel =
                            gson.fromJson(jsonObj["data"], MaintenanceViewModel::class.java)
                    if (maintenanceViewModel?.isMaintenance_status == 1) {
                        val mainIntent =
                                Intent(this@InitialActivity, MaintenanceActivity::class.java)
                        this@InitialActivity.startActivity(mainIntent)
                        finish()
                    } else {
                        if (SharedPref.getInstance().getBoolean(IS_LOGIN, false)) {
//                            if (BuildConfig.DEBUG) {
                            initMain()
//                            } else {
//                                initBio()
//                            }
                        } else {
                            val mainIntent = Intent(this@InitialActivity, LoginActivity::class.java)
                            this@InitialActivity.startActivity(mainIntent)
                            finish()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Toast.makeText(
                        this@InitialActivity,
                        "Something Went Wrong In Health Check For This App",
                        Toast.LENGTH_SHORT
                ).show()
                call.cancel()
            }
        })
    }

    private fun initBio() {
        if (mKeyguardManager?.isKeyguardSecure == false) {
            initMain()
        } else {
            val biometricManager = BiometricManager.from(this)
            when (biometricManager.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {

                }

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Log.e("DEBUG", "CALLING FROM BIOMETRIC_ERROR_NONE_ENROLLED")
                    openSomeActivityForResult()
                }
            }

            // creating a variable for our Executor
            val executor = ContextCompat.getMainExecutor(this)
            // this will give us result of AUTHENTICATION
            val biometricPrompt = BiometricPrompt(
                    this@InitialActivity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            super.onAuthenticationError(errorCode, errString)
                            if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                Log.e("DEBUG", "CALLING FROM ERROR_NEGATIVE_BUTTON")
                                openSomeActivityForResult()
                            }
                            if (errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                                val intent =
                                        Intent(this@InitialActivity, SecurityReadWriteActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            initMain()
                        }
                    })
            val promptInfo = PromptInfo.Builder().setTitle("Unlock Rupyz")
                    .setDescription("Confirm your phone screen lock pattern, PIN or password ")
                    .setNegativeButtonText("Use PIN").build()
            biometricPrompt.authenticate(promptInfo)
        }
    }

    fun openSomeActivityForResult() {
        val intent = mKeyguardManager?.createConfirmDeviceCredentialIntent(null, null)
        if (intent != null) {
            if (someActivityResultLauncher != null) {
                try {
                    someActivityResultLauncher?.launch(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                initMain()
            }
        }
    }

    private fun initMain() {
        if (SharedPref.getInstance().getBoolean(IS_LOGIN, false)) {
            if (intent.hasExtra("module_name")) {
                navigatePushNotification()
            } else {
                val mainIntent = Intent(this, SalesMainActivity::class.java)
                startActivity(mainIntent)
                finish()
            }
        } else {
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }

    private fun navigatePushNotification() {
        var newIntent = Intent()
        val moduleUid = intent.extras?.getString("module_uid").toString1().toInt()
        if (intent.extras?.getString("module_name") != null) {
            when (intent.extras?.getString("module_name").toString1()) {
                "ORDER-DISPATCH" -> {
                    run {
                        newIntent = Intent(this, OrderDispatchHistoryActivity::class.java)
                        if (intent.extras?.getString("parent_module_uid") != null) {
                            newIntent.putExtra(
                                    AppConstant.ORDER_ID,
                                    intent.extras?.getString("parent_module_uid").toString1()
                            )
                        }
                        newIntent.putExtra(AppConstant.DISPATCH_ID, moduleUid)
                        newIntent.putExtra(AppConstant.ORDER_CLOSE, false)
                        newIntent.putExtra(AppConstant.NOTIFICATION, true)
                    }
                }

                "ORDER" -> {
                    run {
                        newIntent = Intent(this, OrderDetailActivity::class.java)
                        newIntent.putExtra(AppConstant.ORDER_ID, moduleUid)
                        newIntent.putExtra(AppConstant.NOTIFICATION, true)
                    }
                }

                "CUSTOMER-PAYMENT" -> {
                    run {
                        newIntent = Intent(this, PaymentDetailsActivity::class.java)
                        newIntent.putExtra(AppConstant.PAYMENT_ID, moduleUid)
                        newIntent.putExtra(AppConstant.NOTIFICATION, true)
                    }
                }

                "LEAD" -> {
                    run {
                        newIntent = Intent(this, LeadDetailsActivity::class.java)
                        newIntent.putExtra(AppConstant.LEAD_ID, moduleUid)
                        newIntent.putExtra(AppConstant.NOTIFICATION, true)
                    }
                }

                "LEAD-FEEDBACK", "CUSTOMER-FEEDBACK", "FOLLOWUP-REMINDERS" -> {
                    run {
                        newIntent = Intent(this, CustomerFeedbackDetailActivity::class.java)
                        if (intent.extras?.getString("parent_module_uid") != null) {
                            newIntent.putExtra(
                                    AppConstant.ACTIVITY_ID,
                                    intent.extras?.getString("parent_module_uid").toString1().toInt()
                            )
                        }
                        if (intent.extras?.getString("org_id") != null) {
                            newIntent.putExtra(
                                    AppConstant.ORGANIZATION,
                                    intent.extras?.getString("org_id").toString1().toInt()
                            )
                        }
                        newIntent.putExtra(AppConstant.NOTIFICATION, true)
                    }
                }

                else -> {
                    newIntent = if (SharedPref.getInstance().getBoolean(IS_LOGIN, false)) {
                        Intent(this, SalesMainActivity::class.java)
                    } else {
                        Intent(this, LoginActivity::class.java)
                    }
                }
            }
        }

        // Here FLAG_ACTIVITY_CLEAR_TOP flag is set to clear
        // the activities present in the activity stack,
        // on the top of the Activity that is to be launched
        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        Log.e("DEBUG", "CALLING FROM MAIN INITIAL")
        startActivity(newIntent)
        finish()
    }

    private fun updateApp(model: UpdateInfoModel) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Update Rupyz")
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setIcon(R.drawable.rupyz_logo)
        alertDialogBuilder.setMessage("Rupyz recommends that you update to the latest version.")
        alertDialogBuilder.setPositiveButton("Update") { dialog: DialogInterface?, id: Int ->
            val appPackageName = packageName // getPackageName() from Context or Activity object
            try {
                startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=$appPackageName")
                        )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                        )
                )
            }
        }
        if (!model.isForcedUpdate) {
            alertDialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface?, id: Int ->
//                if (BuildConfig.DEBUG) {
                initMain()
//                } else {
//                    initBio()
//                }
            }
        }
        alertDialogBuilder.show()
    }

    override fun onResume() {
        super.onResume()
        if (Connectivity.hasInternetConnection(this)) {
            forceUpdate()
        } else {
            if (BuildConfig.DEBUG) {
                initMain()
            } else {
                initBio()
            }
        }
    }
}