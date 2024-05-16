package com.enkefalostechnologies.calendarpro.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.devicelock.DeviceId
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.exceptions.invalidstate.SignedInException
import com.amplifyframework.auth.cognito.exceptions.service.InvalidPasswordException
import com.amplifyframework.auth.cognito.exceptions.service.UserNotConfirmedException
import com.amplifyframework.auth.exceptions.NotAuthorizedException
import com.amplifyframework.auth.exceptions.SignedOutException
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.query.Where
import com.amplifyframework.datastore.DataStoreException
import com.amplifyframework.datastore.generated.model.SubscriptionStatus
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.constant.Constants
import com.enkefalostechnologies.calendarpro.constant.StorageConstant
import com.enkefalostechnologies.calendarpro.databinding.ActivityLoginBinding
import com.enkefalostechnologies.calendarpro.databinding.MsgDialogBinding
import com.enkefalostechnologies.calendarpro.model.User
import com.enkefalostechnologies.calendarpro.router.Router.navigateToHome
import com.enkefalostechnologies.calendarpro.router.Router.navigateToUserConfirmationScreen
import com.enkefalostechnologies.calendarpro.router.Router.navigateToUserConfirmationScreenForLogin
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.ui.forgot_password.ForgotPasswordActivity
import com.enkefalostechnologies.calendarpro.ui.forgot_password.ForgotPasswordOtpVerificationScreen
import com.enkefalostechnologies.calendarpro.ui.userconfirmation.UserConfirmationScreen
import com.enkefalostechnologies.calendarpro.ui.viewModel.LoginActivityViewModel
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.facebookLogin
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.googleLogin
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.handleExceptions
import com.enkefalostechnologies.calendarpro.util.AmplifyUtil.loginThroughEmail
import com.enkefalostechnologies.calendarpro.util.AppUtil.getAndroidId
import com.enkefalostechnologies.calendarpro.util.AppUtil.isNetworkAvailable
import com.enkefalostechnologies.calendarpro.util.AppUtil.showToast
import com.enkefalostechnologies.calendarpro.util.DialogUtil.dialogLoading
import com.enkefalostechnologies.calendarpro.util.Extension.close
import com.enkefalostechnologies.calendarpro.util.Extension.gone
import com.enkefalostechnologies.calendarpro.util.Extension.visible
import com.enkefalostechnologies.calendarpro.util.MsgAlertDialogListener
import com.enkefalostechnologies.calendarpro.util.ValidationUtil.validateEmail
import com.enkefalostechnologies.calendarpro.util.ValidationUtil.validateField
import com.enkefalostechnologies.calendarpro.util.ValidationUtil.validatePassword
import com.enkefalostechnologies.calendarpro.util.dialog.messageAlertDialog
import java.lang.Exception


class LoginActivity : BaseActivity<ActivityLoginBinding>(R.layout.activity_login), TextWatcher {
    private var isLoginPasswordVisible = false
    private var isSignupPasswordVisible = false
    private var userData: User? = null

    val viewModel: LoginActivityViewModel by viewModels { LoginActivityViewModel.Factory }
    private var listener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            displayLoginScreen(!binding.llLogin.isVisible)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    override fun onViewBindingCreated() {
        checkSignIn()
        initializeLoginScreen()
        initializeSignupScreen()
        if (intent.extras?.get("view") == "signup") {
            displayLoginScreen(false)
        } else {
            displayLoginScreen(true)
        }
    }

    val signoutObserver = Observer<Boolean> { isSignedOut ->
        if (isSignedOut) {
            clearLocalStorage(this)
        }
    }

    val isInitialSyncStartedObserver = Observer<Boolean> { isSyncStarted ->
        Log.d("SyncOnLogin", "isInitialSyncStarted=>$isSyncStarted")
    }
    val isInitialSyncCompletedObserver = Observer<Boolean> { isSyncCompleted ->
        Log.d("SyncOnLogin", "isInitialSyncCompleted=>$isSyncCompleted")
        userData?.let { viewModel.createUser(it) }
    }
    val initialSyncingModelObserver = Observer<String> { model ->
        Log.d("SyncOnLogin", "Syncing $model ....")
    }

    override fun addObserver() {
        binding.login.etEmail.addTextChangedListener(this)
        binding.login.etPassword.addTextChangedListener(this)
        binding.signup.etName.addTextChangedListener(this)
        binding.signup.etEmail.addTextChangedListener(this)
        binding.signup.etPassword.addTextChangedListener(this)
        viewModel.signOutConfirmation().observe(this, signoutObserver)
        viewModel.isUserCreated.observe(this, userCreatedObserver)
        viewModel.isTaskSyncCompleted.observe(this, taskSyncObserver)
        viewModel.isWaterInTakeSyncCompleted.observe(this, waterInTakeObserver)
        viewModel.isKnowYourDayCompleted.observe(this, knowYourDayObserver)
        viewModel.isListGroupSyncCompleted.observe(this, listSyncObserver)
        viewModel.userAttributes.observe(this, userAttributeObserver)
        viewModel.SignInResultConfirmation.observe(this, signInResultConfirmationObserver)
        viewModel.error().observe(this, onErrorObserver)
        viewModel.isInitialSyncStarted.observe(this, isInitialSyncStartedObserver)
        viewModel.isInitialSyncCompleted.observe(this, isInitialSyncCompletedObserver)
        viewModel.initialSyncingModel.observe(this, initialSyncingModelObserver)
    }

    val userCreatedObserver =
        Observer<com.amplifyframework.datastore.generated.model.User?> { user ->
            Log.d("syncing", "------------isUserCreated==>${user}-------------------")
            if (user != null) {
                setUserPicUrl(user.picUrl)
                setUserName(user.name)
                setUserEmail(user.email)
                setIsUserEmailVerified(user.isEmailVerified)
                saveSubscribedPlan(user.subscriptionType)
                setSubscriptionStatus(user.subscriptionStatus)
                setUserLoggedIn(true)
                setIsSocialLoggedIn(user.isSocialLoggedIn)
                user.countrySelected?.let {
                    setCountries(it)
                }

                Log.d("syncing", "------------Task Syncing initiated-------------------")
//
                Amplify.DataStore.query(
                    com.amplifyframework.datastore.generated.model.User::class.java,
                    Where.matches(
                        com.amplifyframework.datastore.generated.model.User.EMAIL.eq(getUserEmail())
                            .and(
                                com.amplifyframework.datastore.generated.model.User.COUNTRY_SELECTED.ne(
                                    ""
                                )
                                    .or(
                                        com.amplifyframework.datastore.generated.model.User.COUNTRY_SELECTED.ne(
                                            null
                                        )
                                            .or(
                                                com.amplifyframework.datastore.generated.model.User.COUNTRY_SELECTED.ne(
                                                    "ADD"
                                                )
                                            )
                                    )
                            )
                    ), {
                        runOnUiThread {
                            if (it.hasNext()) {
                                viewModel.syncTasks(getUserEmail(), getAndroidId(), false)
                            } else {
                                viewModel.syncTasks(getUserEmail(), getAndroidId(), true)
                                updateCountrySelected()
                                deleteUserByDeviceId(getAndroidId())
                            }
                        }
                    }, {
                        runOnUiThread {
                            viewModel.syncTasks(getUserEmail(), getAndroidId(), false)
                        }
                    }
                )


//                viewModel.syncTasks(getUserId(), deviceId())
//                amplifyDataModelUtil.syncTasks(viewModel.user?.userId?:"", deviceId())
//                amplifyDataModelUtil.syncWaterIntake(viewModel.user?.userId?:"", deviceId())
//                amplifyDataModelUtil.syncKnowYourDay(viewModel.user?.userId?:"", deviceId())
//                amplifyDataModelUtil.syncListGroups(viewModel.user?.userId?:"", deviceId())
//                clearFields()
//                navigateToHome(binding.login.imageView2)
            }
        }
    val taskSyncObserver = Observer<Boolean> { isCompleted ->
        Log.d("syncing", "isTaskSyncCompleted==>${isCompleted}")
        if (isCompleted) {
            Log.d("syncing", "------------Task Syncing finished-------------------")
            Log.d("syncing", "------------WaterIntake Syncing initiated-------------------")
            viewModel.syncWaterIntake(getUserEmail(), getAndroidId())
        }
    }
    val waterInTakeObserver = Observer<Boolean> { isCompleted ->
        Log.d("syncing", "isWaterInTakeSyncCompleted==>${isCompleted}")
        if (isCompleted) {
            Log.d("syncing", "------------WaterIntake finished-------------------")
            Log.d("syncing", "------------KnowYourDay Syncing initiated-------------------")
            viewModel.syncKnowYourDay(getUserEmail(), getAndroidId())
        }
    }
    val knowYourDayObserver = Observer<Boolean> { isCompleted ->
        Log.d("syncing", "isKnowYourDayCompleted==>${isCompleted}")
        if (isCompleted) {
            Log.d("syncing", "------------KnowYourDay finished-------------------")
            Log.d("syncing", "------------ListGroups( Syncing initiated-------------------")
            viewModel.syncListGroups(getUserEmail(), getAndroidId())
        }
    }
    val listSyncObserver = Observer<Boolean> { isCompleted ->
        Log.d("syncing", "isListGroupSyncCompleted==>${isCompleted}")
        if (isCompleted) {
            Log.d("syncing", "------------ListGroups finished-------------------")
            dialog.close()
            clearFields()
            Log.d("syncing", "------------Move to Home Screen-------------------")
            navigateToHome(binding.login.imageView2)
        }
    }
    val userAttributeObserver = Observer<User> { user ->
        viewModel.user = user
        setUserLoggedIn(true)
        setUserEmail(user.email)
        Amplify.DataStore.stop({
            runOnUiThread {
                Handler(Looper.getMainLooper()).postDelayed({
                    Amplify.DataStore.start({
                        runOnUiThread {
                            userData = user
                            Log.d("syncOnLogin", "start callback")
                        }
                    }, {})
                }, 1000)
            }
        }, {})
    }
    val signInResultConfirmationObserver = Observer<Boolean> { result ->
        Log.d("syncing", "------------SignInResultConfirmation-------------------")
        viewModel.getUserAttributes()
    }
    val onErrorObserver = Observer<Exception> { ex ->
        dialog.close()
        when (ex) {
            is SignedInException -> {
                viewModel.signOut()
                showToast(Constants.TOAST_SOMETHING_WENT_WRONG)
            }

            is UserNotConfirmedException -> {
                val email = binding.login.etEmail.text.toString().trim()
                val password = binding.login.etPassword.text.toString().trim()
                messageAlertDialog(
                    MsgDialogBinding.inflate(LayoutInflater.from(this)),
                    title = "Account Verification is Required..",
                    msg = "Calendar Pro app will send OTP to $email for verifying account before login.",
                    btnText = "Continue",
                    listener = object : MsgAlertDialogListener {
                        override fun onDoneClicked() {
                            viewModel.sendUserVerificationCode(email)
                            dialog.visible()
                            viewModel.isUserConfirmationCodeSent()
                                .observe(this@LoginActivity, Observer { isSent ->
                                    if (isSent) {
                                        val intent = Intent(
                                            this@LoginActivity,
                                            UserConfirmationScreen::class.java
                                        )
                                        intent.putExtra(Constants.INTENT_EMAIL, email)
                                        intent.putExtra(Constants.INTENT_PASSWORD, password)
                                        startActivity(intent)
                                    }
                                })

                        }
                    }).show()
            }

            else -> handleExceptions(ex)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.hasExtra("view") == true) {
            if (intent.extras?.get("view") == "signup") {
                displayLoginScreen(false)
            } else {
                displayLoginScreen(true)
            }
        } else {

            intent?.let {
                dialog.visible()
                Log.d("syncing", "------------newIntent-------------------")
                Amplify.Auth.handleWebUISignInResponse(it)
            }
        }
    }

    override fun removeObserver() {
        Log.d("syncing", "remove observer")
        viewModel.isUserCreated.removeObserver(userCreatedObserver)
        viewModel.isTaskSyncCompleted.removeObserver(taskSyncObserver)
        viewModel.isWaterInTakeSyncCompleted.removeObserver(waterInTakeObserver)
        viewModel.isKnowYourDayCompleted.removeObserver(knowYourDayObserver)
        viewModel.isListGroupSyncCompleted.removeObserver(listSyncObserver)
        viewModel.userAttributes.removeObserver(userAttributeObserver)
        viewModel.signOutConfirmation().removeObserver(signoutObserver)
        viewModel.SignInResultConfirmation.removeObserver(signInResultConfirmationObserver)
        viewModel.error().removeObserver(onErrorObserver)
        binding.login.etEmail.removeTextChangedListener(this)
        binding.login.etPassword.removeTextChangedListener(this)
        binding.signup.etName.removeTextChangedListener(this)
        binding.signup.etEmail.removeTextChangedListener(this)
        binding.signup.etPassword.removeTextChangedListener(this)
        viewModel.isInitialSyncStarted.removeObserver(isInitialSyncStartedObserver)
        viewModel.isInitialSyncCompleted.removeObserver(isInitialSyncCompletedObserver)
        viewModel.initialSyncingModel.removeObserver(initialSyncingModelObserver)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onClick(p0: View?) {
        when (p0) {
            binding.login.btnLogin -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                } else if (isLoginFormValidate()) {
                    login()
                }
            }

            binding.signup.btnSignUp -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                } else if (isSignupForValidate()) {
                        Amplify.DataStore.query(com.amplifyframework.datastore.generated.model.User::class.java,Where.matches(com.amplifyframework.datastore.generated.model.User.EMAIL.eq(binding.signup.etEmail.text.toString())),{

                            runOnUiThread {
                                if(it.hasNext()){
                                    showToast("Enter Already registered.")
                                }else signup()
                            }

                        },{
                            runOnUiThread { handleExceptions(it) }

                        })


                    }
            }

            binding.login.tvForgotPassword -> {
                startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
            }

            binding.login.ibGoogle, binding.signup.ibGoogle -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                } else {
                    googleLogin()
                }

            }

            binding.login.btnGuest -> {
                finish()
            }

            binding.login.ibFacebook, binding.signup.ibFacebook -> {
                if (!isNetworkAvailable()) {
                    showToast("Please check your internet connection.")
                } else {
                    facebookLogin()
                }
            }

            binding.login.ivVisibility -> {
                if (!isLoginPasswordVisible) {
                    binding.login.etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                    binding.login.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_visible));
                    isLoginPasswordVisible = true;
                } else {
                    binding.login.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                    binding.login.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_invisible));
                    isLoginPasswordVisible = false;
                }
            }

            binding.signup.ivVisibility -> {
                if (!isSignupPasswordVisible) {
                    binding.signup.etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance();
                    binding.signup.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_visible));
                    isSignupPasswordVisible = true;
                } else {
                    binding.signup.etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance();
                    binding.signup.ivVisibility.setImageDrawable(resources.getDrawable(R.drawable.ic_pwd_invisible));
                    isSignupPasswordVisible = false;
                }
            }
        }
    }


    private fun TextView.setSpannable(text: String, listener: ClickableSpan) {
        val ss = SpannableStringBuilder(this.text.toString())

        ss.setSpan(
            listener, ss.toString().indexOf(text),
            ss.toString().indexOf(text) + text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_25282B)),
            ss.toString().indexOf(text),
            ss.toString().indexOf(text) + text.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )


        this.text = ss
        this.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun login() {
        val email = binding.login.etEmail.text.toString().trim()
        val password = binding.login.etPassword.text.toString().trim()
        dialog.visible()
        viewModel.signInUser(email, password)
    }


    fun clearFields() {
        binding.login.etEmail.setText("")
        binding.login.etPassword.setText("")
        binding.signup.etName.setText("")
        binding.signup.etEmail.setText("")
        binding.signup.etPassword.setText("")
    }

    private fun signup() {
        val name = binding.signup.etName.text.trim().toString()
        val email = binding.signup.etEmail.text.trim().toString()
        val password = binding.signup.etPassword.text.trim().toString()
        viewModel.signUp(name, email, password)
        dialog.visible()
        viewModel.getSignUpValue().observe(this, Observer { result ->
            dialog.close()
            if (result == true) {
                clearFields()
                navigateToUserConfirmationScreen(email)
            }
        })
    }

    private fun googleLogin() {
        dialog.visible()
        viewModel.googleLogin(this@LoginActivity)
    }

    private fun facebookLogin() {
        viewModel.facebookLogin(this@LoginActivity)
        dialog.visible()
    }

    private fun initializeLoginScreen() {
        binding.login.tvDontHaveAnAccount.setSpannable("Sign up", listener)
        binding.login.etEmail.setText("")
        binding.login.etPassword.setText("")
        binding.login.btnLogin.setOnClickListener(this)
        binding.login.btnGuest.setOnClickListener(this)
        binding.login.tvForgotPassword.setOnClickListener(this)
        binding.login.ibGoogle.setOnClickListener(this)
        binding.login.ibFacebook.setOnClickListener(this)
        binding.login.ivVisibility.setOnClickListener(this)
    }

    private fun initializeSignupScreen() {
        binding.signup.tvAlreadyAnAccount.setSpannable("Log in", listener)
        binding.signup.etName.setText("")
        binding.signup.etEmail.setText("")
        binding.signup.etPassword.setText("")
        binding.signup.btnSignUp.setOnClickListener(this)
        binding.signup.ibGoogle.setOnClickListener(this)
        binding.signup.ibFacebook.setOnClickListener(this)
        binding.signup.ivVisibility.setOnClickListener(this)
    }

    private fun isLoginFormValidate(): Boolean {
        val email = binding.login.etEmail.text.trim()
        val password = binding.login.etPassword.text.trim()
        if (binding.login.etEmail.text.toString().length > 256) {
            binding.login.tvEmailError.text = "Email must not be more than 256 character."
            binding.login.tvEmailError.visible()
            return false
        }
        if (email.toString().isEmpty()) {
            binding.login.tvEmailError.text = Constants.ERROR_EMAIL_REQ
            binding.login.tvEmailError.visible()
            return false
        }
        if (!email.toString().validateEmail()) {
            binding.login.tvEmailError.text = Constants.ERROR_FORM_EMAIL
            binding.login.tvEmailError.visible()
            return false
        }
        if (!password.toString().validatePassword()) {
            binding.login.tvPasswordError.text = Constants.ERROR_FORM_PASSWORD
            binding.login.tvPasswordError.visible()
            return false
        }
        return true
    }

    private fun isSignupForValidate(): Boolean {
        if (!binding.signup.etName.text.toString().validateField()) {
            binding.signup.tvNameError.text = Constants.ERROR_FORM_NAME
            binding.signup.tvNameError.visible()
            return false
        }
        if (binding.signup.etEmail.text.toString().length > 256) {
            binding.signup.tvEmailError.text = "Email must not be more than 256 character."
            binding.signup.tvEmailError.visible()
            return false
        }
        if (binding.signup.etEmail.text.toString().isEmpty()) {
            binding.signup.tvEmailError.text = Constants.ERROR_EMAIL_REQ
            binding.signup.tvEmailError.visible()
            return false
        }
        if (!binding.signup.etEmail.text.toString().validateEmail()) {
            binding.signup.tvEmailError.text = Constants.ERROR_FORM_EMAIL
            binding.signup.tvEmailError.visible()
            return false
        }
        if (!binding.signup.etPassword.text.toString().validatePassword()) {
            binding.signup.tvPasswordError.text = Constants.ERROR_FORM_PASSWORD
            binding.signup.tvPasswordError.visible()
            return false
        }
        return true
    }

    private fun displayLoginScreen(value: Boolean) {
        if (value) {
            binding.llSignup.gone()
            binding.llLogin.visible()
            binding.login.etEmail.setText("")
            binding.login.etPassword.setText("")
        } else {
            binding.signup.etName.setText("")
            binding.signup.etEmail.setText("")
            binding.signup.etPassword.setText("")
            binding.llSignup.visible()
            binding.llLogin.gone()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        binding.login.tvEmailError.gone()
        binding.login.tvPasswordError.gone()
        binding.signup.tvNameError.gone()
        binding.signup.tvEmailError.gone()
        binding.signup.tvPasswordError.gone()
    }

    override fun afterTextChanged(p0: Editable?) {}

    fun checkSignIn() {
//        //TODO: manage this function according to your need and do not all both {viewModel.checkSessionValue()} and observer at same time
//        //viewModel.checkSessionValue()
//        viewModel.getSessionValue().observe(this, Observer { check ->
//            if (check) {
//                startActivity(Intent(this, HomeActivity::class.java)).also { finishAffinity() }
//            }
//        })
        if (isUserLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java)).also { finishAffinity() }
        }
    }

//    fun fetchUserAttributesAndCreateUser() {
//        viewModel.fetchUserAttributes()
//        dialog.visible()
//    }


    fun deleteUserByDeviceId(deviceId: String) {
        Amplify.DataStore.query(com.amplifyframework.datastore.generated.model.User::class.java,
            Where.matches(com.amplifyframework.datastore.generated.model.User.DEVICE_ID.eq(deviceId)),
            {
                while (it.hasNext()) {
                    Amplify.DataStore.delete(it.next(), {}, {})
                }
            },
            {}
        )

    }

    fun updateCountrySelected() {
        Amplify.DataStore.query(
            com.amplifyframework.datastore.generated.model.User::class.java,
            Where.matches(
                com.amplifyframework.datastore.generated.model.User.DEVICE_ID.eq(getAndroidId())
            ),
            {
                if (it.hasNext()) {
                    val user = it.next()
                    amplifyDataModelUtil.updateCountrySelected(
                        email = getUserEmail(),
                        {},
                        {},
                        user.countrySelected
                    )
                }
            },
            {})
    }

}