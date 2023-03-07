package com.example.sensors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager

import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity()  {
        
       	private lateinit var model: ModelFacade
       	
       	override fun onCreate(savedInstanceState: Bundle?) {
       	      super.onCreate(savedInstanceState)
       	      setContentView(R.layout.activity_main)
       	
       	      val myPagerAdapter = PagerAdapter(this, supportFragmentManager)
       	      val viewpager: ViewPager = findViewById(R.id.view_pager)
       	      viewpager.adapter = myPagerAdapter
       	      val tabs: TabLayout = findViewById(R.id.tabs)
       	      tabs.setupWithViewPager(viewpager)
       	      model = ModelFacade.getInstance(this)
       	}
       	

    }
