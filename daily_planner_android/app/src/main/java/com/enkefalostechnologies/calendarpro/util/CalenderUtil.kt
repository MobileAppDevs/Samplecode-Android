package com.enkefalostechnologies.calendarpro.util

import android.util.Log
import com.enkefalostechnologies.calendarpro.util.AppUtil.getDayFromDate
import com.enkefalostechnologies.calendarpro.util.AppUtil.setTimeToZero
import java.util.Calendar
import java.util.Date

object CalenderUtil {



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

    fun getDatesForCalenderView(year:Int,month: Int): MutableList<Date>{
        val list= mutableListOf<Date>()
        val previousMonthDateList=CalenderUtil.getDatesForCurrentMonth(year,month-1)
        val currentMonthDateList=CalenderUtil.getDatesForCurrentMonth(year,month)
        val nextMonthDateList=CalenderUtil.getDatesForCurrentMonth(year,month+1)

        val t=previousMonthDateList.toMutableList().getPreviousDates(currentMonthDateList.first())
        t.reverse()
        list.addAll(t)
        list.addAll(CalenderUtil.getDatesForCurrentMonth(year,month))
        list.addAll(nextMonthDateList.toMutableList().getNextDates(currentMonthDateList.last()))
        return list
    }

    fun Date.isCurrentDate(): Boolean {
        val currentDate = Calendar.getInstance()
        val givenDate = Calendar.getInstance()
        givenDate.time = this
        return currentDate.get(Calendar.YEAR) == givenDate.get(Calendar.YEAR) &&
                currentDate.get(Calendar.MONTH) == givenDate.get(Calendar.MONTH) &&  currentDate.get(Calendar.DAY_OF_MONTH) == givenDate.get(Calendar.DAY_OF_MONTH)
    }
    fun Date.isSelectedDate(date:Date):Boolean{
        val currentDate = Calendar.getInstance()
            currentDate.time=date
        val givenDate = Calendar.getInstance()
        givenDate.time = this
        return currentDate.get(Calendar.YEAR) == givenDate.get(Calendar.YEAR) &&
                currentDate.get(Calendar.MONTH) == givenDate.get(Calendar.MONTH) &&  currentDate.get(Calendar.DAY_OF_MONTH) == givenDate.get(Calendar.DAY_OF_MONTH)
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

    /*** for week view on calender ***/
    fun getWeekDates(
        currentMonth: Int, currentYear: Int, weekIndex: Int
    ): List<Date> {
        val getDatesVisible =getDatesForCalenderView(year = currentYear, month = currentMonth)
        val weekWiseDateList = getDatesVisible.chunked(7)
        return weekWiseDateList[weekIndex]
    }
    fun getWeekIndex(date: Date?):Int{
        var index=0
        if(date==null){
            return index
        }
        val currentYear=date.year+1900
        val currentMonth=date.month+1
        val getDatesVisible =getDatesForCalenderView(currentYear, currentMonth)
        val weekWiseDateList = getDatesVisible.chunked(7)
        weekWiseDateList.mapIndexed{i,list->
            if(list.any { it.date==date.date && it.month==date.month && it.year==date.year }){
                index=i
            }
        }
        return index

    }
    fun getWeeksInMonth(currentMonth: Int, currentYear: Int):Int{
        val getDatesVisible =getDatesForCalenderView(currentYear, currentMonth)
        return getDatesVisible.chunked(7).size
    }

    fun Date.nextSelectedDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.MONTH, 1)
        return calendar.time
    }
    fun Date.previousSelectedDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.MONTH, -1)
        return calendar.time
    }

   fun  Date.getNextWeekSelectedDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.time = this
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        return calendar.time
    }


}