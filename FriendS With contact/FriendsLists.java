package com.joe.friendscontacts.utils;

import android.content.Context;
import com.joe.friendscontacts.R;
import com.joe.friendscontacts.modals.ContactsViewModal;
import com.joe.friendscontacts.modals.HomeScreenCategory;
import com.joe.friendscontacts.modals.Response.UploadPhoneNumberResponse;
import com.joe.friendscontacts.modals.SubCategoryList;
import com.joe.friendscontacts.modals.SubCategoryMainList;

import java.util.*;

/**
 * Created By Madhur on 20/11/18 , 2:32 PM
 */
public class FriendsLists {
    public static List<String> nameList = new ArrayList<>();
    public static List<String> colorList = new ArrayList<>();
    public static ArrayList<HomeScreenCategory> homeScreenCategories = new ArrayList<>();
    public static ArrayList<SubCategoryMainList> subCategoryMainLists = new ArrayList<>();
    public static ArrayList<SubCategoryList> subCategoryLists = new ArrayList<>();
    public static ContactsViewModal contactsViewModal = new ContactsViewModal();
    public static boolean isNewContactAddInCategory = false;
    public static Map<String, Object> map = new HashMap<>();
    public static Map<String, Object> postQuestionMap = new HashMap<>();

    public static Map<String, Object> getPostQuestionMap() {
        return postQuestionMap;
    }

    public static void setPostQuestionMap(Map<String, Object> postQuestionMap) {
        FriendsLists.postQuestionMap = postQuestionMap;
    }

    public static String branchData = "";

    public static String getBranchData() {
        return branchData;
    }

    public static void setBranchData(String branchData) {
        FriendsLists.branchData = branchData;
    }

    public static Map<String, Object> getMap() {
        return map;
    }

    public static void setMap(Map<String, Object> map) {
        FriendsLists.map = map;
    }

    public static boolean isIsNewContactAddInCategory() {
        return isNewContactAddInCategory;
    }

    public static void setIsNewContactAddInCategory(boolean isNewContactAddInCategory) {
        FriendsLists.isNewContactAddInCategory = isNewContactAddInCategory;
    }

    public static ArrayList<UploadPhoneNumberResponse.NewNumber> newNumberArrayList = new ArrayList<>();

    public static ArrayList<UploadPhoneNumberResponse.NewNumber> getNewNumberArrayList() {
        return newNumberArrayList;
    }

    public static void setNewNumberArrayList(ArrayList<UploadPhoneNumberResponse.NewNumber> newNumberArrayList) {
        FriendsLists.newNumberArrayList = newNumberArrayList;
    }

    public static ContactsViewModal getContactsViewModal() {
        return contactsViewModal;
    }

    public static void setContactsViewModal(ContactsViewModal contactsViewModal) {
        FriendsLists.contactsViewModal = contactsViewModal;
    }

    public static ArrayList<SubCategoryList> getSubCategoryLists(Context ctx, int j) {
        subCategoryLists = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        switch (j) {
            case 1:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.House_Hold_Sub));
                break;
            case 2:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Health_Sub_list));
                break;
            case 3:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Travel_Sub_List));
                break;
            case 4:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Auto_Sub_List));
                break;
            case 5:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.B2B_Sub_List));
                break;
            case 6:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Financial_Sub_list));
                break;
            case 7:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Real_State_Sub_List));
                break;
            case 8:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Technology_Sub_List));
                break;
            case 9:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Entertainment_Sub_list));
                break;
            case 10:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Pets_Sub_list));
                break;
            case 11:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Resturants_Sub_List));
                break;
            case 12:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Wedding_Sub_List));
                break;
            case 13:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Education_Sub_list));
                break;
            case 14:
                stringList = Arrays.asList(ctx.getResources().getStringArray(R.array.Sports_SubList));
                break;
            default:
                break;
        }
        for (int i = 0; i < stringList.size(); i++) {
            SubCategoryList subCategoryList = new SubCategoryList(i + 1, 0, stringList.get(i));
            subCategoryLists.add(subCategoryList);
        }
        return subCategoryLists;
    }

    public static ArrayList<SubCategoryMainList> getSubCategoryMainLists(Context ctx, int id, ArrayList<HomeScreenCategory> categoryArrayList) {
        for (int i = 0; i < categoryArrayList.size(); i++) {
            ArrayList<SubCategoryList> subCategoryLists = getSubCategoryLists(ctx, i + 1);
            SubCategoryMainList subCategoryMainList = new SubCategoryMainList(categoryArrayList.get(i).getcName(),
                    categoryArrayList.get(i).getId(), subCategoryLists);
            subCategoryMainLists.add(subCategoryMainList);
        }
        return subCategoryMainLists;
    }

    public static ArrayList<HomeScreenCategory> getHomeScreenCategories() {
        return homeScreenCategories;
    }

    public static void setHomeScreenCategories(ArrayList<HomeScreenCategory> homeScreenCategories) {
        FriendsLists.homeScreenCategories = homeScreenCategories;
    }

    public static List<String> getNameList(Context ctx) {
        nameList = Arrays.asList(ctx.getResources().getStringArray(R.array.Category));
        return nameList;
    }

    public static List<String> getColorList(Context ctx) {
        colorList = Arrays.asList(ctx.getResources().getStringArray(R.array.Category_Color));
        return colorList;
    }


}
