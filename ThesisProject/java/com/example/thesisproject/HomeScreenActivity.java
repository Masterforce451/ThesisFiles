package com.example.thesisproject;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class HomeScreenActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    View navHeaderView;
    TextView header_name;
    BottomNavigationView bottomNavigationView;
    Button logoutButton;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        logoutButton = findViewById(R.id.logoutButton);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            logoutButton.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle(getString(R.string.exit_account))
                    .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(HomeScreenActivity.this, LoginScreenActivity.class);
                        startActivity(intent);
                        HomeScreenActivity.this.finish();
                    }).setNegativeButton(getString(R.string.no), null).setIcon(android.R.drawable.ic_dialog_alert).show());
        } else {
            logoutButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeScreenActivity.this, LoginScreenActivity.class);
                startActivity(intent);
                HomeScreenActivity.this.finish();
            });
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.bringToFront();

        navHeaderView = navigationView.inflateHeaderView(R.layout.navigation_bar_header);
        header_name = navHeaderView.findViewById(R.id.header_title);
        header_name.setTextColor(Color.BLACK);


        if (user != null) {
            header_name.setText(String.format("%s%s", getText(R.string.header_hello), user.getEmail()));
        } else {
            header_name.setText(getText(R.string.header_nologin));
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).setReorderingAllowed(true).commit();
        navigationView.setNavigationItemSelectedListener(item -> {
            unCheckAllMenuItems(navigationView.getMenu());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            int id = item.getItemId();
            if (id == R.id.drawer_favourites){
                transaction.replace(R.id.fragment_container, new FavouritesFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
            }
            else if (id == R.id.drawer_settings) {
                transaction.replace(R.id.fragment_container, new SettingsFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                bottomNavigationView.getMenu().getItem(2).setChecked(true);
            }
            else if (id == R.id.drawer_recomendationWizard) {
                transaction.replace(R.id.fragment_container, new RecommendationWizardFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                bottomNavigationView.getMenu().getItem(4).setChecked(true);
            }
            if (id == R.id.drawer_cpu){
                transaction.replace(R.id.fragment_container, new CPUFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
            }
            else if (id == R.id.drawer_gpu) {
                transaction.replace(R.id.fragment_container, new GPUFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                bottomNavigationView.getMenu().getItem(1).setChecked(true);
            }
            else if (id == R.id.drawer_laptop) {
                transaction.replace(R.id.fragment_container, new LaptopFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
            }
            item.setChecked(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            unCheckAllMenuItems(navigationView.getMenu());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navbar_cpu:
                    transaction.replace(R.id.fragment_container, new CPUFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                    navigationView.getMenu().getItem(2).getSubMenu().getItem(1).setChecked(true);
                    break;
                case R.id.navbar_gpu:
                    transaction.replace(R.id.fragment_container, new GPUFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                    navigationView.getMenu().getItem(2).getSubMenu().getItem(2).setChecked(true);
                    break;
                case R.id.navbar_home:
                    transaction.replace(R.id.fragment_container, new HomeFragment()).setReorderingAllowed(true).commit();
                    break;
                case R.id.navbar_laptop:
                    transaction.replace(R.id.fragment_container, new LaptopFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                    navigationView.getMenu().getItem(2).getSubMenu().getItem(3).setChecked(true);
                    break;
                case R.id.navbar_recommendation:
                    transaction.replace(R.id.fragment_container, new RecommendationWizardFragment()).addToBackStack(null).setReorderingAllowed(true).commit();
                    navigationView.getMenu().getItem(2).getSubMenu().getItem(0).setChecked(true);
                    break;
            }
            return true;
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            new AlertDialog.Builder(this).setTitle(getString(R.string.exit_app)).setPositiveButton(getString(R.string.yes),
                    (dialog, which) -> this.finishAffinity()).setNegativeButton(getString(R.string.no), null).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    private void unCheckAllMenuItems(Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}