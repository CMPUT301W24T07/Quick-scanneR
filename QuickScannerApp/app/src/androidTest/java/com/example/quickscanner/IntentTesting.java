package com.example.quickscanner;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertEquals;

import static java.util.EnumSet.allOf;
import android.content.Context;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quickscanner.ui.addevent.AddEventActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class IntentTesting {
    /*              Intent testing               */

    // launches main activity
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.quickscanner", appContext.getPackageName());
    }

    @Test
    public void testBottomNavMenu(){
        /* Testing the bottom navigation menu; scanner, announcements, and events button*/
        // Events click
        onView(withId(R.id.navigation_events)).perform(click());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
        // QR Scanner click
        onView(withId(R.id.navigation_scanner)).perform(click());
        onView(withId(R.id.navigation_scanner)).check(matches(isDisplayed()));
        // Announcements r click
        onView(withId(R.id.navigation_announcements)).perform(click());
        onView(withId(R.id.navigation_announcements)).check(matches(isDisplayed()));
    }


    /* Test the add events button */
    @Test
    public void testAddEvents(){
        /* test if add event button works */
        Intents.init(); // intents listener
        onView(withId(R.id.navigation_events)).perform(click());    // event fragment
        onView(withId(R.id.fob_createEvent)).perform(click());      // add event button
        intended(hasComponent(AddEventActivity.class.getName()));   // check if intent worked
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }





}
