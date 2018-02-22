package com.eservices.floamnx.coomcook;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;

import com.eservices.floamnx.coomcook.authentification.LoginActivity;
import com.eservices.floamnx.coomcook.splashSceen.KenBurnsView;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

@RunWith(AndroidJUnit4.class)
public class TestUI {

    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void init(){
        activityActivityTestRule.getActivity()
                .getSupportFragmentManager().beginTransaction();
    }


    public void toto() {
        onView(ViewMatchers.withId(R.id.register)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.userConfirmEmail)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void TestConnexionReussi() {
        connexion();


        onView(ViewMatchers.withId(R.id.header_navigation_drawer_media_image)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());

    }

    public void connexion(){
        onView(ViewMatchers.withId(R.id.userEmail)).perform(ViewActions.typeTextIntoFocusedView("lille2@lille2.fr"));
        onView(ViewMatchers.withId(R.id.userPassword)).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.userPassword)).perform(ViewActions.typeTextIntoFocusedView("lille123"));
        onView(isRoot()).perform(ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.login)).perform(ViewActions.click());
    }
    @Test
    public void TestChercher() {
        connexion();
        onView(ViewMatchers.withText("Chercher")).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.activity_search_bar_shop_filters)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(2000));

        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }
    @Test
    public void TestDidactitiel() {
        connexion();
        onView(ViewMatchers.withText("Didacticiel")).perform(ViewActions.click());
        onView(isRoot()).perform(waitFor(2000));
        onView(ViewMatchers.withText("Suivant")).perform(ViewActions.click());
        onView(ViewMatchers.withText("Suivant")).perform(ViewActions.click());
        onView(ViewMatchers.withText("Démarrer")).perform(ViewActions.click());

        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(2000));

        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }

    @Test
    public void TestVoirDetail() {
        connexion();
        onView(ViewMatchers.withText("Chercher")).perform(ViewActions.click());
        onView(isRoot()).perform(waitFor(2000));
        onView(isRoot()).perform(ViewActions.swipeRight());

        onView(ViewMatchers.withId(R.id.eventTitle)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(2000));

        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }

    @Test
    public void TestVoirProfil() {
        connexion();
        onView(ViewMatchers.withText("Maxime")).perform(ViewActions.click());
        onView(isRoot()).perform(waitFor(2000));
        onView(ViewMatchers.withId(R.id.surname)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(2000));

        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }

    @Test
    public void TestVoirEditerProfil() {
        connexion();
        onView(ViewMatchers.withText("Maxime")).perform(ViewActions.click());
        onView(isRoot()).perform(waitFor(2000));
        onView(ViewMatchers.withId(R.id.surname)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.editProfile)).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.uploadPhoto)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(1000));
        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }

    @Test
    public void TestVoirProposer() {
        connexion();
        onView(ViewMatchers.withText("Proposer")).perform(ViewActions.click());
        onView(isRoot()).perform(waitFor(2000));
        onView(ViewMatchers.withId(R.id.eventName)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(1000));
        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }

    @Test
    public void TestMessagerie() {
        connexion();
        onView(ViewMatchers.withText("Messagerie")).perform(ViewActions.click());
        onView(isRoot()).perform(waitFor(1000));
        onView(isRoot()).perform(ViewActions.click());
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(ViewMatchers.withId(R.id.partnerName)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        onView(ViewMatchers.withId(R.id.drawer_layout)).perform(actionOpenDrawer());
        onView(isRoot()).perform(waitFor(1000));
        onView(ViewMatchers.withText("Se déconnecter")).perform(ViewActions.click());
    }

    private static ViewAction actionOpenDrawer() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(DrawerLayout.class);
            }

            @Override
            public String getDescription() {
                return "open drawer";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((DrawerLayout) view).openDrawer(GravityCompat.START);
            }
        };
    }

    private ViewAction myCusomViewAction() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(KenBurnsView.class);
            }

            @Override
            public String getDescription() {
                return "toto";
            }

            @Override
            public void perform(UiController uiController, View view) {
                Log.e("test", "perform");
                KenBurnsView mView = (KenBurnsView) view;
                mView.performClick();
            }
        };
    }
    public ViewAction waitFor(final long milis)  {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints()  {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for"+ milis +" milliseconds.";
            }

            @Override
            public void perform( UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(milis);
            }
        };
    }
}
