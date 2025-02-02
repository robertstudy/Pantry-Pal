package com.example.pantrypal;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;

/**
 * This testing file uses ActivityScenarioRule instead of ActivityTestRule
 * to demonstrate system testings cases
 */
@RunWith(AndroidJUnit4.class)
public class MichaelSystemTest {
    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<IngredientListView> activityScenarioRule = new ActivityScenarioRule<>(IngredientListView.class);
//    @Rule
//    public ActivityScenarioRule<AdminChatActivity> activityScenarioRule2 = new ActivityScenarioRule<>(AdminChatActivity.class);

    /**
     * Start the server and run this test
     *
     * This test uses the default string value specified within the activity
     * instead of the input string from edittext
     *
     * the default string value is set by activityScenarioRule upon activity creation
     * meanwhile the switch is set to reading the default value
     //     */
//    @Test
//    public void deleteIngredient(){
//        String resultString = "hotdog";
//
//        activityScenarioRule.getScenario().onActivity(activity -> {
////            activity.ingredients.add(new Ingredient("hotdog", "hotdog", 1));
//            activity.refreshRecyleView();
//        });
//
//        onView(withId(R.id.i_delete)).perform(click());
//        // Put thread to sleep to allow volley to handle the request
//        try {
//            Thread.sleep(SIMULATED_DELAY_MS);
//        } catch (InterruptedException e) {}
//
//        // Verify that volley returned the correct value
//        onView(withText(resultString)).check(matches(withText(endsWith(resultString))));;
//    }

    @Test
    public void addIngredientTest() {

        String testString = "hotdog";
        String resultString = "hotdog";

        activityScenarioRule.getScenario().onActivity(activity -> {
            LoginInfo.username = "user";
        });

        // Type in testString and send request
        onView(withId(R.id.add_ingredient_button)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Click button to submit
        onView(withId(R.id.edit_iname)).perform(typeText(testString));

        onView(withId(R.id.add_ingredient)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withText(testString)).check(matches(withText(endsWith(resultString))));
    }

    @Test
    public void recyclerViewTest(){
        String resultString = "hotdog";

        activityScenarioRule.getScenario().onActivity(activity -> {
//            activity.ingredients.add(new Ingredient("hotdog", "hotdog", 1));
            activity.refreshRecyleView();
        });

//        onView(withId(R.id.submit)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withText(resultString)).check(matches(withText(endsWith(resultString))));
    }

    /**
     * Start the server and run this test
     *
     * This test uses the user input string value from edittext
     * instead of the default string within the activity
     *
     * the default string value is set to null by activityScenarioRule upon activity creation
     * meanwhile the switch is set to reading the user input value
     */
    @Test
    public void nutritionInformation(){
        String resultString = "hotdog";

        activityScenarioRule.getScenario().onActivity(activity -> {
//            activity.ingredients.add(new Ingredient("hotdog", "hotdog", 1));
            activity.refreshRecyleView();
        });

        onView(withId(R.id.i_information)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withText(resultString)).check(matches(withText(endsWith(resultString))));;
    }


    /**
     * Start the server and run this test
     *
     * This test uses the default string value specified within the activity
     * instead of the input string from edittext
     *
     * the default string value is set by activityScenarioRule upon activity creation
     * meanwhile the switch is set to reading the default value
     */
    @Test
    public void adminChatActiviyTest() {
        String resultString = "Hello";
        String inputString = "Hello";

        activityScenarioRule.getScenario().onActivity(activity ->
        {
            LoginInfo.username = "user";

        });
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        onView(withId(R.id.menu)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}
        onView(withId(R.id.chat_button)).perform(click());
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        onView(withId(R.id.message_text)).perform(typeText(inputString), closeSoftKeyboard());

        onView(withId(R.id.Send_chat_button)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        onView(withId(R.id.chat_display)).check(matches(withText(endsWith(resultString))));;
    }

    /**
     * Start the server and run this test
     *
     * This test uses the user input string value from edittext
     * instead of the default string within the activity
     *
     * the default string value is set to null by activityScenarioRule upon activity creation
     * meanwhile the switch is set to reading the user input value
     */

}
