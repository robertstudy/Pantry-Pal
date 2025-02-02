package com.example.pantrypal;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AdrianSystemTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> loginScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testCreateUser() throws InterruptedException {
        onView(withId(R.id.signup_button)).perform(click());
        onView(withId(R.id.signup_username_edt)).perform(typeText("newUser"));
        onView(withId(R.id.signup_password_edt)).perform(typeText("newPassword"));
        onView(withId(R.id.signup_confirm_edt)).perform(typeText("newPassword"));
        onView(withId(R.id.signup_btn)).perform(click());
        onView(withId(R.id.navigation_stores)).perform(click());
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        if (uiDevice.findObject(By.text("While using the app")) != null) {
            uiDevice.findObject(By.text("While using the app")).click();
        }
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.exit_stores_button)).perform(click());
        onView(withId(R.id.navigation_home)).perform(click());

        onView(withId(R.id.delete_user_button)).perform(click());
    }

    @Test
    public void testCreateRecipe(){
        //login
        onView(withId(R.id.login_username_edt)).perform(typeText("1"));
        onView(withId(R.id.login_password_edt)).perform(typeText("1"));
        onView(withId(R.id.login_button)).perform(click());

        //create recipe
        onView(withId(R.id.navigation_create_recipe)).perform(click());
        onView(withText("Create New Recipe")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.create_rname)).perform(typeText("Test food"));
        onView(withId(R.id.total_time_hr)).perform(typeText("1"));
        onView(withId(R.id.total_time_min)).perform(typeText("30"));
        onView(withId(R.id.add_ingredient_btn)).perform(click());
        onView(withId(R.id.edit_amount)).perform(typeText("1 cup"));
        onView(withId(R.id.edit_ingredient)).perform(typeText("Test ingredient"));
        onView(withId(R.id.add_step_btn)).perform(click());
        onView(withId(R.id.edit_step)).perform(typeText("Make test food"));
        onView(withId(R.id.upload_button)).perform(click());

        //view created recipe
        onView(withId(R.id.visit_recipe_button)).perform(click());
        onView(withId(R.id.return_button)).perform(click());

        //delete recipe
        onView(withId(R.id.edit_recipe_button)).perform(click());
        onView(withId(R.id.delete_button)).perform(click());
    }

    @Test
    public void testHomeFragments() throws InterruptedException {
        UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        onView(withId(R.id.login_username_edt)).perform(typeText("1"));
        onView(withId(R.id.login_password_edt)).perform(typeText("1"));
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.navigation_search)).perform(click());
        onView(withId(R.id.navigation_stores)).perform(click());
        if (uiDevice.findObject(By.text("While using the app")) != null) {
            uiDevice.findObject(By.text("While using the app")).click();
        }
        TimeUnit.SECONDS.sleep(5);
        onView(withId(R.id.exit_stores_button)).perform(click());
        onView(withId(R.id.navigation_create_recipe)).perform(click());
        onView(withId(R.id.return_button)).perform(click());
        onView(withId(R.id.navigation_home)).perform(click());
    }

    @Test
    public void testSearchFragment(){
        onView(withId(R.id.login_username_edt)).perform(typeText("1"));
        onView(withId(R.id.login_password_edt)).perform(typeText("1"));
        onView(withId(R.id.login_button)).perform(click());
        onView(withId(R.id.navigation_search)).perform(click());

        onView(withId(R.id.recipe_search_bar)).perform(click());
        onView(withId(R.id.vegan_check)).perform(click());
        onView(withId(R.id.keto_check)).perform(click());
        onView(withId(R.id.reset_filters_text_button)).perform(click());
        onView(withId(R.id.clear_focus_button)).perform(click());
    }

}
