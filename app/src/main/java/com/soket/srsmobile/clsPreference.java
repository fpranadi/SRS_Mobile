package com.soket.srsmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class clsPreference {
    static final String KEY_USERNAME_SEDANG_LOGIN = "Username_logged_in";
    static final String KEY_STATUS_SEDANG_LOGIN = "Status_logged_in";
    static final String KEY_USERGROUP_SEDANG_LOGIN = "UserGroup_logged_in";
    static final String KEY_INSTITUTION_CODE="Kode institusi";
    static final String KEY_INSTITUTION_NAME="Nama Institusi";
    static final String KEY_KODE_OPNAME_AKTIF="Kode Opname Aktif";
    static final String KEY_LOKASI_OPNAME_AKTIF="Lokasi Opname Aktif";

    /** Pendlakarasian Shared Preferences yang berdasarkan paramater context */
    private static SharedPreferences getSharedPreference(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public  void setRegisteredInstitution(Context context, String institutionCode, String institutionName){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_INSTITUTION_CODE, institutionCode);
        editor.putString(KEY_INSTITUTION_NAME, institutionName);
        editor.apply();
    }
    public  String getRegisteredInstitutionCode(Context context){
        return getSharedPreference(context).getString(KEY_INSTITUTION_CODE,"000000");
    }
    public  String getRegisteredInstitutionName(Context context){
        return getSharedPreference(context).getString(KEY_INSTITUTION_NAME,"Koperasi Indonesia");
    }

    public  void setLoggedInUser(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USERNAME_SEDANG_LOGIN, username);
        editor.apply();
    }
    public  String getLoggedInUser(Context context){
        return getSharedPreference(context).getString(KEY_USERNAME_SEDANG_LOGIN,"");
    }

    public  void setLoggedInStatus(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(KEY_STATUS_SEDANG_LOGIN,status);
        editor.apply();
    }
    public  boolean getLoggedInStatus(Context context){
        return getSharedPreference(context).getBoolean(KEY_STATUS_SEDANG_LOGIN,false);
    }

    public  void setLoggedInUserGroup(Context context, String usergroup){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_USERGROUP_SEDANG_LOGIN,usergroup);
        editor.apply();
    }
    public  String getLoggedInUserGroup(Context context){
        return getSharedPreference(context).getString(KEY_USERGROUP_SEDANG_LOGIN,"");
    }
    public  void clearLoggedInUser (Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_USERNAME_SEDANG_LOGIN);
        editor.remove(KEY_STATUS_SEDANG_LOGIN);
        editor.remove(KEY_USERGROUP_SEDANG_LOGIN);
        editor.apply();
    }

    public  void setRegisteredOpnameAktif(Context context, String KodeOpname, String LokasiOpname){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_KODE_OPNAME_AKTIF, KodeOpname);
        editor.putString(KEY_LOKASI_OPNAME_AKTIF, LokasiOpname);
        editor.apply();
    }
    public  String getRegisteredKodeOpnameAktif(Context context){
        return getSharedPreference(context).getString(KEY_KODE_OPNAME_AKTIF,"");
    }
    public  String getRegisteredLokasiOpnameAktif(Context context){
        return getSharedPreference(context).getString(KEY_LOKASI_OPNAME_AKTIF,"");
    }

    public  void clearRegisteredOpname (Context context){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_KODE_OPNAME_AKTIF);
        editor.remove(KEY_LOKASI_OPNAME_AKTIF);
        editor.apply();
    }
}
