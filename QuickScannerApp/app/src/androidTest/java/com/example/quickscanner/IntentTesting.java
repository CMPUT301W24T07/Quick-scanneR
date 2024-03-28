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
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertEquals;

import static java.util.EnumSet.allOf;
import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.example.quickscanner.ui.adminpage.AdminActivity;
import com.example.quickscanner.ui.adminpage.BrowseEventsActivity;
import com.example.quickscanner.ui.adminpage.BrowseProfilesActivity;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.example.quickscanner.ui.settings.SettingsActivity;
import com.google.firebase.Timestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

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

    /* Is called before every test
     * Set up an intent listener
     * */
    @Before
    public void setUp() throws Exception {
        // Initialize Espresso-Intents before each test
        Intents.init();
    }

    /* Is called after every test
     * Deletes/tearsdown the intent listener
     * */
    @After
    public void tearDown() throws Exception {
        Intents.release();
    }

    /**
     * create a mock Event
     * @return User
     */
    public Event MockEvent(){
        Timestamp timestamp = new Timestamp(new Date());
        Event event = new Event("Dylan's Event", "This is a mock event for testing", MockUser().getUid(),
                timestamp, "Edmonton");
        return event;
    }

    /**
     * create a mock User
     * @return User
     */
    public User MockUser(){
        User user = new User("Dylan", "dndu@ualberta.ca", "dndu.linkedin.com", "https://firebasestorage.googleapis.com/v0/b/quick-scanner-54fbc.appspot.com/o/Test%20Poster%202.png?alt=media&token=fb26816d-a7b5-4d35-89a0-65d7d5db31b3");
        return user;
    }

    /**
     * create a mock User with null attributes
     * @return User
     */
    public User MockEmptyUser(){
        User user = new User();
        return user;
    }

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = getInstrumentation().getTargetContext();
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


    /* Test the navigation to add Events works
    *  ( Does not add/create any Events )
    * */
    @Test
    public void testAddEvents(){
        /* test if add event button works */
        onView(withId(R.id.navigation_events)).perform(click());    // event fragment
        onView(withId(R.id.fob_createEvent)).perform(click());      // add event button
        intended(hasComponent(AddEventActivity.class.getName()));   // check if intent worked
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }

    /*
    *   Test If the redirection to settings work properly
    * */
    @Test
    public void testIntentSettings(){
        /* test if clicking settings on the menu works  */
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on settings
        onView(withText("Settings")).perform(click());
        // check if intent worked
        intended(hasComponent(SettingsActivity.class.getName()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }

    /*
     *   Test if the redirection to profile work properly
     * */
    @Test
    public void testIntentProfile(){
        /* test if clicking profile on the menu works  */
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on profile
        onView(withText("Profile")).perform(click());
        // check if intent worked
        intended(hasComponent(ProfileActivity.class.getName()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }

    /*
     *   Test if the redirection to profile work properly
     * */
    @Test
    public void testIntentAdmin(){
        /* test if clicking Admin Page on the menu works  */
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on Admin Page
        onView(withText("Admin Page")).perform(click());
        // check if intent worked
        intended(hasComponent(AdminActivity.class.getName()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }

    /*
     *   Test if the redirection to BrowseEvents work properly
     * */
    @Test
    public void testIntentAdminBrowseEvents(){
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on Admin Page
        onView(withText("Admin Page")).perform(click());
        // check if intent worked
        intended(hasComponent(AdminActivity.class.getName()));
        // click on BrowsePage
        onView(withText("Browse Events")).perform(click());
        // check if intent worked
        intended(hasComponent(BrowseEventsActivity.class.getName()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.admin_activity_main)).check(matches(isDisplayed()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }


    /*
     *   Test if the redirection to BrowseProfiles work properly
     * */
    @Test
    public void testIntentAdminBrowseProfiles(){
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on Admin Page
        onView(withText("Admin Page")).perform(click());
        // check if intent worked
        intended(hasComponent(AdminActivity.class.getName()));
        // click on BrowsePage
        onView(withText("Browse Profiles")).perform(click());
        // check if intent worked
        intended(hasComponent(BrowseProfilesActivity.class.getName()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.admin_activity_main)).check(matches(isDisplayed()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }



}
