package com.zjncodetest

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.launcher.ARouter
import com.zjncodetest.arouter.PATH_AROUTER_MAIN

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_arouter_demo).setOnClickListener {
            ARouter.getInstance().build(PATH_AROUTER_MAIN).navigation()
        }
    }
}
