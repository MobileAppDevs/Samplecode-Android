package com.enkefalostechnologies.calendarpro.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import com.enkefalostechnologies.calendarpro.model.LocalContact
import com.enkefalostechnologies.calendarpro.model.MobileContact

class ContactUtils(private val context: Context) {
    private var phoneBookContactList: List<LocalContact>? = null
    private var contactsMap: HashMap<String, LocalContact>? = null

    @SuppressLint("Range")
    fun fetchAll(): List<LocalContact>? {
        val projectionFields = arrayOf(
            ContactsContract.CommonDataKinds.Contactables.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.RawContacts.VERSION,
            ContactsContract.RawContacts.DIRTY,
            ContactsContract.CommonDataKinds.Contactables.CONTACT_ID
        )
        phoneBookContactList = ArrayList()

        val cr: ContentResolver = context.contentResolver
        val c = cr.query(
            ContactsContract.CommonDataKinds.Contactables.CONTENT_URI, projectionFields, null, null,
            null
        )

        if (c != null) {
            contactsMap = HashMap(c.count)
            if (c.moveToFirst()) {
                val idIndex =
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.CONTACT_ID)
                val nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                val version = c.getColumnIndex(ContactsContract.RawContacts.VERSION)
                c.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.CONTACT_ID)
                do {
                    val contactId = c.getString(idIndex)
                    val contactDisplayName = c.getString(nameIndex)
                    val contact = LocalContact()
                    contact.contactId = contactId
                    contact.sync = "0"
                    contact.name = contactDisplayName
                    contact.alMultiMobileContacts = ArrayList()
                    contact.version = c.getString(version).toInt()
                    //   Log.e("contactDisplayName",contactDisplayName+","+hasnumbercount+","+contactId+","+version+","+dirty+","+c.getString(type));
                    if (c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                            .toInt() > 0
                    ) {
                        contactsMap!![contactId] = contact
                    }
                } while (c.moveToNext())
            }
            c.close()
            matchContactNumbers(contactsMap!!)
        }
        return if (contactsMap != null) {
            ArrayList(contactsMap!!.values)
        } else null
    }

    private fun matchContactNumbers(contactsMap: MutableMap<String, LocalContact>) {
        // Get numbers
        val numberProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )


        val cr: ContentResolver = context.contentResolver


        val phone = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, numberProjection, null, null,
            null
        )
        if (phone!!.moveToFirst()) {
            val contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val contactTypeColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
            val contactIdColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val photoIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
            while (!phone.isAfterLast) {
                try {
                    val number = phone.getString(contactNumberColumnIndex)
                    val contactId = phone.getString(contactIdColumnIndex)
                    val contact = contactsMap[contactId]
                    if (contact != null) {
                        contact.imagePath = phone.getString(photoIndex)
                        val type = phone.getInt(contactTypeColumnIndex)
                        val customLabel = "Custom"
                        val phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.resources, type, customLabel)
                        val mobi = MobileContact()
                        mobi.chkStatus = false
                        mobi.mobileType = phoneType.toString()
                        mobi.mobileNumber = number.replace("\\s+".toRegex(), "")
                        //&&(!mobi.mobilenumber.contains(userVo.mobilenumber))
                        //removing the mobile number of moderator and the length of mobile number in between 7 and 16.
                        if (mobi.mobileNumber!!.length in 8..15) {
                            contact.alMultiMobileContacts?.add(mobi)
                            contact.mobileNumber = mobi.mobileNumber?.replace("-","")?.replace(" ","")
                            contact.alMultiMobileContacts!!.first().chkStatus = true
                        }
                        if (contact.alMultiMobileContacts!!.size == 0) {
                            contactsMap.remove(contact.contactId)
                        }
                    }
                    phone.moveToNext()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        phone.close()
    }
}