package com.example.quickscanner;

import static android.os.SystemClock.sleep;
import static android.provider.Settings.System.getString;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static java.util.EnumSet.allOf;

import android.content.Context;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.GrantPermissionRule;

import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.addevent.AddEventActivity;
import com.example.quickscanner.ui.adminpage.AdminActivity;
import com.example.quickscanner.ui.adminpage.BrowseEventsActivity;
import com.example.quickscanner.ui.adminpage.BrowseImagesActivity;
import com.example.quickscanner.ui.adminpage.BrowseProfilesActivity;
import com.example.quickscanner.ui.my_events.MyEvents_Activity;
import com.example.quickscanner.ui.profile.ProfileActivity;
import com.example.quickscanner.ui.settings.SettingsActivity;
import com.example.quickscanner.ui.viewevent.ViewEventActivity;
import com.google.firebase.Timestamp;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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

    // Grant location access permissions.
    // For testing purposes.
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule
            .grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

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


    /*
     *   Test if the redirection to BrowseImages work properly
     * */
    @Test
    public void testIntentAdminBrowseImages(){
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on Admin Page
        onView(withText("Admin Page")).perform(click());
        // check if intent worked
        intended(hasComponent(AdminActivity.class.getName()));
        // click on BrowsePage
        onView(withText("Browse Images")).perform(click());
        // check if intent worked
        intended(hasComponent(BrowseImagesActivity.class.getName()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.admin_activity_main)).check(matches(isDisplayed()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
    }


    /*
     *   Test if the redirection to BrowseEvents work properly
     * */
    @Test
    public void testAdminBrowseEvents() {
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
        // click on an event
        sleep(1500); // get some sleep sid zzz
        AssertionError eCode = null;
        try {
            onView(withId(R.id.BrowseEventsListView)).check(matches(hasChildCount(0)));
        } catch (AssertionError e) {
            // listview is most likely empty, so didn't do anything.
            eCode = e;
        }
        if (eCode == null) {
            // listview is not empty. so open an event.
            onView(withId(R.id.BrowseEventsListView)).perform(click());
            // check if opening event worked
            intended(hasComponent(BrowseEventsActivity.class.getName()));
            // Opens menu
            Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
            // check if the 'delete' option exists
            onView(withText(R.string.delete)).check(matches(isDisplayed()));
        }

    }

    /*
     *   Test if MyEvents_Activity works properly
     * */
    @Test
    public void testIntentMyEvents(){
        /* test if clicking settings on the menu works  */
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on settings
        onView(withText("My Events")).perform(click());
        // check if intent worked
        intended(hasComponent(MyEvents_Activity.class.getName()));
        // check bottom nav bar for my_events.
        // Attending Events click
        onView(withId(R.id.navigation_attending_events)).perform(click());
        onView(withId(R.id.navigation_attending_events)).check(matches(isDisplayed()));
        // Organized Events click
        onView(withId(R.id.navigation_organized_events)).perform(click());
        onView(withId(R.id.navigation_organized_events)).check(matches(isDisplayed()));
        // Test the back button
        onView(isRoot()).perform(ViewActions.pressBack());
    }


    /*
     *   Test if the BrowseProfiles Activity works properly
     * */
    @Test
    public void testAdminBrowseProfiles() {
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
        // click on a profile
        sleep(1500); // get some sleep sid zzz
        AssertionError eCode = null;
        try {
            onView(withId(R.id.BrowseProfilesListView)).check(matches(hasChildCount(0)));
        } catch (AssertionError e) {
            // listview is most likely empty, so didn't do anything.
            eCode = e;
        }
        if (eCode == null) {
            // listview is not empty. so open a profile.
            onView(withId(R.id.BrowseProfilesListView)).perform(click());
            // check if opening event worked
            intended(hasComponent(BrowseProfilesActivity.class.getName()));
            // check if the 'delete' option exists
            onView(withText(R.string.delete)).check(matches(isDisplayed()));
        }

    }

    /*
     *   Test if the BrowseImages Activity works properly
     * */
    @Test
    public void testAdminBrowseImages() {
        // Opens menu
        Espresso.openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        // clicks on Admin Page
        onView(withText("Admin Page")).perform(click());
        // check if intent worked
        intended(hasComponent(AdminActivity.class.getName()));
        // click on BrowsePage
        onView(withText("Browse Images")).perform(click());
        // check if intent worked
        intended(hasComponent(BrowseProfilesActivity.class.getName()));
        sleep(1500); // get some sleep sid zzz
        // click on an image
        AssertionError eCode = null;
        try {
            onView(withId(R.id.BrowseImagesListView)).check(matches(hasChildCount(0)));
        } catch (AssertionError e) {
            // listview is most likely empty, so didn't do anything.
            eCode = e;
        }
        if (eCode == null) {
            // listview is not empty. so open an Image.
            onView(withId(R.id.BrowseImagesListView)).perform(click());
        }
    }

    /*
     *   Test viewing events
     * */
    @Test
    public void testViewEvents() {
        // Events click
        onView(withId(R.id.navigation_events)).perform(click());
        onView(withId(R.id.navigation_events)).check(matches(isDisplayed()));
        sleep(1500); // get some sleep sid zzz
        // click on an Event
        AssertionError eCode = null;
        try {
            onView(withId(R.id.event_listview)).check(matches(hasChildCount(0)));
        } catch (AssertionError e) {
            // listview is most likely empty, so didn't do anything.
            eCode = e;
        }
        if (eCode == null) {
            // listview is not empty. so open an Event.
            onView(withId(R.id.event_listview)).perform(click());
            // check if intent worked
            intended(hasComponent(ViewEventActivity.class.getName()));
        }
    }







}
