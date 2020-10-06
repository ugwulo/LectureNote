package dev.ugwulo.lecturenote;


import android.content.Context;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dev.ugwulo.lecturenote.util.Settings;
import dev.ugwulo.lecturenote.view.OnboardingActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



/**
    Simple test for activity with espresso
 */
@RunWith(AndroidJUnit4.class)
public class OnBoardingTest {
    boolean flag = true;


    @Rule
    public ActivityScenarioRule<OnboardingActivity> activityScenarioRule =
            new ActivityScenarioRule<>(OnboardingActivity.class);


    @Before
    public void restartActivity() {
        activityScenarioRule.getScenario().recreate();
    }


    @Test
    public void loginAsLecturer() {
        onView(withId(R.id.edit_reg_num)).perform(typeText("18EH/0059/CS"));
        onView(withId(R.id.btn_verify)).perform(ViewActions.click());
        assertTrue(Settings.isFirstTimeLaunch());
        Settings.setIsFirstTimeLaunch(false);
        flag = !flag;

    }


    @Test
    public void loginAsStudent() {
        onView(withId(R.id.edit_staff_id)).perform(typeText("ID-2020-CS"));
        onView(withId(R.id.btn_verify)).perform(ViewActions.click());
        assertTrue(Settings.isFirstTimeLaunch());


    }


    @After
    public void afterLoginAsLecturer() {
        if (flag) {
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Settings.init(appContext.getApplicationContext());
            assertTrue(Settings.isLecturerLogin());
            assertFalse(Settings.isStudentLogin());
        }
    }


    @After
    public void afterLoginAsStudent() {
        if (!flag) {
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Settings.init(appContext.getApplicationContext());
            assertTrue(Settings.isStudentLogin());
            assertFalse(Settings.isLecturerLogin());
        }
    }

    @After
    public void afterSetFirstTimeStatus() {
        Settings.setIsFirstTimeLaunch(false);
    }


}
