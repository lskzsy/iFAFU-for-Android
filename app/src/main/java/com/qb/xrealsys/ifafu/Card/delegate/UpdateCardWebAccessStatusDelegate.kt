package com.qb.xrealsys.ifafu.Card.delegate

import android.graphics.Bitmap
import com.qb.xrealsys.ifafu.Base.model.Response

interface UpdateCardWebAccessStatusDelegate {

    fun UpdateVerifyImage(verifyCode: Bitmap)

    fun InitRouter(suc: Boolean)

    fun LoginAccessFinish(response: Response)
}