package org.versebyverseministry.vbvmi;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import org.versebyverseministry.vbvmi.api.APIManager;

public class MainActivity extends AppCompatActivity implements StudiesView.OnFragmentInteractionListener, AnswersFragment.OnFragmentInteractionListener {

    private Fragment _selectedFragment = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:

                    selectedFragment = StudiesView.newInstance("one", "two");
                    break;
                case R.id.navigation_dashboard:
                    selectedFragment = AnswersFragment.newInstance("wef", "tg");
                    break;
                case R.id.navigation_notifications:
                    return false;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if(_selectedFragment != null) {
                transaction.remove(_selectedFragment);
            }
            transaction.add(R.id.content, selectedFragment);
            transaction.commit();
            _selectedFragment = selectedFragment;
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        _selectedFragment = StudiesView.newInstance("ah", "hah");
        transaction.add(R.id.content, _selectedFragment);
        transaction.commit();

        APIManager.getInstance().downloadStudies();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
