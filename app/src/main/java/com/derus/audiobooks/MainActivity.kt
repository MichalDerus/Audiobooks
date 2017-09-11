package com.derus.audiobooks

import android.app.SearchManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.widget.SearchView
import com.derus.audiobooks.fragment.AudiobooksListFragment
import com.derus.audiobooks.fragment.AuthorsListFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewpager.adapter = TabsAdapter(supportFragmentManager)
        tabs.setupWithViewPager(viewpager)
        viewpager.addOnPageChangeListener(this)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        when (viewpager.currentItem) {
            0 -> menu?.setGroupVisible(R.id.menu_group, true)
            1 -> menu?.setGroupVisible(R.id.menu_group, false)
        }

        val searchManager = getSystemService(AppCompatActivity.SEARCH_SERVICE) as SearchManager
        searchView = (menu!!.findItem(R.id.search).actionView as? SearchView)!!
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        invalidateOptionsMenu()
    }

    class TabsAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int = 2
        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return AudiobooksListFragment()
                1 -> return AuthorsListFragment()
                else -> return AudiobooksListFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            when (position) {
                0 -> return "Audiobooki"
                1 -> return "Autorzy"
                else -> return ""
            }
        }
    }
}
