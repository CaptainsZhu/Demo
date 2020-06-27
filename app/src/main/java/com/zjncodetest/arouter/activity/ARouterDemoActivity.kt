package com.zjncodetest.arouter.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.zjncodetest.R
import com.zjncodetest.arouter.PATH_AROUTER_DEMO_MAIN

@Route(path = PATH_AROUTER_DEMO_MAIN)
public class ARouterDemoActivity : AppCompatActivity() {
    private val TAG = "ARouterDemoActivity"

    @Autowired
    @Autowired
    var name: String? = null

    @JvmField
    @Autowired
    var age: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arouter_demo)

        ARouter.getInstance().inject(this)
        val outputStr = "ext params name is $name " +
                "and age is $age"
        Log.d(TAG, outputStr)
        Toast.makeText(this, outputStr, Toast.LENGTH_SHORT).show()
        initParams()
        initView()
    }

    private fun initParams() {
//        val name = intent.getStringExtra(NAME) ?: ""
//        if (name.isNotEmpty()) {
//            val outputStr = "ext params name is $name " +
//                    "and age is ${intent.getIntExtra(AGE, -1)}"
//            Log.d(TAG, outputStr)
//            Toast.makeText(this, outputStr, Toast.LENGTH_SHORT).show()
//        }

//        val bundle = intent.extras
//        bundle?.let {
//            val name = it.getString(NAME)
//            val age = it.getInt(AGE, -1)
//            val outputStr = "ext params name is $name " +
//                    "and age is ${intent.getIntExtra(AGE, -1)}"
//            Log.d(TAG, outputStr)
//            Toast.makeText(this, outputStr, Toast.LENGTH_SHORT).show()
//        }
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_back).setOnClickListener {
            this.setResult(Activity.RESULT_OK)
            this.finish()
        }
    }
}