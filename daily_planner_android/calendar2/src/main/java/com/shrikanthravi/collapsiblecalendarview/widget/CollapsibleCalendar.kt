package com.shrikanthravi.collapsiblecalendarview.widget

/**
 * Created by shrikanthravi on 07/03/18.
 */


import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.shrikanthravi.collapsiblecalendarview.R
import com.shrikanthravi.collapsiblecalendarview.data.CalendarAdapter
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.data.Event
import com.shrikanthravi.collapsiblecalendarview.view.BounceAnimator
import com.shrikanthravi.collapsiblecalendarview.view.ExpandIconView
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.exp


class CollapsibleCalendar : UICalendar, View.OnClickListener {
    override fun changeToToday() {
        val calendar = Calendar.getInstance()
        val calenderAdapter = CalendarAdapter(context, calendar);
        calenderAdapter.mEventList = mAdapter!!.mEventList
        calenderAdapter.setFirstDayOfWeek(firstDayOfWeek)
        val today = GregorianCalendar()
        this.selectedItem = null

        this.selectedItemPosition = -1
        this.selectedDay = Day(
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        mCurrentWeekIndex = suitableRowIndex
        setAdapter(calenderAdapter)
    }

    override fun openPopUp(v: View) {
        mListener?.onMoreIconClicked(v)
    }

    override fun onClick(view: View?) {
        view?.let {
            mListener.let { mListener ->
                if (mListener == null) {
                    expandIconView.performClick()
                } else {
                    mListener.onClickListener()
                }
            }
        }
    }

    private var mAdapter: CalendarAdapter? = null
    private var mListener: CalendarListener? = null

    var expanded = false

    private var mInitHeight = 0

    private val mHandler = Handler()
    private var mIsWaitingForUpdate = false

    var mCurrentWeekIndex: Int = 0
    var minHeight = 84

    private val suitableRowIndex: Int
        get() {
            if (selectedItemPosition != -1) {
                val view = mAdapter!!.getView(selectedItemPosition)
                val row = view.parent as TableRow

                return mTableBody.indexOfChild(row)
            } else if (todayItemPosition != -1) {
                val view = mAdapter!!.getView(todayItemPosition)
                val row = view.parent as TableRow

                return mTableBody.indexOfChild(row)
            } else {
                return 0
            }
        }

    var year: Int = 0
        get() = mAdapter!!.calendar.get(Calendar.YEAR)

    var month: Int = 0
        get() = mAdapter!!.calendar.get(Calendar.MONTH)

    /**
     * The date has been selected and can be used on Calender Listener
     */
    var selectedDay: Day? = null
        get() {
            if (selectedItem == null) {
                val cal = Calendar.getInstance()
                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH)
                val year = cal.get(Calendar.YEAR)
                return Day(
                    year,
                    month /*+ 1*/,
                    day
                )
            }
            return Day(
                selectedItem!!.year,
                selectedItem!!.month,
                selectedItem!!.day
            )
        }
        set(value: Day?) {
            field = value
            redraw()
        }

    var selectedItemPosition: Int = -1
        get() {
            var position = -1
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)

                if (isSelectedDay(day)) {
                    position = i
                    break
                }
            }
            if (position == -1) {
                position = todayItemPosition
            }
            return position
        }

    val todayItemPosition: Int
        get() {
            var position = -1
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)

                if (isToday(day)) {
                    position = i
                    break
                }
            }
            return position
        }

    override var state: Int
        get() = super.state
        set(state) {
            super.state = state
            if (state == STATE_COLLAPSED) {
                expanded = false
                expandIconView.isClickable = true
                expandIconView.isEnabled = true
            }
            if (state == STATE_EXPANDED) {
                expanded = true
                expandIconView.isClickable = true
                expandIconView.isEnabled = true
            }
        }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    fun init(context: Context) {


        val cal = Calendar.getInstance()
        val adapter = CalendarAdapter(context, cal)
        setAdapter(adapter)


        // bind events

        mBtnPrevMonth.setOnClickListener { prevMonth() }

        mBtnNextMonth.setOnClickListener { nextMonth() }

        mBtnPrevWeek.setOnClickListener { prevWeek() }

        mBtnNextWeek.setOnClickListener { nextWeek() }

        mTodayIcon.setOnClickListener { v ->
//            changeToToday()
            openPopUp(v)
        }

        expandIconView.setState(ExpandIconView.MORE, true)


        expandIconView.setOnClickListener {
//            if (expanded) {
//                collapse(400)
//            } else {
//                expand(400)
//            }

            when(state) {
                STATE_EXPANDED -> {
                    collapse(400)
                }
                STATE_COLLAPSED -> {
                    expand(400)
                }
                else -> {
                    expandIconView.isClickable = false
                    expandIconView.isEnabled = false
                    Log.i("CalendarCollapse", "Wrong state of performClick() $state")
                }
            }
        }

        this.post { collapseTo(mCurrentWeekIndex) }


    }

    fun navigateToCurrentWeek(year:Int,month:Int,date:Int){
        collapse(400)
        val cal = Calendar.getInstance()
        cal.set(year,month,date)
        val adapter = CalendarAdapter(context, cal)
        setAdapter(adapter)
        reload()
    }
    fun navigateToCurrentMonth(year:Int,month:Int,date:Int){
        val cal = Calendar.getInstance()
        cal.set(year,month,date)
        val adapter = CalendarAdapter(context, cal)
        setAdapter(adapter)
        expand(400)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mInitHeight = mTableBody.measuredHeight

        if (mIsWaitingForUpdate) {
            redraw()
            mHandler.post { collapseTo(mCurrentWeekIndex) }
            mIsWaitingForUpdate = false
            if (mListener != null) {
                mListener!!.onDataUpdate()
            }
        }
    }
    fun redraww(){
        redraw()
    }

    override fun redraw() {
        // redraw all views of week
        val rowWeek = mTableHead.getChildAt(0) as TableRow?
        if (rowWeek != null) {
            for (i in 0 until rowWeek.childCount) {
                (rowWeek.getChildAt(i) as TextView).setTextColor(textColor)
            }
        }
        // redraw all views of day
        if (mAdapter != null) {
            for (i in 0 until mAdapter!!.count) {
                val day = mAdapter!!.getItem(i)
                val view = mAdapter!!.getView(i)
                val txtDay = view.findViewById<View>(R.id.txt_day) as TextView
                txtDay.setBackgroundColor(Color.TRANSPARENT)
                txtDay.setTextColor(textColor)

                // set today's item
                if (isToday(day)) {
                    txtDay.setBackgroundDrawable(todayItemBackgroundDrawable)
                    txtDay.setTextColor(todayItemTextColor)
                }

                // set the selected item
                if (isSelectedDay(day)) {
                    txtDay.setBackgroundDrawable(selectedItemBackgroundDrawable)
                    txtDay.setTextColor(selectedItemTextColor)
                }
            }
        }
    }

    fun reloadCalender() {
        reload()
    }

    override fun reload() {
        mAdapter?.let { mAdapter ->
            mAdapter.refresh()
            val calendar = Calendar.getInstance()
            val tempDatePattern: String
            if (calendar.get(Calendar.YEAR) != mAdapter.calendar.get(Calendar.YEAR)) {
                tempDatePattern = "MMMM YYYY"
            } else {
                tempDatePattern = datePattern
            }
            // reset UI
            val dateFormat = SimpleDateFormat("MMMM yyyy", getCurrentLocale(context))
            dateFormat.timeZone = mAdapter.calendar.timeZone
            mTxtTitle.text = dateFormat.format(mAdapter.calendar.time)
            mTableHead.removeAllViews()
            mTableBody.removeAllViews()

            var rowCurrent: TableRow
            rowCurrent = TableRow(context)
            rowCurrent.layoutParams = TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            for (i in 0..6) {
                val view = mInflater.inflate(R.layout.layout_day_of_week, null)
                val txtDayOfWeek = view.findViewById<View>(R.id.txt_day_of_week) as TextView
                txtDayOfWeek.setText(DateFormatSymbols().getShortWeekdays()[(i + firstDayOfWeek) % 7 + 1])
                view.layoutParams = TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                rowCurrent.addView(view)
            }
            mTableHead.addView(rowCurrent)

            // set day view
            for (i in 0 until mAdapter.count) {

                if (i % 7 == 0) {
                    rowCurrent = TableRow(context)
                    rowCurrent.layoutParams = TableLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    mTableBody.addView(rowCurrent)
                }
                val view = mAdapter.getView(i)
                view.layoutParams = TableRow.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                params.let { params ->
                    if (params != null && (mAdapter.getItem(i).diff < params.prevDays || mAdapter.getItem(
                            i
                        ).diff > params.nextDaysBlocked)
                    ) {
                        view.isClickable = false
                        view.alpha = 0.3f
                    } else {
                        view.setOnClickListener { v -> onItemClicked(v, mAdapter.getItem(i)) }
                        view.setOnLongClickListener { p0 ->
                            p0?.let { onLongItemClicked(it, mAdapter.getItem(i)) }
                            true
                        }
                    }
                }
                rowCurrent.addView(view)
            }

            redraw()
            mIsWaitingForUpdate = true
        }
    }

    var isDaySelected = true

    fun onItemClicked(view: View, day: Day) {

        isDaySelected = !(isDaySelected && isSelectedDay(day))

        if (isDaySelected) select(day) else unSelectDay(day)
        Log.d("31dateissue","-----from onItemClicked()------------")

        val cal = mAdapter!!.calendar
        if (isLongPress) {
//            this.selectedItemRange.
        }

        val newYear = day.year
        val newMonth = day.month
        val oldYear = cal.get(Calendar.YEAR)
        val oldMonth = cal.get(Calendar.MONTH)
        if (newMonth != oldMonth) {
            cal.set(day.year, day.month, 1)

            if (newYear > oldYear || newMonth > oldMonth) {
                mCurrentWeekIndex = 0
            }
            if (newYear < oldYear || newMonth < oldMonth) {
                mCurrentWeekIndex = -1
            }
            if (mListener != null) {
                mListener!!.onMonthChange()
            }
            reload()
        }

        if (mListener != null) {
            mListener!!.onItemClick(view)
        }
    }


    fun onLongItemClicked(view: View, day: Day) {
        select(day)
        this.isLongPress = true
        Log.e("hfmjdhfjkhsdjhfj", "onItemClicked: ")
        val cal = mAdapter!!.calendar

        val newYear = day.year
        val newMonth = day.month
        val oldYear = cal.get(Calendar.YEAR)
        val oldMonth = cal.get(Calendar.MONTH)
        if (newMonth != oldMonth) {
            cal.set(day.year, day.month, 1)

            if (newYear > oldYear || newMonth > oldMonth) {
                mCurrentWeekIndex = 0
            }
            if (newYear < oldYear || newMonth < oldMonth) {
                mCurrentWeekIndex = -1
            }
            if (mListener != null) {
                mListener!!.onMonthChange()
            }
            reload()
        }

        if (mListener != null) {
            mListener!!.onItemClick(view)
        }
    }

    // public methods
    fun setAdapter(adapter: CalendarAdapter) {
        mAdapter = adapter
        adapter.setFirstDayOfWeek(firstDayOfWeek)

        reload()

        // init week
        mCurrentWeekIndex = suitableRowIndex
    }

    fun addEventTagList(dateList: List<Date>) {
        val eventList = dateList.map { it->
            it.year = it.year.plus(1900)
            Event(it.year, it.month, it.date, eventColor)
        }
    mAdapter!!.addEventList( ArrayList(eventList))
    reload()
}
    fun addHEventTagList(dateList: List<Date>) {
        val eventList = dateList.map { it->
            it.year = it.year.plus(1900)
            Event(it.year, it.month, it.date, eventColor)
        }
        mAdapter!!.addHEventList( ArrayList(eventList))
        reload()
    }
fun addEventTag(numYear: Int, numMonth: Int, numDay: Int) {
    mAdapter!!.addEvent(Event(numYear, numMonth, numDay, eventColor))
    reload()
}

fun removeEvents() {
    mAdapter!!.removeEvent()
    reload()
}

fun addEventTag(numYear: Int, numMonth: Int, numDay: Int, color: Int) {
    mAdapter!!.addEvent(Event(numYear, numMonth, numDay, color))
    reload()
}

fun prevMonth() {
    val cal = mAdapter!!.calendar
    params.let {
        if (it != null && (Calendar.getInstance().get(Calendar.YEAR) * 12 + Calendar.getInstance()
                .get(Calendar.MONTH) + it.prevDays / 30) > (cal.get(Calendar.YEAR) * 12 + cal.get(
                Calendar.MONTH
            ))
        ) {
            val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
            val interpolator = BounceAnimator(0.1, 10.0)
            myAnim.setInterpolator(interpolator)
            mTableBody.startAnimation(myAnim)
            mTableHead.startAnimation(myAnim)
            return
        }
        if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
            cal.set(cal.get(Calendar.YEAR) - 1, cal.getActualMaximum(Calendar.MONTH), 1)
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1)
        }
        reload()
        if (mListener != null) {

            if (isDaySelected && selectedDay != null) {
                val day = Day(
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    if (!expanded) {
                        getEndDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                    } else {
                        val lastDate=getEndDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                        if(selectedDay!!.day > lastDate) lastDate else selectedDay!!.day
                    }
                )
                select(day)
                Log.d("31dateissue","-----from prevMonth()------------")
            }
            mListener!!.onMonthChange()
            /*if (expanded)mListener!!.onMonthChange()
            else mListener!!.onWeekChange(mCurrentWeekIndex)*/
        }
    }

}
    fun getStartAndEndDateOfCurrentMonth(month: Int, year: Int): Pair<Date, Date> {
        val monthsEndsWith31 = listOf(0, 2, 4, 6, 7, 9, 11)
        val monthsEndsWith30 = listOf(3, 5, 8, 10)
        val sdCalender = Calendar.getInstance()
        sdCalender.set(year, month, 1)
        val startDate = sdCalender.time

        val ed = if (monthsEndsWith31.contains(month)) {
            31
        } else if (monthsEndsWith30.contains(month)) {
            30
        } else {
            /** feb month here check for leap year **/
            if (year.isLeapYear()) 29 else 28
        }

        val edCalender = Calendar.getInstance()
        edCalender.set(year, month, ed)
        val endDate = edCalender.time

        return Pair(startDate, endDate)
    }

fun nextMonth() {
    val cal = mAdapter!!.calendar
    params.let {
        if (it != null && (Calendar.getInstance().get(Calendar.YEAR) * 12 + Calendar.getInstance()
                .get(Calendar.MONTH) + it.nextDaysBlocked / 30) < (cal.get(Calendar.YEAR) * 12 + cal.get(
                Calendar.MONTH
            ))
        ) {
            val myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce)
            val interpolator = BounceAnimator(0.1, 10.0)
            myAnim.setInterpolator(interpolator)
            mTableBody.startAnimation(myAnim)
            mTableHead.startAnimation(myAnim)
            return
        }
        if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
            cal.set(cal.get(Calendar.YEAR) + 1, cal.getActualMinimum(Calendar.MONTH), 1)
        } else {
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1)
        }
        reload()
        if (mListener != null) {

            if (isDaySelected && selectedDay != null) {
                val day = Day(
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                    if (!expanded) {
                        1
                    } else {
                        val lastDate=getEndDayOfMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                        if(selectedDay!!.day > lastDate) lastDate else selectedDay!!.day
                    }
                )
                select(day)
                Log.d("31dateissue","-----from nextMonth()------------")
            }


            mListener!!.onMonthChange()
            /*if (expanded) {
                mListener!!.onMonthChange()
            }else{
                mListener!!.onWeekChange(mCurrentWeekIndex)
            }*/
        }
    }
}

fun nextDay() {
    if (selectedItemPosition == mAdapter!!.count - 1) {
        nextMonth()
        mAdapter!!.getView(0).performClick()
        reload()
        mCurrentWeekIndex = 0
        collapseTo(mCurrentWeekIndex)
    } else {
        mAdapter!!.getView(selectedItemPosition + 1).performClick()
        if (((selectedItemPosition + 1 - mAdapter!!.calendar.firstDayOfWeek) / 7) > mCurrentWeekIndex) {
            nextWeek()
        }
    }
    mListener?.onDayChanged()
}

fun prevDay() {
    if (selectedItemPosition == 0) {
        prevMonth()
        mAdapter!!.getView(mAdapter!!.count - 1).performClick()
        reload()
        return;
    } else {
        mAdapter!!.getView(selectedItemPosition - 1).performClick()
        if (((selectedItemPosition - 1 + mAdapter!!.calendar.firstDayOfWeek) / 7) < mCurrentWeekIndex) {
            prevWeek()
        }
    }
    mListener?.onDayChanged()
}

fun prevWeek() {
    if (mCurrentWeekIndex - 1 < 0) {
        mCurrentWeekIndex = -1
        prevMonth()
    } else {
        mCurrentWeekIndex--
        collapseTo(mCurrentWeekIndex)
        selectWeekDay(false)
    }

}

fun nextWeek() {
    if (mCurrentWeekIndex + 1 >= mTableBody.childCount) {
        mCurrentWeekIndex = 0
        nextMonth()
    } else {
        mCurrentWeekIndex++
        collapseTo(mCurrentWeekIndex)
        selectWeekDay(true)
    }
}

    private fun selectWeekDay(next: Boolean) {

        if (!isDaySelected || selectedDay?.day==0) return

        val newDay = getNextOrPrevWeekDay(next)

        if (newDay?.day!=0) select(newDay!!)
    }

    private fun getNextOrPrevWeekDay(next: Boolean): Day? {
        if (selectedDay?.day == 0) return selectedDay

        val calendar = Calendar.getInstance()
        calendar.set(selectedDay!!.year, selectedDay!!.month, selectedDay!!.day)

        val pair = getStartAndEndDateOfWeek(
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR), mCurrentWeekIndex
        )

        val startDate = pair.first
        val endDate = pair.second

        if (next) {
            if(mCurrentWeekIndex  >= mTableBody.childCount){
                calendar.time = endDate
            }/*else if(mCurrentWeekIndex-1  <= 0){
                calendar.time = startDate
            }*/else {
                calendar.add(Calendar.DATE, 7)
            }
        }else{
            if(mCurrentWeekIndex  <= 0){
                calendar.time = startDate
            }/*else if(mCurrentWeekIndex+1  >= mTableBody.childCount){
                calendar.time = endDate
            }*/else {
                calendar.add(Calendar.DATE, -7)
            }

        }


        return Day(
            calendar[Calendar.YEAR], calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )
    }

    private fun isLastDayOfMonth(year:Int, month: Int, day: Int) : Boolean {
        return getEndDayOfMonth(year, month)==day
    }

    fun isSelectedDay(day: Day?): Boolean {
    return (day != null
            && selectedItem != null
            && day.year == selectedItem!!.year
            && day.month == selectedItem!!.month
            && day.day == selectedItem!!.day)
}

fun isToday(day: Day?): Boolean {
    val todayCal = Calendar.getInstance()
    return (day != null
            && day.year == todayCal.get(Calendar.YEAR)
            && day.month == todayCal.get(Calendar.MONTH)
            && day.day == todayCal.get(Calendar.DAY_OF_MONTH))
}

/**
 * collapse in milliseconds
 */
open fun collapse(duration: Int) {

    if (state == STATE_EXPANDED) {

        state = STATE_PROCESSING

        mLayoutBtnGroupMonth.visibility = View.GONE
        mLayoutBtnGroupWeek.visibility = View.VISIBLE
        mBtnPrevWeek.isClickable = false
        mBtnNextWeek.isClickable = false

        val index = suitableRowIndex
        mCurrentWeekIndex = index

        minHeight = if (mTableBody.getChildAt(index).measuredHeight != 0) mTableBody.getChildAt(index).measuredHeight else minHeight
        val currentHeight = mInitHeight
        val targetHeight = minHeight
        var tempHeight = 0
        for (i in 0 until index) {
            tempHeight += minHeight
        }
        val topHeight = tempHeight

        val anim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                mScrollViewBody.layoutParams.height = if (interpolatedTime == 1f)
                    targetHeight
                else
                    currentHeight - ((currentHeight - targetHeight) * interpolatedTime).toInt()
                mScrollViewBody.requestLayout()

                if (mScrollViewBody.measuredHeight < topHeight + targetHeight) {
                    val position = topHeight + targetHeight - mScrollViewBody.measuredHeight
                    mScrollViewBody.smoothScrollTo(0, position)
                }

                if (interpolatedTime == 1f) {
                    state = STATE_COLLAPSED

                    if (mListener != null) mListener!!.onCollapse(mCurrentWeekIndex)

                    mBtnPrevWeek.isClickable = true
                    mBtnNextWeek.isClickable = true
                }
            }
        }
        anim.duration = duration.toLong()
        startAnimation(anim)
    }

    expandIconView.setState(ExpandIconView.MORE, true)
    reload()
}

 fun collapseTo(index: Int) {
    var index = index
    if (state == STATE_COLLAPSED) {
        if (index == -1) {
            index = mTableBody.childCount - 1
        }
        mCurrentWeekIndex = index

        minHeight = if (mTableBody.getChildAt(index).measuredHeight != 0) mTableBody.getChildAt(index).measuredHeight else minHeight
        val targetHeight = minHeight
        var tempHeight = 0
        for (i in 0 until index) {
            tempHeight += minHeight
        }
        val topHeight = tempHeight

        mScrollViewBody.layoutParams.height = targetHeight
        mScrollViewBody.requestLayout()

        mHandler.post { mScrollViewBody.smoothScrollTo(0, topHeight) }


        if (mListener != null) {
            mListener!!.onWeekChange(mCurrentWeekIndex)
        }
    }
}

fun expand(duration: Int) {

    if (state == STATE_COLLAPSED) {

        state = STATE_PROCESSING

        mLayoutBtnGroupMonth.visibility = View.VISIBLE
        mLayoutBtnGroupWeek.visibility = View.GONE
        mBtnPrevMonth.isClickable = false
        mBtnNextMonth.isClickable = false

        val currentHeight = mScrollViewBody.measuredHeight
        val targetHeight = mInitHeight

        val anim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {

                mScrollViewBody.layoutParams.height = if (interpolatedTime == 1f)
                    LinearLayout.LayoutParams.WRAP_CONTENT
                else
                    currentHeight - ((currentHeight - targetHeight) * interpolatedTime).toInt()
                mScrollViewBody.requestLayout()

                if (interpolatedTime == 1f) {
                    state = STATE_EXPANDED

                    if (mListener != null) mListener!!.onExpand()

                    mBtnPrevMonth.isClickable = true
                    mBtnNextMonth.isClickable = true
                }
            }
        }
        anim.duration = duration.toLong()
        startAnimation(anim)
    }

    expandIconView.setState(ExpandIconView.LESS, true)
    reload()
}

fun select(day: Day) {
    selectedItem = Day(day.year, day.month, day.day)
    Log.d("31dateissue","----${selectedItem?.day}/${selectedItem?.month}/${selectedItem?.year}")

    redraw()

    if (mListener != null) {
        mListener!!.onDaySelect()
    }
}

fun unSelectDay(day: Day) {
    selectedItem = Day(day.year, day.month)

    redraw()

    if (mListener != null) {
        mListener!!.onDayUnSelected()
    }
}

fun setStateWithUpdateUI(state: Int) {
    this@CollapsibleCalendar.state = state

    if (state != state) {
        mIsWaitingForUpdate = true
        requestLayout()
    }
}

// callback
fun setCalendarListener(listener: CalendarListener) {
    mListener = listener
}

interface CalendarListener {

    // triggered when a day is selected programmatically or clicked by user.
    fun onDaySelect()


    // triggered when a day is unselected programmatically or clicked by user.
    fun onDayUnSelected()

    // triggered only when the views of day on calendar are clicked by user.
    fun onItemClick(v: View)

    // triggered when the data of calendar are updated by changing month or adding events.
    fun onDataUpdate()

    // triggered when the month are changed.
    fun onMonthChange()

    // triggered when the week position are changed.
    fun onWeekChange(position: Int)

    fun onClickListener()

    fun onDayChanged()

    fun onMoreIconClicked(v: View)

    //triggered when calendar view is collapse to week view
    fun onCollapse(mCurrentWeekIndex: Int)

    //triggered when calendar view is expanded to month view
    fun onExpand()
}

fun setExpandIconVisible(visible: Boolean) {
    if (visible) {
        expandIconView.visibility = View.VISIBLE
    } else {
        expandIconView.visibility = View.GONE
    }
}

data class Params(val prevDays: Int, val nextDaysBlocked: Int)

var params: Params? = null
    set(value) {
        field = value
    }
}

fun getEndDayOfMonth(year: Int, month: Int): Int {
    val monthsEndsWith31 = listOf(0, 2, 4, 6, 7, 9, 11)
    val monthsEndsWith30 = listOf(3, 5, 8, 10)

    val ed = if (monthsEndsWith31.contains(month)) {
        31
    } else if (monthsEndsWith30.contains(month)) {
        30
    } else {
        /** feb month here check for leap year **/
        if (year.isLeapYear()) 29 else 28
    }

    val edCalender = Calendar.getInstance()
    edCalender.set(year, month, ed)

    return edCalender.time.date
}

fun Int.isLeapYear(): Boolean {
    return this % 4 == 0 && (this % 100 != 0 || this % 400 == 0)
}

fun getStartAndEndDateOfWeek(month: Int, year: Int, weekIndex: Int): Pair<Date, Date> {
    val dates = getEnabledDatesForWeekView(month + 1, year, weekIndex)
    return Pair(dates.first(), dates.last())
}

fun getEnabledDatesForWeekView(
    currentMonth: Int,
    currentYear: Int,
    weekIndex: Int
): List<Date> {
    return getWeekDates(
        currentMonth=currentMonth,
        currentYear=currentYear,
        weekIndex=weekIndex
    ).filter { (it.month+1) == currentMonth }
}

fun getWeekDates(
    currentMonth: Int, currentYear: Int, weekIndex: Int
): List<Date> {
    val getDatesVisible =getDatesForCalenderView(year = currentYear, month = currentMonth)
    val weekWiseDateList = getDatesVisible.chunked(7)
    return weekWiseDateList[weekIndex]
}

fun getDatesForCalenderView(year:Int,month: Int): MutableList<Date>{
    val list= mutableListOf<Date>()
    val previousMonthDateList=getDatesForCurrentMonth(year,month-1)
    val currentMonthDateList=getDatesForCurrentMonth(year,month)
    val nextMonthDateList=getDatesForCurrentMonth(year,month+1)

    val t=previousMonthDateList.toMutableList().getPreviousDates(currentMonthDateList.first())
    t.reverse()
    list.addAll(t)
    list.addAll(getDatesForCurrentMonth(year,month))
    list.addAll(nextMonthDateList.toMutableList().getNextDates(currentMonthDateList.last()))
    return list
}


fun getDatesForCurrentMonth(year: Int, month: Int): List<Date> {
    val calendar = Calendar.getInstance()
    calendar.clear()
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1) // Month is zero-based, so subtract 1
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfMonth = calendar.time

    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.DAY_OF_MONTH, -1)
    val lastDayOfMonth = calendar.time


//        val currentDay = Date(year, month-1, 1)
//        val lastDayOfMonth = Date(year, month, 0)
    val datesList = mutableListOf<Date>()

    val currentDay = firstDayOfMonth.clone() as Date
    while (!currentDay.after(lastDayOfMonth)) {
        datesList.add(currentDay.clone() as Date)
        currentDay.date++
    }

    return datesList
}

private fun MutableList<Date>.getPreviousDates(startDate: Date): MutableList<Date> {

    val list=this
    list.reverse()

    return when(startDate.getDayFromDate()){
        "Sun"-> mutableListOf()
        "Mon"-> mutableListOf(list[0])
        "Tue"->mutableListOf(list[0], list[1])
        "Wed"->mutableListOf(list[0], list[1],list[2])
        "Thu"->mutableListOf(list[0], list[1],list[2],list[3])
        "Fri"->mutableListOf(list[0], list[1],list[2],list[3],list[4])
        "Sat"->mutableListOf(list[0], list[1],list[2],list[3],list[4],list[5])
        else -> mutableListOf()
    }
}

private fun MutableList<Date>.getNextDates(endDate: Date): MutableList<Date> {

    val list=this
    return when(endDate.getDayFromDate()){
        "Sun"->mutableListOf(list[0], list[1],list[2],list[3],list[4],list[5])
        "Mon"->mutableListOf(list[0], list[1],list[2],list[3],list[4])
        "Tue"->mutableListOf(list[0], list[1],list[2],list[3])
        "Wed"->mutableListOf(list[0], list[1],list[2])
        "Thu"->mutableListOf(list[0], list[1])
        "Fri"->mutableListOf(list[0])
        "Sat"->mutableListOf()
        else -> mutableListOf()
    }
}

fun Date.getDayFromDate()= DateFormat.format("EE",this).toString()