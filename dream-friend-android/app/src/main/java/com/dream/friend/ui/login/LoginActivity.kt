package com.dream.friend.ui.login

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.dream.friend.R
import com.dream.friend.common.*
import com.dream.friend.common.Utils.dialogLoading
import com.dream.friend.common.Utils.onBackPress
import com.dream.friend.common.Utils.showToast
import com.dream.friend.data.model.login.LoginWithGoogleReq
import com.dream.friend.data.network.Resource
import com.dream.friend.databinding.ActivityLoginBinding
import com.dream.friend.ui.home.HomeActivity
import com.dream.friend.ui.onboarding.CreateProfileActivity
import com.dream.friend.ui.viewModel.UserLoginViewModel
import com.dream.friend.util.AppUtil.hideStatusBar

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel: UserLoginViewModel by viewModels()
    private val loginBinding: ActivityLoginBinding by activityBindings(R.layout.activity_login)
    lateinit var dialog: Dialog
     var email:String?=null

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideStatusBar()
        setContentView(loginBinding.root)

        dialog = dialogLoading()
        dialog.dismiss()

        googleLoginInit()

        loginBinding.btnLoginWithPhoneNumber.setOnClickListener(this)
        loginBinding.btnLoginWithGoogle.setOnClickListener(this)

        onBackPress()
        googleApiResponse()

        setSpannable()
    }

    private fun setSpannable() {
        val ss = SpannableStringBuilder(loginBinding.tvInfo.text.toString())
        val font = ResourcesCompat.getFont(this, R.font.urbanist_bold)

        val text1 = "Privacy Policy."
        loginBinding.tvInfo.text.contains(text1)
        ss.setSpan(
            policyListener, ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            CustomTypefaceSpan("", font), ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_161616)),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            UnderlineSpan(),
            ss.toString().indexOf(text1),
            ss.toString().indexOf(text1)+text1.length,
            0
        )

        val text2 = "Terms and conditions."
        loginBinding.tvInfo.text.contains(text2)
        ss.setSpan(
            termsListener, ss.toString().indexOf(text2),
            ss.toString().indexOf(text2)+text2.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            CustomTypefaceSpan("", font), ss.toString().indexOf(text2),
            ss.toString().indexOf(text2)+text2.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.color_161616)),
            ss.toString().indexOf(text2),
            ss.toString().indexOf(text2)+text2.length,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            UnderlineSpan(),
            ss.toString().indexOf(text2),
            ss.toString().indexOf(text2)+text2.length,
            0
        )

        loginBinding.tvInfo.text = ss
        loginBinding.tvInfo.movementMethod = LinkMovementMethod.getInstance()
    }


    private var termsListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            openTermsAndConditions()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    private var policyListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            openPrivacyPolicy()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    private fun openTermsAndConditions() {
        openUrl(Constants.URL_terms_of_service)
    }

    private fun openPrivacyPolicy() {
        openUrl(Constants.URL_privacy_policy)
    }
    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun googleLoginInit() {
        FirebaseApp.initializeApp(this)

        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    override fun onClick(id: View?) {
        when(id) {
            loginBinding.btnLoginWithPhoneNumber -> {
                startActivity(
                    Intent(
                        this,
                        MobileLoginActivity::class.java
                    )
                )
//                Intent(
//                    this,
//                    CreateProfileActivity::class.java
//                ).also {
//                    startActivity(it)
//                    finish()
//                }
            }

            loginBinding.btnLoginWithGoogle -> {
                loginBinding.btnLoginWithPhoneNumber.disable()
                loginBinding.btnLoginWithGoogle.disable()
                googleLoginLauncher.launch(googleSignInClient.signInIntent)
            }
        }
    }

    private val googleLoginLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            loginBinding.btnLoginWithPhoneNumber.enable()
            loginBinding.btnLoginWithGoogle.enable()
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            googleAccount(account)
        } catch (e: Exception) {
            e.printStackTrace()
            googleAccount(null)
        }
    }

    private fun googleAccount(account:GoogleSignInAccount?) {
        if (account != null) {
            val name = account.displayName
            val givenName = account.givenName
           // val email = account.email
            email=account.email
            val id = account.id
            val photo = account.photoUrl

            Log.d("Account details: ", "$name, $givenName, $email, $id, $photo")
            dialog.show()
            dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
            viewModel.userLoginWithGoogle(
                LoginWithGoogleReq(
                    name.toString(),
                    id.toString()
                )
            )
        }
    }

    private fun googleApiResponse() {
        viewModel.userLoginWithGoogleResponse.observe(this) { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        dialog.dismiss()
                        viewModel.saveUser(it.user)
                        it.accessToken?.let { it1 -> viewModel.saveUserAccessToken(it1) }
                        if (it.user.isNew) openNextScr()
                        else openHomeScr()
                    }
                }
                is Resource.Error -> {
                    dialog.dismiss()
                    response.message?.let { showToast(it) }
                }
                is Resource.Loading -> {
                    dialog.show()
                    dialog.findViewById<ImageView>(R.id.imgAnimation).startRotationAnimation()
                }
                is Resource.TokenRenew -> {}
            }
        }
    }

    private fun openNextScr() {
        Intent(
            this,
            CreateProfileActivity::class.java
        ).also {intent->
            email?.let {
                intent.putExtra("email",it)
            }
            startActivity(intent)
            finishAffinity()
        }
//        Intent(
//            this,
//            IAgreeActivity::class.java
//        ).also {
//            startActivity(it)
//            finishAffinity()
//        }
    }

    private fun openHomeScr() {
        Intent(
            this,
            HomeActivity::class.java
        ).also {
            startActivity(it)
            finishAffinity()
        }
    }
}