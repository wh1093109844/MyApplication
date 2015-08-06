package com.example.hero.tablet;

import android.support.test.espresso.Espresso;

import android.test.ActivityInstrumentationTestCase2;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

/**
 * Created by hero on 8/6/2015.
 */
public class SimpleNameTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public static final String NAME = "John";
    public static final String MESSAGE = "你好，" + NAME + "!";

    public SimpleNameTest() {
        super(MainActivity.class);
    }
    public SimpleNameTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testEnterName() {
        onView(withId(R.id.edit_text)).perform(typeText(NAME));
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.text)).check(matches(withText(MESSAGE)));
    }
}
