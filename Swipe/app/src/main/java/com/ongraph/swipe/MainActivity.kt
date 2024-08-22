package com.ongraph.swipe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.ongraph.swipe.ui.adapter.PagerAdapter
import com.ongraph.swipe.databinding.ActivityMainBinding
import com.ongraph.swipe.ui.MiddleFragment
import com.ongraph.swipe.ui.SwipeLeftFragment
import com.ongraph.swipe.ui.SwipeRightFragment
import java.util.LinkedList
import java.util.Queue

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val middleFragment by lazy { MiddleFragment() }
    private var isScrollStart = false
    private var isLike = false
    private var isPass = false

    /**
     * A testing queue of image URLs to display.
     */
    private val testingImagesUrl: Queue<String> by lazy {
        LinkedList<String>().also {
            it.push("https://hollywoodlife.com/wp-content/uploads/2022/10/Male-Stars-In-Colored-Suits-2.jpg?w=680")
            it.push("https://scontent.fdel27-5.fna.fbcdn.net/v/t39.30808-6/295370830_111114371688152_7072251043314512845_n.jpg?_nc_cat=110&ccb=1-7&_nc_sid=6ee11a&_nc_ohc=dlAYhA8ISdwQ7kNvgHjWliX&_nc_ht=scontent.fdel27-5.fna&oh=00_AYDEVl_RTIK4_62KW7MM1NfDaqzy7D5pqqvrc7rBHHoSTg&oe=66CCDA58")
            it.push("https://beauty-around.com/images/sampledata/Hollywood_Actors/24zac-efron.jpg")
            it.push("https://media.glamour.com/photos/5695940c16d0dc3747ec5f87/master/w_1600%2Cc_limit/health-fitness-2012-11-sophia-vergara-main.jpg")
            it.push("https://beauty-around.com/images/sampledata/Hollywood_Actors/23james_franco.jpg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    /**
     * Initializes the ViewPager and sets up the page change listener.
     */
    private fun init() {
        val pagerAdapter = PagerAdapter(this)
        // swipe left fragment
        pagerAdapter.addScreenFragment(SwipeLeftFragment())
        // display user info fragment
        pagerAdapter.addScreenFragment(middleFragment)
        // swipe right fragment
        pagerAdapter.addScreenFragment(SwipeRightFragment())

        binding.viewpager.adapter = pagerAdapter

        //forcefully set the current item to 1 to display user info fragment
        binding.viewpager.currentItem = 1

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (testingImagesUrl.isNotEmpty())
                    middleFragment.setImage(testingImagesUrl.element())
                if (position == 1 && positionOffset == 0f) {
                    isScrollStart = true
                    rotation(0f)
                    if (isLike) resetDetails(true)
                    else if (isPass) resetDetails(false)
                    isLike = false
                    isPass = false
                }
                if (isScrollStart) {
                    if (position == 1 && positionOffsetPixels == 0) {
                        rotation(0f)
                        middleFragment.resetView()
                    } else {
                        middleFragment.setSwipeView()
                        if (position == 0) {
                            if (positionOffset <= 0.95) isPass = true
                            rotation(3f)
                        } else if (position == 1) {
                            if (positionOffset > 0.15) isLike = true
                            rotation(-3f)
                        }
                    }
                }
                binding.viewpager.currentItem = 1
            }
        })
    }

    /**
     * Resets the view and updates the screen based on whether the user liked or passed.
     *
     * @param isLike True if the user liked the item, false if they passed.
     */
    private fun resetDetails(isLike: Boolean) {
        binding.viewpager.currentItem = 1
        middleFragment.resetView()
        // todo : update screen & do the other job
        if (testingImagesUrl.isNotEmpty()) {
            testingImagesUrl.remove()
            visibility(testingImagesUrl.isNotEmpty())
        }
    }

    /**
     * Rotates the ViewPager.
     *
     * @param rotation The rotation angle in degrees.
     */
    private fun rotation(rotation: Float) {
        binding.viewpager.rotation = rotation
    }

    /**
     * Sets the visibility of the ViewPager based on whether there are more images to display.
     *
     * @param isVisible True to show the ViewPager, false to hide it.
     */
    private fun visibility(isVisible: Boolean) {
        if (isVisible) middleFragment.setImage(testingImagesUrl.element())
        binding.viewpager.isVisible = isVisible
    }
}