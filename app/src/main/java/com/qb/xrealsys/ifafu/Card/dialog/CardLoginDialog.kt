package com.qb.xrealsys.ifafu.Card.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.*
import com.qb.xrealsys.ifafu.Base.model.Response
import com.qb.xrealsys.ifafu.Card.controller.CardController
import com.qb.xrealsys.ifafu.Card.delegate.CardLoginCallbackDelegate
import com.qb.xrealsys.ifafu.Card.delegate.UpdateCardWebAccessStatusDelegate
import com.qb.xrealsys.ifafu.R

class CardLoginDialog(context: Context?, cardController: CardController, delegate: CardLoginCallbackDelegate) :
        Dialog(context, R.style.styleIOSDialog),
        UpdateCardWebAccessStatusDelegate,
        View.OnClickListener {

    private var cardController: CardController? = null

    private var verifyImage: ImageView? = null

    private var accountInput: TextView? = null

    private var passwordInput: EditText? = null

    private var verifyInput: EditText? = null

    private var submitBtn: Button? = null

    private var cancelBtn: Button? = null

    private var activity: Activity? = null

    private var delegate: CardLoginCallbackDelegate? = null

    init {
        this.cardController = cardController
        this.activity       = context as Activity
        this.delegate       = delegate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_card_login)

        verifyImage     = findViewById(R.id.cardLoginVerifyImage)
        accountInput    = findViewById(R.id.cardLoginAccount)
        passwordInput   = findViewById(R.id.cardLoginPassword)
        verifyInput     = findViewById(R.id.cardLoginVerify)
        submitBtn       = findViewById(R.id.cardLoginSubmit)
        cancelBtn       = findViewById(R.id.cardLoginCancel)

        submitBtn!!.setOnClickListener(this)
        cancelBtn!!.setOnClickListener(this)
        verifyImage!!.setOnClickListener(this)
        cardController!!.webUpdateDelegate = this

        cardController!!.initRouter()
        accountInput!!.text = cardController!!.user!!.account
        if (cardController!!.user!!.authPassword != null) {
            passwordInput!!.setText(cardController!!.user!!.authPassword)
        }
    }

    override fun UpdateVerifyImage(verifyCode: Bitmap) {
        this.activity!!.runOnUiThread {
            verifyImage!!.setImageBitmap(verifyCode)
        }
    }

    override fun InitRouter(suc: Boolean) {
        if (suc) {
            cardController!!.downloadVerifyImage()
        } else {
            cardController!!.authentication()
            delegate!!.CardLoginCallback()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.cardLoginSubmit -> {
                cardController!!.login(
                        accountInput!!.text.toString(),
                        passwordInput!!.text.toString(),
                        verifyInput!!.text.toString())
            }
            R.id.cardLoginCancel -> {
                cancel()
            }
            R.id.cardLoginVerifyImage -> {
                cardController!!.downloadVerifyImage()
            }
        }
    }

    override fun LoginAccessFinish(response: Response) {
        activity!!.runOnUiThread {
            if (response.isSuccess) {
                cardController!!.authentication()
                delegate!!.CardLoginCallback()
            } else {
                Toast.makeText(activity, response.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}