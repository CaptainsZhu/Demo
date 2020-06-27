package com.zjncodetest.arouter.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.zjncodetest.R
import com.zjncodetest.arouter.PATH_AROUTER_DEMO_FRAG

@Route(path = PATH_AROUTER_DEMO_FRAG)
class ARouterDemoFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_demo, container, false)
    }
}