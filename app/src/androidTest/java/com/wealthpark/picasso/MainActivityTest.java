package com.wealthpark.picasso;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.wealthpark.picasso.activity.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by sasa on 2/23/16.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule(MainActivity.class);

    @Test
    public void showColorPickerDialog() {
        onView(withId(R.id.action_color)).perform(click());
        onView(withText(mActivityRule.getActivity().getString(R.string.select_color))).check(matches(isDisplayed()));
    }

    @Test
    public void showDeleteDrawingDialog() {
        onView(withId(R.id.action_delete)).perform(click());
        onView(withText(mActivityRule.getActivity().getString(R.string.delete_drawing))).check(matches(isDisplayed()));
    }

    @Test
    public void showSettingsActivity() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withText(R.string.title_activity_settings)).perform(click());
        onView(withText(mActivityRule.getActivity().getString(R.string.pref_title_google_analytics))).check(matches(isDisplayed()));
    }
}
