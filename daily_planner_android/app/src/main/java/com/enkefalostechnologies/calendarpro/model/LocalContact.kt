package com.enkefalostechnologies.calendarpro.model

import java.io.Serializable

class LocalContact {
    var version = 0
    var contactId: String = ""
    var name: String? = null
    var email: String? = null
    var imagePath: String? = null
    var sync: String? = null
    var mobileNumber: String? = null
    var alMultiMobileContacts: ArrayList<MobileContact>? = null
}

class MobileContact: Serializable {
    var mobileNumber: String? = null
    var mobileType: String? = null
    var chkStatus: Boolean = false
}
