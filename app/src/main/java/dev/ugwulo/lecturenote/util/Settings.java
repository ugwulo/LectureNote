package dev.ugwulo.lecturenote.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

/**
 * Preferences for student
 */
public class Settings {
    //Keys for Shared preferences
    //This would be the name of our shared preferences
    public static final String SHARED_PREF_NAME = "lecture_note";

    //Check if the User is logged in or not
    public static final String LOGGED_IN_SHARED_PREF = "loggedin";

    public static final String STUDENT_LOGIN = "isStudentLogin";
    public static final String  LECTURER_LOGIN = "isLecturerLogin";

    //Check if its first time use
    private static final String IS_FIRST_TIME_LAUNCH = "isFirstTimeLaunch";

    private static SharedPreferences settings;

    private static SharedPreferences defaultPrefs;

    public static void init(@NonNull Context context){
        settings = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean isLoggedIn(){
        return settings.getBoolean(LOGGED_IN_SHARED_PREF, false);
    }

    public static void setLoggedInSharedPref(boolean loggedInSharedPref){
        settings.edit()
                .putBoolean(LOGGED_IN_SHARED_PREF, loggedInSharedPref)
                .apply();
    }

    public static boolean isStudentLogin(){
        return settings.getBoolean(STUDENT_LOGIN, false);
    }

    public static void setStudentLogin(boolean studentLoginSharedPref){
        settings.edit()
                .putBoolean(STUDENT_LOGIN, studentLoginSharedPref)
                .apply();
    }

    public static boolean isLecturerLogin(){
        return settings.getBoolean(LECTURER_LOGIN, false);
    }

    public static void setLecturerLogin(boolean lecturerLogin){
        settings.edit()
                .putBoolean(LECTURER_LOGIN, lecturerLogin)
                .apply();
    }

    public static void setIsFirstTimeLaunch(boolean isFirstTimeLaunch){
        settings.edit()
                .putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch)
                .apply();
    }

    public static boolean isFirstTimeLaunch(){
        return settings.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

}
