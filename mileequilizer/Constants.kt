package com.ongraphtechnologies.mileequilizer

class Constants {
    interface ACTION {
        companion object {
            const val MAIN_ACTION = "com.ongraphtechnologies.mileequilizer.action.main"
            const val STARTFOREGROUND_ACTION =
                "com.ongraphtechnologies.foregroundservice.action.startforeground"
            const val STOPFOREGROUND_ACTION =
                "com.ongraphtechnologies.foregroundservice.action.stopforeground"
        }
    }

    interface NOTIFICATION_ID {
        companion object {
            const val FOREGROUND_SERVICE = 101
        }
    }
}