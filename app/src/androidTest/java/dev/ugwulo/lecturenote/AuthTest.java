package dev.ugwulo.lecturenote;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Set;

import dev.ugwulo.lecturenote.util.Settings;
import dev.ugwulo.lecturenote.view.AuthActivity;
import dev.ugwulo.lecturenote.view.MainActivity;
import dev.ugwulo.lecturenote.view.OnboardingActivity;
import dev.ugwulo.lecturenote.view.home.HomeFragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class AuthTest {

    private String email = "test2@test.com";
    private String password = "qwerty";

    @Rule
    public ActivityTestRule<AuthActivity> activityScenarioRule =
            new ActivityTestRule<>(AuthActivity.class, true, false);

    @Before
    public void recreateActivity() {
        /*onView(withId(R.id.edit_reg_num)).perform(typeText("18EH/0059/CS"));
        onView(withId(R.id.btn_verify)).perform(ViewActions.click());*/
        activityScenarioRule.getActivity().startActivity(new Intent());
        Intents.init();
    }


    @Test
    public void loginSuccess() {
        Settings.init(getInstrumentation().getContext());
        Settings.setLecturerLogin(false);
        Settings.setLoggedInSharedPref(false);
        onView(withId(R.id.email)).perform(typeText(email));
        onView(withId(R.id.password)).perform(typeText(password));
        onView(withId(R.id.btn_sign_in)).perform(click());
        intended(hasComponent(hasClassName(HomeFragment.class.getName())));



    }

    @After
    public void releaseIntents() {
        /*Activity activity = activityScenarioRule.getActivity();
        SharedPreferences prefs = activity.getSharedPreferences("lecture_note", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("loggedin", false).apply();
        assertTrue(prefs.getBoolean("loggedin", false));


        prefs.edit().putBoolean("isLecturerLogin", false).apply();
        assertTrue(prefs.getBoolean("isLecturerLogin", false));*/
        Settings.init(getInstrumentation().getContext());
        Settings.setLecturerLogin(false);
        Settings.setLoggedInSharedPref(false);

    }


}
