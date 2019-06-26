package com.joe.friendscontacts.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.telephony.PhoneNumberUtils;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.joe.friendscontacts.R;
import com.joe.friendscontacts.modals.ContactAddress;
import com.joe.friendscontacts.modals.ContactEmail;
import com.joe.friendscontacts.modals.ContactPhoneNumber;
import com.joe.friendscontacts.modals.ContactsViewModal;
import com.joe.friendscontacts.modals.Request.PhoneBookRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By Madhur on 16/11/18 , 3:16 PM
 */
public class ContactsFetcher {
    private final Context context;

    public ContactsFetcher(Context c) {
        this.context = c;
    }

    public ArrayList<ContactsViewModal> fetchAll() {
        String[] projectionFields = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            projectionFields = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
            };
        }
        ArrayList<ContactsViewModal> listContacts = new ArrayList<>();
        CursorLoader cursorLoader = new CursorLoader(context,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields, // the columns to retrieve
                null, // the selection criteria (none)
                null, // the selection args (none)
                null // the sort order (default)
        );

        Cursor c = cursorLoader.loadInBackground();

        Map<String, ContactsViewModal> contactsMap = null;
        if (c != null) {
            contactsMap = new HashMap<>(c.getCount());
        }

        if (c != null && c.moveToFirst()) {

            int idIndex = c.getColumnIndex(ContactsContract.Contacts._ID);
            int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

            do {
                String contactId = c.getString(idIndex);
                String contactDisplayName = c.getString(nameIndex);
                ContactsViewModal contact = new ContactsViewModal();
                contact.setName(contactDisplayName);
                contact.setId(contactId);
                contactsMap.put(contactId, contact);
                listContacts.add(contact);
            } while (c.moveToNext());
        }

        c.close();

        /*new LoadDetails(contactsMap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
        matchContactNumbers(contactsMap);
        //matchContactEmails(contactsMap);
        //matchContactAddresses(contactsMap);
        return listContacts;
    }

    private void matchContactEmails(Map<String,ContactsViewModal> contactsMap) {

        ArrayList<ContactEmail> contactEmailList = new ArrayList<>();

        // Get email
        final String[] emailProjection = new String[]{
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID,
        };

        Cursor email = new CursorLoader(context,
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                emailProjection,
                null,
                null,
                null).loadInBackground();

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
            final int contactTypeColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);
            final int contactIdColumnsIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);

            ContactsViewModal contact;
            while (!email.isAfterLast()) {
                contact = new ContactsViewModal();
                contactEmailList = new ArrayList<>();
                final String address = email.getString(contactEmailColumnIndex);
                final String contactId = email.getString(contactIdColumnsIndex);
                final int type = email.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                CharSequence emailType = ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(), type, customLabel);
                ContactEmail contactEmail = new ContactEmail(address,emailType.toString());
                contactEmailList.add(contactEmail);


                if (contact.getEmails().size()>0){
                    contactEmailList.addAll(contact.getEmails());
                }
                contact.setEmails(contactEmailList);

                email.moveToNext();
            }

        }
        email.close();
    }

    private void matchContactNumbers(Map<String,ContactsViewModal> contactsMap) {

        ArrayList<ContactPhoneNumber> contactPhoneNUmberList = new ArrayList<ContactPhoneNumber>();

        // Get numbers
        final String[] numberProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        };

        ContentResolver cr = context.getContentResolver();

        Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, numberProjection, null, null, null);

        /*Cursor phone = new CursorLoader(context,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                numberProjection,
                null,
                null,
                null).loadInBackground();*/

        if (phone != null && phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            final int contactIdColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

            ContactsViewModal contact;

            while (!phone.isAfterLast()) {
                contact = new ContactsViewModal();
                contactPhoneNUmberList = new ArrayList<>();
                final String number = phone.getString(contactNumberColumnIndex);
                final String contactId = phone.getString(contactIdColumnIndex);
                contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                final int type = phone.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, customLabel);
                ContactPhoneNumber phoneNumber = new ContactPhoneNumber();
                phoneNumber.setType(phoneType.toString());
                phoneNumber.setNumber(number);

                if (contact.getNumbers().size()>0){
                    contactPhoneNUmberList.addAll(contact.getNumbers());
                }
                contactPhoneNUmberList.add(phoneNumber);
                contact.setNumbers(contactPhoneNUmberList);
                phone.moveToNext();
            }
        }

        phone.close();
    }

    private void matchContactCompany(Map<String,ContactsViewModal> contactsMap) {

        ArrayList<ContactPhoneNumber> contactPhoneNUmberList = new ArrayList<ContactPhoneNumber>();

        // Get numbers
        final String[] numberProjection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
        };

        ContentResolver cr = context.getContentResolver();

        Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, numberProjection, null, null, null);

        /*Cursor phone = new CursorLoader(context,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                numberProjection,
                null,
                null,
                null).loadInBackground();*/

        if (phone != null && phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            final int contactTypeColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            final int contactIdColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);

            ContactsViewModal contact;

            while (!phone.isAfterLast()) {
                contact = new ContactsViewModal();
                contactPhoneNUmberList = new ArrayList<>();
                final String number = phone.getString(contactNumberColumnIndex);
                final String contactId = phone.getString(contactIdColumnIndex);
                contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                final int type = phone.getInt(contactTypeColumnIndex);
                String customLabel = "Custom";
                CharSequence phoneType = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, customLabel);
                ContactPhoneNumber phoneNumber = new ContactPhoneNumber();
                phoneNumber.setType(phoneType.toString());
                phoneNumber.setNumber(number);

                if (contact.getNumbers().size()>0){
                    contactPhoneNUmberList.addAll(contact.getNumbers());
                }
                contactPhoneNUmberList.add(phoneNumber);
                contact.setNumbers(contactPhoneNUmberList);
                phone.moveToNext();
            }
        }

        phone.close();
    }

    private void matchContactAddresses(Map<String,ContactsViewModal> contactsMap) {
        ArrayList<ContactAddress> contactAddressArrayList = new ArrayList<>();

        // Get email
        final String[] addressProjection = new String[]{
                ContactsContract.CommonDataKinds.StructuredPostal.POBOX,
                ContactsContract.CommonDataKinds.StructuredPostal.STREET,
                ContactsContract.CommonDataKinds.StructuredPostal.CITY,
                ContactsContract.CommonDataKinds.StructuredPostal.REGION,
                ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE,
                ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID
        };

        Cursor address = new CursorLoader(context,
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                addressProjection,
                null,
                null,
                null).loadInBackground();

        if (address.moveToNext()) {
            final int contactAdressPoboxColumnIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX);
            final int contactStreetColumnIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET);
            final int contactCityColumnsIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY);
            final int contactAdresssRegionColumnIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION);
            final int contactPostCodeColumnIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE);
            final int contactCountryColumnsIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY);
            final int contactTypeColumnIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
            final int contactContactIdColumnsIndex = address.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID);

            ContactsViewModal contact = new ContactsViewModal();

            while (!address.isAfterLast()) {
                String poBox = address.getString(contactAdressPoboxColumnIndex);
                String street = address.getString(contactStreetColumnIndex);
                String city = address.getString(contactCityColumnsIndex);
                String region = address.getString(contactAdresssRegionColumnIndex);
                String PostCode = address.getString(contactPostCodeColumnIndex);
                String country = address.getString(contactCountryColumnsIndex);
                int type = address.getInt(contactTypeColumnIndex);
                String contactId = address.getString(contactContactIdColumnsIndex);
                String customLabel = "Custom";
                contact = contactsMap.get(contactId);
                if (contact == null) {
                    continue;
                }
                CharSequence addressType = ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(), type, customLabel);
                ContactAddress contactAddress = new ContactAddress(poBox,street,city,region,PostCode,country,addressType.toString());
                contactAddressArrayList.add(contactAddress);
            }

            if (contact != null){
                contact.setAddresses(contactAddressArrayList);
            }
        }
    }

    public static ArrayList<PhoneBookRequest.PhoneBook> fetchContactsToUpload(Context ctx){
        ArrayList<PhoneBookRequest.PhoneBook> phoneBookArrayList = new ArrayList<>();
        String[] projectionFields = new String[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            projectionFields = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            };
        }

        /*CursorLoader cursorLoader = new CursorLoader(context,
                ContactsContract.Contacts.CONTENT_URI,
                projectionFields, // the columns to retrieve
                null, // the selection criteria (none)
                null, // the selection args (none)
                null // the sort order (default)
        );

        Cursor c = cursorLoader.loadInBackground();*/

        Cursor c = ctx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projectionFields, // the columns to retrieve
                null, // the selection criteria (none)
                null, // the selection args (none)
                null // the sort order (default)
                 );

        Map<String, PhoneBookRequest.PhoneBook> contactsMap = null;
        if (c != null) {
            contactsMap = new HashMap<>(c.getCount());
        }

        if (c != null && c.moveToFirst()) {

            int idIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int nameIndex = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            int phoneIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            do {
                String contactDisplayName = c.getString(idIndex);
                String contactPhoneNumber = c.getString(phoneIndex);
                String contactId = c.getString(idIndex);
                PhoneBookRequest.PhoneBook contact = new PhoneBookRequest.PhoneBook();
                contact.setName(contactDisplayName);
                contact.setPhoneNumber(contactPhoneNumber);
                String phone;
                try {
                     Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse(contact.getPhoneNumber(),SharedPrefsHelper.getInstance().get(AppConstants.REGION_CODE,"CA"));
                     phone = String.valueOf(phoneNumber.getNationalNumber());
                     contactsMap.put(phone, contact);
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                phoneBookArrayList.add(contact);
            } while (c.moveToNext());
        }
        if (c != null) {
            c.close();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ArrayList<Object> deduped = (ArrayList<Object>) phoneBookArrayList.stream().distinct().collect(Collectors.toList());
        }else {
        }

        if (contactsMap != null) {
            phoneBookArrayList.clear();
            phoneBookArrayList.addAll(contactsMap.values());
        }
        return phoneBookArrayList;
    }
}
