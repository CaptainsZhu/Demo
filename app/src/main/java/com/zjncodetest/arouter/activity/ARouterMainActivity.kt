package com.zjncodetest.arouter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.launcher.ARouter
import com.zjncodetest.R
import com.zjncodetest.REQUEST_CODE_AROUTER_DEMO_SCREEN
import com.zjncodetest.arouter.PATH_AROUTER_DEMO_FRAG
import com.zjncodetest.arouter.PATH_AROUTER_DEMO_MAIN
import com.zjncodetest.arouter.PATH_AROUTER_MAIN
import com.zjncodetest.arouter.PATH_WEBVIEW_ACTIVITY

const val AGE = "age"
const val NAME = "name"

@Route(path = PATH_AROUTER_MAIN)
class ARouterMainActivity : AppCompatActivity() {

    private val TAG = "ARouterMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arouter)

        initUri()
        initView()
    }

    private fun initUri() {
        val uri = intent.data
        uri?.let {
            ARouter.getInstance().build(uri).navigation(this, object : NavCallback() {
                override fun onArrival(postcard: Postcard) {
                    finish()
                }
            })
        }
    }

    private fun initView() {
        findViewById<TextView>(R.id.tv_demo_start_activity_for_result).setOnClickListener {
            ARouter.getInstance().build(PATH_AROUTER_DEMO_MAIN)
                .navigation(this, REQUEST_CODE_AROUTER_DEMO_SCREEN)
        }

        findViewById<TextView>(R.id.tv_demo_navigation_callback).setOnClickListener {
            ARouter.getInstance()
                .build(PATH_AROUTER_DEMO_MAIN)
                .navigation(this, object : NavCallback() {
                    override fun onFound(postcard: Postcard) {
                        Log.d(TAG, "callback: onFound")
                    }

                    override fun onLost(postcard: Postcard) {
                        Log.d(TAG, "callback: onLost")
                    }

                    override fun onArrival(postcard: Postcard) {
                        Log.d(TAG, "callback: onArrival and group is ${postcard.group}")
                    }

                    override fun onInterrupt(postcard: Postcard) {
                        Log.d(TAG, "callback: onInterrupt")
                    }
                })
        }

        findViewById<TextView>(R.id.tv_demo_pass_params).setOnClickListener {
            ARouter
                .getInstance()
                .build(PATH_AROUTER_DEMO_MAIN)
                .withInt(AGE, 18)
                .withString(NAME, "Kaka")
                .navigation()
        }

        findViewById<TextView>(R.id.tv_demo_add_fragment).setOnClickListener {
            val frag = ARouter.getInstance().build(PATH_AROUTER_DEMO_FRAG)
                .navigation()

            if (frag is Fragment) {
                val fragmentManager: FragmentManager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.add(R.id.root_container, frag)
                fragmentTransaction.commit()
            }
        }

        findViewById<TextView>(R.id.tv_demo_jump_via_uri).setOnClickListener {
            ARouter.getInstance().build(PATH_WEBVIEW_ACTIVITY).navigation(this, object : NavCallback() {
                override fun onArrival(postcard: Postcard) {
                    finish()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_AROUTER_DEMO_SCREEN -> {
                    Toast.makeText(
                        this,
                        "back from ARouter demo screen",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}