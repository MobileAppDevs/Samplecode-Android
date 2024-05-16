package com.enkefalostechnologies.calendarpro.ui

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.enkefalostechnologies.calendarpro.R
import com.enkefalostechnologies.calendarpro.databinding.ActivityPhoneBookBinding
import com.enkefalostechnologies.calendarpro.model.LocalContact
import com.enkefalostechnologies.calendarpro.ui.adapter.PhoneBookAdapter
import com.enkefalostechnologies.calendarpro.ui.base.BaseActivity
import com.enkefalostechnologies.calendarpro.util.ContactUtils
import com.enkefalostechnologies.calendarpro.util.PhoneBookAdapterItemListener

class PhoneBookActivity : BaseActivity<ActivityPhoneBookBinding>(R.layout.activity_phone_book) {

    private lateinit var adapter: PhoneBookAdapter
    private var list: MutableList<LocalContact>? = null

    override fun onViewBindingCreated() {
        adapter = PhoneBookAdapter(object: PhoneBookAdapterItemListener {
            override fun onClick(contact: LocalContact) {
                val intent = Intent()
                intent.putExtra("Contact", contact.mobileNumber)
                setResult(RESULT_OK, intent)
                finish()
            }
        })
        binding.rvContacts.adapter = adapter
        list = ContactUtils(this).fetchAll()?.sortedBy { it.name }?.distinctBy {
            it.mobileNumber
        }?.toMutableList()
        adapter.setList(list)
        binding.icBack.setOnClickListener(this)
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    val dataList = list?.filter {
                        it.name?.toLowerCase()?.contains(binding.etSearch.text.toString().toLowerCase()) == true || it.mobileNumber?.contains(binding.etSearch.text.toString().toLowerCase()) == true
                    }?.toMutableList()
                    adapter.setList(dataList)
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun addObserver() {}

    override fun removeObserver() {}

    override fun onClick(p0: View?) {
        when(p0){
            binding.icBack->{finish()}
        }
    }
}