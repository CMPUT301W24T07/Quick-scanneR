package com.example.quickscanner;

import static junit.framework.TestCase.assertEquals;

import com.example.quickscanner.model.*;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ModelTesting {
    /**
     * create a mock Event
     * @return User
     */
    public Event MockEvent(){
        Event event = new Event("Dylan's Event", "This is a mock event for testing", MockUser().getUid(),
                    "05,03,2001", "Edmonton");
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

    /**
     * Test Admin Access in User Class
     * @return void
     */
    @Test
    public void testUserClass(){
        User user = new User("Dylan", "dndu@ualberta.ca", "dndu.linkedin.com", "https://firebasestorage.googleapis.com/v0/b/quick-scanner-54fbc.appspot.com/o/Test%20Poster%202.png?alt=media&token=fb26816d-a7b5-4d35-89a0-65d7d5db31b3");
        // admin should be default false
        assertEquals(false, user.getAdmin().booleanValue());
        // TODO: Next sprint, implement and test admin access for specific users.
    }

    /**
     * Test adding organizing events to User
     * @return void
     */
    @Test
    public void testUserOrganizedEvent() {
        User user = MockUser();
        assertEquals(false, user.getAdmin().booleanValue());
        // size should be 0 as user has not signed up for anything yet
        assertEquals(user.getOrganizedEvents().size(), 0);
        assertEquals(user.getAttendingEvents().size(), 0);
        // add events
        Event event = MockEvent();
        ArrayList<String> orgEvents = new ArrayList<>();
        orgEvents.add(event.getEventID());
        user.setOrganizedEvents(orgEvents);
        // Test Size of organized events
        assertEquals(1, user.getOrganizedEventsSize());

    }


    /**
     * Test adding attending events to User
     * @return void
     */
    @Test
    public void testUserAttendingEvent() {
        User user = MockUser();
        assertEquals(false, user.getAdmin().booleanValue());
        // size should be 0 as user has not signed up for anything yet
        assertEquals(user.getOrganizedEvents().size(), 0);
        assertEquals(user.getAttendingEvents().size(), 0);
        // add events
        Event event = MockEvent();
        ArrayList<String> attendEvents = new ArrayList<>();
        attendEvents.add(event.getEventID());
        user.setAttendingEvents(attendEvents);
        // Test Size of organized events
        assertEquals(1, user.getAttendingEventsSize());
    }

    /**
     * Test adding attending Users to an Event
     * @return void
     */
    @Test
    public void testEventHavingUsers() {
        Event event = MockEvent();
        // size should be 0 as no user has signed up for anything yet
        assertEquals(event.getSignUps().size(), 0);
        assertEquals(event.getCheckIns().size(), 0);
        // create users to sign in and check in
        User user = MockUser();
        ArrayList<String> signups = new ArrayList<>();
        ArrayList<String> checkins = new ArrayList<>();
        signups.add(user.getUid());
        checkins.add(user.getUid());
        // sign up and check in  users
        event.setCheckIns(checkins);
        event.setSignUps(signups);
        // Test Size of organized events
        assertEquals(1, event.getSignUps().size());
        assertEquals(1, event.getCheckIns().size());
    }





}









