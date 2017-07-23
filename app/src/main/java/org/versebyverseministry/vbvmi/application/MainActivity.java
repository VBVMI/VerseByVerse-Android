package org.versebyverseministry.vbvmi.application;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;


import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackDelegate;
import com.zhuinden.simplestack.KeyContextWrapper;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import org.versebyverseministry.vbvmi.R;
import org.versebyverseministry.vbvmi.api.APIManager;
import org.versebyverseministry.vbvmi.api.DatabaseManager;
import org.versebyverseministry.vbvmi.fragments.answers.AnswersKey;
import org.versebyverseministry.vbvmi.fragments.studies.StudiesKey;
import org.versebyverseministry.vbvmi.model.Category;
import org.versebyverseministry.vbvmi.model.Lesson;
import org.versebyverseministry.vbvmi.model.Study;
import org.versebyverseministry.vbvmi.util.Multistack;
import org.versebyverseministry.vbvmi.util.ViewUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.versebyverseministry.vbvmi.application.MainActivity.StackType.STUDIES;
import static org.versebyverseministry.vbvmi.application.MainActivity.StackType.ANSWERS;

public class MainActivity extends AppCompatActivity implements StateChanger {

    private static final String TAG = "MainActivity";

    public enum StackType {
        STUDIES,
        ANSWERS
    }

    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.coordinator_root)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    Multistack multistack;

    private boolean isAnimating; // unfortunately, we must manually ensure that you can't navigate while you're animating.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.multistack = new Multistack();

        multistack.add(STUDIES.name(), new BackstackDelegate(null));
        multistack.add(ANSWERS.name(), new BackstackDelegate(null));

        Multistack.NonConfigurationInstance nonConfigurationInstance = (Multistack.NonConfigurationInstance) getLastCustomNonConfigurationInstance();

        multistack.onCreate(savedInstanceState);

        multistack.onCreate(STUDIES.name(), savedInstanceState, nonConfigurationInstance, StudiesKey.create());
        multistack.onCreate(ANSWERS.name(), savedInstanceState, nonConfigurationInstance, AnswersKey.create());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        APIManager.getInstance().downloadStudies();
        APIManager.getInstance().downloadCategories();

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String newStack = null;
                switch (item.getItemId()) {
                    case R.id.navigation_studies:
                        newStack = STUDIES.name();
                        break;
                    case R.id.navigation_answers:
                        newStack = ANSWERS.name();
                        break;
                }

                if (multistack.getSelectedStackIdentifier() != newStack) {
                    removePreviousFragment();
                    multistack.setSelectedStack(newStack);
                }
                return true;
            }
        });

        multistack.setStateChanger(this);


        DatabaseManager.observer.registerForContentChanges(this, Lesson.class);
        DatabaseManager.observer.registerForContentChanges(this, Category.class);
        DatabaseManager.observer.registerForContentChanges(this, Study.class);

    }


    public Multistack getMultistack() {
        return multistack;
    }

    private void removePreviousFragment() {
        if (previousFragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
            fragmentTransaction.remove(previousFragment);
            fragmentTransaction.commit();
        }
    }

    public static MainActivity get(Context context) {
        // noinspection ResourceType
        return (MainActivity) context.getSystemService(TAG);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return multistack.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        multistack.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if (!multistack.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        multistack.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        multistack.persistViewToState(root.getChildAt(0));
        multistack.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        DatabaseManager.observer.unregisterForContentChanges(this);
        multistack.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object getSystemService(@NonNull  String name) {
        if(multistack != null) {
            BackstackDelegate stack = multistack.get(name);
            if(stack != null) {
                return stack;
            }
        }
        if(TAG.equals(name)) {
            return this;
        }
        return super.getSystemService(name);
    }

//    private void exchangeViewForKey(Key newKey, final int direction) {
//        multistack.persistViewToState(root.getChildAt(0));
//        multistack.setSelectedStack(newKey.stackIdentifier());
//        Context newContext = new KeyContextWrapper(this, newKey);
//        final View previousView = root.getChildAt(0);
//        final View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
//        multistack.restoreViewFromState(newView);
//        root.addView(newView);
//
//        if(direction == StateChange.REPLACE) {
//            finishStateChange(previousView);
//        } else {
//            isAnimating = true;
//
//            ViewUtils.waitForMeasure(newView, new ViewUtils.OnMeasuredCallback() {
//                @Override
//                public void onMeasured(View view, int width, int height) {
//                    runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            isAnimating = false;
//                            finishStateChange(previousView);
//                        }
//                    });
//                }
//            });
//        }
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return !isAnimating && super.dispatchTouchEvent(ev); // unfortunately, we must manually make sure you can't navigate while you're animating.
    }

    Fragment previousFragment;

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();

        if(stateChange.getDirection() == StateChange.FORWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        } else if(stateChange.getDirection() == StateChange.BACKWARD) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        }

        for(Object _oldKey : stateChange.getPreviousState()) {
            Key oldKey = (Key) _oldKey;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(oldKey.getFragmentTag());
            if(fragment != null) {
                if(!stateChange.getNewState().contains(oldKey)) {
                    Log.i(TAG, "Old key is NOT in new state: removing [" + oldKey + "]");
                    fragmentTransaction.remove(fragment);
                } else if(!fragment.isDetached()) {
                    Log.i(TAG, "Old key is in new state, but not showing: detaching [" + oldKey + "]");
                    fragmentTransaction.detach(fragment);
                }
            }
        }

        for(Object _newKey : stateChange.getNewState()) {
            Key newKey = (Key) _newKey;
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(newKey.getFragmentTag());
            previousFragment = fragment;
            if(newKey.equals(stateChange.topNewState())) {
                if(fragment != null) {
                    if(fragment.isDetached()) {
                        Log.i(TAG, "New key is top state but detached: reattaching [" + newKey + "]");
                        fragmentTransaction.attach(fragment);
                    } else {
                        Log.i(TAG, "New key is top state but already attached: probably config change for [" + newKey + "]");
                    }
                } else {
                    Log.i(TAG, "New fragment does not exist yet, adding [" + newKey + "]");
                    fragment = newKey.createFragment();
                    previousFragment = fragment;
                    fragmentTransaction.add(R.id.root, fragment, newKey.getFragmentTag());
                }
            } else {
                if(fragment != null && !fragment.isDetached()) {
                    Log.i(TAG, "New fragment is not active fragment. It should be detached: [" + newKey + "]");
                    fragmentTransaction.detach(fragment);
                } else {
                    Log.i(TAG, "New fragment is already detached or doesn't exist, as expected: [" + newKey + "]");
                }
            }
        }
        fragmentTransaction.commitNow();
        completionCallback.stateChangeComplete();

//        int direction = StateChange.REPLACE;
//        if(root.getChildAt(0) != null) {
//            Key previousKey = Backstack.getKey(root.getChildAt(0).getContext());
//            StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
//            StackType newStack = StackType.valueOf(((Key) stateChange.topNewState()).stackIdentifier());
//            direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
//        }
//        exchangeViewForKey((Key) stateChange.topNewState(), direction);
//        completionCallback.stateChangeComplete();
    }
}
