package com.ongraphtechnologies.mileequilizer

import android.content.Intent
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.Virtualizer
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.marcinmoskala.arcseekbar.ArcSeekBar
import com.marcinmoskala.arcseekbar.ProgressListener
import com.ongraphtechnologies.mileequilizer.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(),OnSeekBarChangeListener {
    private lateinit var binding: ActivityMainBinding

    var enabled: Switch? = null
    var enableBass: Switch? = null
    var enableVirtual:Switch? = null
    var enableLoud:Switch? = null
    var spinner: Spinner? = null

    var eq: Equalizer? = null
    var bb: BassBoost? = null
    var virtualizer: Virtualizer? = null
    var loudnessEnhancer: LoudnessEnhancer? = null

    var min_level = 0
    var max_level = 100

    val MAX_SLIDERS = 5 // Must match the XML layout

    var sliders = arrayOfNulls<SeekBar>(MAX_SLIDERS)
    var bassSlider: ArcSeekBar? = null
    var virtualSlider:ArcSeekBar? = null
    var loudSlider:ArcSeekBar? = null
    var slider_labels = arrayOfNulls<TextView>(MAX_SLIDERS)
    var num_sliders = 0
    var canEnable = false
    var eqPreset: ArrayList<String>? = null
    var spinnerPos = 0
    var dontcall = false
    var canPreset = false
    var presetView: LinearLayout? = null
    var loudnessView:LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enabled = binding.switchEnable
        enabled?.isChecked = true
        spinner = binding.spinner
        sliders[0] = binding.mySeekBar0
        slider_labels[0] = binding.centerFreq0
        sliders[1] =binding.mySeekBar1
        slider_labels[1] = binding.centerFreq1
        sliders[2] = binding.mySeekBar2
        slider_labels[2] = binding.centerFreq2
        sliders[3] = binding.mySeekBar3
        slider_labels[3] = binding.centerFreq3
        sliders[4] = binding.mySeekBar4
        slider_labels[4] = binding.centerFreq4
        bassSlider = binding.bassSeekBar
        virtualSlider = binding.virtualSeekBar
        enableBass = binding.bassSwitch
        enableVirtual = binding.virtualSwitch
        enableLoud = binding.volSwitch
        loudSlider = binding.volSeekBar
        presetView = binding.presetView
        loudnessView = binding.loudnessView
        bassSlider?.maxProgress = 1000
        virtualSlider?.maxProgress = 1000
        loudSlider?.maxProgress = 10000
        enableLoud?.isChecked = true
        enableBass?.isChecked = true
        enableVirtual?.isChecked = true
        eqPreset = java.util.ArrayList()
        val spinnerAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            eqPreset!!
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        eq = Equalizer(100, 0)
        bb = BassBoost(100, 0)
        virtualizer = Virtualizer(100, 0)
        loudnessEnhancer = LoudnessEnhancer(0)


        if (eq != null) {
            val num_bands = eq?.numberOfBands?.toInt()
            if (num_bands != null) {
                num_sliders = num_bands
            }
            val r = eq?.bandLevelRange
            min_level = r?.get(0)!!.toInt()
            max_level = r[1].toInt()
            run {
                var i = 0
                while (i < num_sliders && i < MAX_SLIDERS) {
                    val freq_range = eq?.getCenterFreq(i.toShort())
                    sliders[i]?.setOnSeekBarChangeListener(this)
                    slider_labels[i]?.text = freq_range?.let { milliHzToString(it) }
                    i++
                }
            }
            for (i in 0 until eq?.numberOfPresets!!) {
                eq?.getPresetName(i.toShort())?.let { eqPreset?.add(it) }
            }
            eqPreset?.add("Custom")
            spinner?.adapter = spinnerAdapter
        }

        initialize()

        try {
            updateUI()
            canEnable = true
        } catch (e: Throwable) {
            disableEvery()
            e.printStackTrace()
        }


        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (i < eqPreset!!.size - 1) {
                    //      Crashlytics.log(1, "OnItemSelectedListener", ""+i);
                    try {
                        eq!!.usePreset(i.toShort())
                        canPreset = true
                        spinnerPos = i
                        updateSliders()
                        saveChanges()
                    } catch (e: Throwable) {
                        disablePreset()
                        e.printStackTrace()
                    }
                } else {
                    //    Crashlytics.log(1, "OnItemSelectedListener", ""+i);
                    dontcall = true
                    spinnerPos = i
                    saveChanges()
                    applyChanges()
                    updateSliders()
                    dontcall = false
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        virtualSlider?.onProgressChangedListener = ProgressListener { j ->
            if (canEnable) {
                virtualizer!!.setStrength(j.toShort())
                saveChanges()
            } else{
                disableEvery()
            }
        }

        bassSlider?.onProgressChangedListener = ProgressListener { i ->
            if (canEnable) {
                //           Log.d("WOW", "level bass slider*************************** " + (short) i);
                bb!!.setStrength(i.toShort())
                //           Log.d("WOW", "set progress actual bass level *************************** " + bb.getRoundedStrength());
                saveChanges()
            } else{
                disableEvery()
            }
        }
        loudSlider?.onProgressChangedListener = ProgressListener { j ->
            if (canEnable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    loudnessEnhancer!!.setTargetGain(j)
                    //     Log.d("WOW", "Loudness Target gain *************************** " + loudnessEnhancer.getTargetGain());
                }
                saveChanges()
            } else{
                disableEvery()
            }
        }

        if (virtualizer != null) {
            enableVirtual?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (canEnable) {
                    if (enableVirtual?.isChecked == true) {
                        virtualizer?.enabled = true
                        virtualSlider?.setEnabled(true)
                        virtualSlider?.progressColor =
                            ContextCompat.getColor(baseContext, R.color.colorAccent)
                        saveChanges()
                    } else {
                        virtualizer?.enabled = false
                        virtualSlider?.setEnabled(false)
                        virtualSlider?.progressColor =
                            ContextCompat.getColor(baseContext, R.color.progress_gray)
                        saveChanges()
                    }
                    serviceChecker()
                } else{
                    disableEvery()
                }
            })
        }
        if (bb != null) {
            enableBass?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (canEnable) {
                    if (enableBass?.isChecked == true) {
                        bb?.enabled = true
                        bassSlider?.isEnabled = true
                        bassSlider?.progressColor =
                            ContextCompat.getColor(baseContext, R.color.colorAccent)
                        saveChanges()
                    } else {
                        bb?.enabled = false
                        bassSlider?.isEnabled = false
                        bassSlider?.progressColor =
                            ContextCompat.getColor(baseContext, R.color.progress_gray)
                        saveChanges()
                    }
                    serviceChecker()
                } else {
                    disableEvery()
                }
            })
        }
        if (loudnessEnhancer != null) {
            enableLoud?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                if (canEnable) {
                    if (enableLoud?.isChecked == true) {
                        Toast.makeText(
                            applicationContext, R.string.warning,
                            Toast.LENGTH_SHORT
                        ).show()
                        loudnessEnhancer?.enabled = true
                        loudSlider?.isEnabled = true
                        loudSlider?.progressColor =
                            ContextCompat.getColor(baseContext, R.color.colorAccent)
                        saveChanges()
                    } else {
                        loudnessEnhancer?.enabled = false
                        loudSlider?.isEnabled = false
                        loudSlider?.progressColor =
                            ContextCompat.getColor(baseContext, R.color.progress_gray)
                        saveChanges()
                    }
                    serviceChecker()
                } else{
                    disableEvery()
                }
            })
        }
        if (eq != null) {
            enabled?.setOnCheckedChangeListener { compoundButton, b ->
                if (canEnable) {
                    if (enabled?.isChecked == true) {
                        spinner?.isEnabled = true
                        eq?.enabled = true
                        saveChanges()
                        for (i in 0..4) {
                            sliders[i]!!.isEnabled = true
                        }
                    } else {
                        spinner?.isEnabled = false
                        eq?.enabled = false
                        saveChanges()
                        for (i in 0..4) {
                            sliders[i]?.isEnabled = false
                        }
                    }
                    serviceChecker()
                } else{
                    disableEvery()
                }
            }
        }
    }


    fun milliHzToString(milliHz: Int): String? {
        if (milliHz < 1000) return ""
        return if (milliHz < 1000000) "" + milliHz / 1000 + "Hz" else "" + milliHz / 1000000 + "kHz"
    }


    override fun onProgressChanged(seekBar: SeekBar, level: Int, b: Boolean) {
        if (eq != null && canEnable) {
            val new_level = min_level + (max_level - min_level) * level / 100
            for (i in 0 until num_sliders) {
                if (sliders[i] === seekBar) {
                    eq?.setBandLevel(i.toShort(), new_level.toShort())
                    saveChanges()
                    break
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        spinner!!.setSelection(eqPreset!!.size - 1)
        spinnerPos = eqPreset!!.size - 1
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this,"About clicked",Toast.LENGTH_SHORT).show()
//            val myIntent = Intent(this@MainActivity, AboutActivity::class.java)
//            this@MainActivity.startActivity(myIntent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun updateUI() {
        applyChanges()
        serviceChecker()
        if (enableBass!!.isChecked) {
            bassSlider!!.isEnabled = true
            bassSlider!!.progressColor = ContextCompat.getColor(baseContext, R.color.colorAccent)
            bb!!.enabled = true
        } else {
            bassSlider!!.isEnabled = false
            bassSlider!!.progressColor = ContextCompat.getColor(baseContext, R.color.progress_gray)
            bb!!.enabled = false
        }
        if (enableVirtual!!.isChecked) {
            virtualizer!!.enabled = true
            virtualSlider!!.progressColor = ContextCompat.getColor(baseContext, R.color.colorAccent)
            virtualSlider!!.isEnabled = true
        } else {
            virtualizer!!.enabled = false
            virtualSlider!!.isEnabled = false
            virtualSlider!!.progressColor =
                ContextCompat.getColor(baseContext, R.color.progress_gray)
        }
        if (enableLoud!!.isChecked) {
            loudnessEnhancer!!.enabled = true
            loudSlider!!.progressColor = ContextCompat.getColor(baseContext, R.color.colorAccent)
            loudSlider!!.isEnabled = true
        } else {
            loudnessEnhancer!!.enabled = false
            loudSlider!!.isEnabled = false
            loudSlider!!.progressColor = ContextCompat.getColor(baseContext, R.color.progress_gray)
        }
        if (enabled!!.isChecked) {
            spinner!!.isEnabled = true
            for (i in 0..4) sliders[i]!!.isEnabled = true
            eq!!.enabled = true
        } else {
            spinner!!.isEnabled = false
            for (i in 0..4) sliders[i]!!.isEnabled = false
            eq!!.enabled = false
        }
        spinner!!.setSelection(spinnerPos)
        updateSliders()
        updateBassBoost()
        updateVirtualizer()
        updateLoudness()
    }

    fun updateSliders() {
        for (i in 0 until num_sliders) {
            var level: Int
            level = if (eq != null) eq!!.getBandLevel(i.toShort()).toInt() else 0
            val pos = 100 * level / (max_level - min_level) + 50
            sliders[i]!!.progress = pos
        }
    }

    fun updateBassBoost() {
        if (bb != null) bassSlider!!.progress =
            bb!!.roundedStrength.toInt() else bassSlider!!.progress = 0
    }

    fun updateVirtualizer() {
        if (virtualizer != null) virtualSlider!!.progress =
            virtualizer!!.roundedStrength.toInt() else virtualSlider!!.progress = 0
    }

    fun updateLoudness() {
        if (loudnessEnhancer != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) loudSlider!!.progress =
                loudnessEnhancer!!.targetGain.toInt()
        } else loudSlider!!.progress = 0
    }

    fun saveChanges() {
        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val myEditor = myPreferences.edit()
        myEditor.putBoolean("initial", true)
        myEditor.putBoolean("eqswitch", enabled!!.isChecked)
        myEditor.putBoolean("bbswitch", enableBass!!.isChecked)
        myEditor.putBoolean("virswitch", enableVirtual!!.isChecked)
        myEditor.putBoolean("loudswitch", enableLoud!!.isChecked)
        //       Log.d("WOW", "actual bass level *************************** " + bb.getRoundedStrength());
        //       Log.d("WOW", "actual vir level *************************** " + virtualizer.getRoundedStrength());
        myEditor.putInt("spinnerpos", spinnerPos)
        try {
            if (bb != null) myEditor.putInt("bbslider", bb!!.roundedStrength.toInt()) else {
                bb = BassBoost(100, 0)
                myEditor.putInt("bbslider", bb!!.roundedStrength.toInt())
            }
            if (virtualizer != null) myEditor.putInt(
                "virslider",
                virtualizer!!.roundedStrength.toInt()
            ) else {
                virtualizer = Virtualizer(100, 0)
                myEditor.putInt("virslider", virtualizer!!.roundedStrength.toInt())
            }
            if (loudnessEnhancer != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) myEditor.putFloat(
                    "loudslider",
                    loudnessEnhancer!!.targetGain
                )
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    loudnessEnhancer = LoudnessEnhancer(0)
                    myEditor.putFloat("loudslider", loudnessEnhancer!!.targetGain)
                }
            }
        } catch (e: Throwable) {
            myEditor.putInt("bbslider", 0)
            myEditor.putInt("virslider", 0)
            myEditor.putFloat("loudslider", 0f)
            e.printStackTrace()
        }
        if (spinnerPos == eqPreset!!.size - 1 && !dontcall) {
            myEditor.putInt(
                "slider0",
                100 * eq!!.getBandLevel(0.toShort()) / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider1",
                100 * eq!!.getBandLevel(1.toShort()) / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider2",
                100 * eq!!.getBandLevel(2.toShort()) / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider3",
                100 * eq!!.getBandLevel(3.toShort()) / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider4",
                100 * eq!!.getBandLevel(4.toShort()) / (max_level - min_level) + 50
            )
        }

        //   Log.d("WOW", "spinnerPos *************************** " + spinnerPos);
        //   Log.d("WOW", "eqPreset *************************** " + (eqPreset.size() - 1));
        //    Log.d("WOW", "BAND LEVEL *************************** " + myPreferences.getInt("slider0", 0));
        myEditor.apply()
        //   Log.d("WOW", "BAND LEVEL *************************** " + myPreferences.getInt("slider0", 0));
    }


    fun applyChanges() {
        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        spinnerPos = myPreferences.getInt("spinnerpos", 0)
        enabled!!.isChecked = myPreferences.getBoolean("eqswitch", true)
        enableBass!!.isChecked = myPreferences.getBoolean("bbswitch", true)
        enableVirtual!!.isChecked = myPreferences.getBoolean("virswitch", true)
        enableLoud!!.isChecked = myPreferences.getBoolean("loudswitch", false)
        try {
            if (bb != null) bb!!.setStrength(myPreferences.getInt("bbslider", 0).toShort()) else {
                bb = BassBoost(100, 0)
                bb!!.setStrength(myPreferences.getInt("bbslider", 0).toShort())
            }
            if (virtualizer != null) virtualizer!!.setStrength(
                myPreferences.getInt("virslider", 0).toShort()
            ) else {
                virtualizer = Virtualizer(100, 0)
                virtualizer!!.setStrength(myPreferences.getInt("virslider", 0).toShort())
            }
            if (loudnessEnhancer != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) loudnessEnhancer!!.setTargetGain(
                    myPreferences.getFloat("loudslider", 0f).toInt()
                )
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    loudnessEnhancer = LoudnessEnhancer(0)
                    loudnessEnhancer!!.setTargetGain(
                        myPreferences.getFloat("loudslider", 0f).toInt()
                    )
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        if (spinnerPos != eqPreset!!.size - 1) {
            try {
                eq!!.usePreset(spinnerPos.toShort())
            } catch (e: Throwable) {
                disablePreset()
                e.printStackTrace()
            }
        } else {
            eq!!.setBandLevel(
                0.toShort(),
                (min_level + (max_level - min_level) * myPreferences.getInt(
                    "slider0",
                    0
                ) / 100).toShort()
            )
            eq!!.setBandLevel(
                1.toShort(),
                (min_level + (max_level - min_level) * myPreferences.getInt(
                    "slider1",
                    0
                ) / 100).toShort()
            )
            eq!!.setBandLevel(
                2.toShort(),
                (min_level + (max_level - min_level) * myPreferences.getInt(
                    "slider2",
                    0
                ) / 100).toShort()
            )
            eq!!.setBandLevel(
                3.toShort(),
                (min_level + (max_level - min_level) * myPreferences.getInt(
                    "slider3",
                    0
                ) / 100).toShort()
            )
            eq!!.setBandLevel(
                4.toShort(),
                (min_level + (max_level - min_level) * myPreferences.getInt(
                    "slider4",
                    0
                ) / 100).toShort()
            )
        }

        //       Log.d("WOW", "bass level *************************** " + (short) myPreferences.getInt("bbslider", 0));
        //       Log.d("WOW", "virtualizer level *************************** " + (short) myPreferences.getInt("virslider", 0));
    }

    fun disableEvery() {
        Toast.makeText(
            this, R.string.disableOther,
            Toast.LENGTH_LONG
        ).show()
        spinner?.isEnabled = false
        enabled?.isChecked = false
        enableVirtual?.isChecked = false
        enableBass?.isChecked = false
        enableLoud?.isChecked = false
        canEnable = false
        loudnessEnhancer?.enabled = false
        loudSlider?.isEnabled = false
        loudSlider?.progressColor = ContextCompat.getColor(baseContext, R.color.progress_gray)
        virtualizer?.enabled = false
        virtualSlider?.isEnabled = false
        virtualSlider?.progressColor = ContextCompat.getColor(baseContext, R.color.progress_gray)
        bassSlider?.isEnabled = false
        bassSlider?.progressColor = ContextCompat.getColor(baseContext, R.color.progress_gray)
        bb?.enabled = false
        for (i in 0..4) sliders[i]?.isEnabled = false
        eq?.enabled = false
    }

    fun disablePreset() {
        presetView?.visibility = View.GONE
        canPreset = false
    }

    fun initialize() {
        val myPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val myEditor = myPreferences.edit()
        if (!myPreferences.contains("initial")) {
            myEditor.putBoolean("initial", true)
            myEditor.putBoolean("eqswitch", false)
            myEditor.putBoolean("bbswitch", false)
            myEditor.putBoolean("virswitch", false)
            bb?.roundedStrength?.toInt()?.let { myEditor.putInt("bbslider", it) }
            myEditor.putBoolean("loudswitch", false)
            loudnessEnhancer?.targetGain?.let {
                myEditor.putFloat(
                    "loudslider",
                    it
                )
            }
            virtualizer?.roundedStrength?.toInt()?.let { myEditor.putInt("virslider", it) }
            myEditor.putInt(
                "slider0",
                100 * eq?.getBandLevel(0.toShort())!! / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider1",
                100 * eq?.getBandLevel(1.toShort())!! / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider2",
                100 * eq?.getBandLevel(2.toShort())!! / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider3",
                100 * eq?.getBandLevel(3.toShort())!! / (max_level - min_level) + 50
            )
            myEditor.putInt(
                "slider4",
                100 * eq?.getBandLevel(4.toShort())!! / (max_level - min_level) + 50
            )
            myEditor.putInt("spinnerpos", 0)
            myEditor.apply()
        }
    }

    fun serviceChecker() {
        if (enabled?.isChecked == true || enableBass?.isChecked == true || enableVirtual?.isChecked == true || enableLoud?.isChecked == true) {
            val startIntent = Intent(this@MainActivity, ForegroundService::class.java)
            startIntent.action = Constants.ACTION.STARTFOREGROUND_ACTION
            startService(startIntent)
        } else {
            val stopIntent = Intent(this@MainActivity, ForegroundService::class.java)
            stopIntent.action = Constants.ACTION.STOPFOREGROUND_ACTION
            startService(stopIntent)
        }
    }
}