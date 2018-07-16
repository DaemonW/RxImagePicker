package com.qingmei2.rximagepicker_extension_wechat.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater

import com.qingmei2.rximagepicker_extension.ui.widget.MediaGrid
import com.qingmei2.rximagepicker_extension_wechat.R

class WechatMediaGrid : MediaGrid {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.wechat_media_grid_content, this, true)

        mThumbnail = findViewById(R.id.media_thumbnail)
        mCheckView = findViewById(R.id.check_view)
        mGifTag = findViewById(R.id.gif)
        mVideoDuration = findViewById(R.id.video_duration)

        mThumbnail.setOnClickListener(this)
        mCheckView.setOnClickListener(this)
    }
}
