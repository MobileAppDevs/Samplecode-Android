package com.enkefalostechnologies.calendarpro

enum class WeekDays( name:String, var index:Int){
    MON(name = "Mon",0),
    TUE(name = "Tue",1),
    WED(name = "Wed",2),
    THU(name = "THU",3),
    FRI(name = "Fri",4),
    SAT(name = "Sat",5),
    SUN(name = "Sun",6),
}

enum class SubscriptionType(name:String,var value:Int, var planId:String,var productId:String){
    MONTHLY("Monthly",0, planId = "1-month-standart-access", productId = "monthly_subscription_1"),
    YEARLY("Yearly",1, planId = "1-year-standart-access", productId = "annual_subscription_1")
}