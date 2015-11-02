package gov.cdc.mmwrexpress;


import android.app.Application;
import android.support.design.internal.NavigationMenuView;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ArticleListActivityTest {
        @Rule
        public ActivityTestRule<ArticleListActivity> mActivityTestRule =
                new ActivityTestRule<>(ArticleListActivity.class);

    @Test
    public void clickOnNavDrawerButton_OpenNavDrawer(){
        //drawer should be closed
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed()));

        //perform click on NavDrawer button
        onView(withContentDescription("Navigate up")).perform(click());

        //Check if drawer is open
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen()));
    }

    @Test
    public void clickBackButtonWhileNavDrawerIsOpen_CloseNavDrawer(){
        //drawer should be open
        onView(withId(R.id.drawer_layout))
                .perform(DrawerActions.open());

        //press back
        pressBack();

        //drawer should close
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed()));
    }

    /*@Test
    public void clickArticlesNavDrawerItem_CloseNavDrawer(){

    }*/

}