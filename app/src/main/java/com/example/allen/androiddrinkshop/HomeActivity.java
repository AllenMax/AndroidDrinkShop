package com.example.allen.androiddrinkshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.allen.androiddrinkshop.Adapter.CategoryAdapter;
import com.example.allen.androiddrinkshop.Database.DataSource.CartRepository;
import com.example.allen.androiddrinkshop.Database.DataSource.FavoriteRepository;
import com.example.allen.androiddrinkshop.Database.Local.CartDataSource;
import com.example.allen.androiddrinkshop.Database.Local.FavoriteDataSource;
import com.example.allen.androiddrinkshop.Database.Local.ProjectRoomDatabase;
import com.example.allen.androiddrinkshop.Model.Banner;
import com.example.allen.androiddrinkshop.Model.Category;
import com.example.allen.androiddrinkshop.Model.CheckUserResponse;
import com.example.allen.androiddrinkshop.Model.Drink;
import com.example.allen.androiddrinkshop.Model.User;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.nex3z.notificationbadge.NotificationBadge;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView txt_name,txt_phone;
    SliderLayout sliderLayout;

    RecyclerView first_menu;

    NotificationBadge badge;
    ImageView cart_icon;

    IDrinkShopAPI mService;

    //Rxjava
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mService = Common.getAPI();

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);

        first_menu = findViewById(R.id.first_menu);
        first_menu.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        first_menu.setHasFixedSize(true);

        sliderLayout = findViewById(R.id.slider);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, ""+Common.currentUser.getPhone(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        txt_name = (TextView)headerView.findViewById(R.id.txt_name);
        txt_phone = (TextView)headerView.findViewById(R.id.txt_phone);


        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get Menu
                getMenu();

                //Save newest Topping List
                getToppingList();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                //Get Menu
                getMenu();

                //Save newest Topping List
                getToppingList();
            }
        });

        //Get Banner
        getBannerImage();

        //Init Database
        initDB();

        //If user already logged, just login again (session sill alive)
        checkSessionLogin();

    }

    private void checkSessionLogin() {
        if(AccountKit.getCurrentAccessToken() != null)
        {
            swipeRefreshLayout.setRefreshing(true);

//            final AlertDialog alertDialog = new SpotsDialog(HomeActivity.this);
//            alertDialog.show();
//            alertDialog.setMessage("Please wait...");

            //Check exists user on Server (MySQL)
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(final Account account) {

                    mService.checkExistsUser(account.getPhoneNumber().toString())
                            .enqueue(new Callback<CheckUserResponse>() {
                                @Override
                                public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                    CheckUserResponse userResponse = response.body();
                                    if (userResponse.isExists()) {

                                        //Fetch Information

                                        mService.getUserInformation(account.getPhoneNumber().toString())
                                                .enqueue(new Callback<User>() {
                                                    @Override
                                                    public void onResponse(Call<User> call, Response<User> response) {

                                                        Common.currentUser = response.body();

                                                        if(Common.currentUser != null){
                                                            swipeRefreshLayout.setRefreshing(false);

                                                            //Check if user has logged in to the app
                                                            if(Common.currentUser != null){
                                                                //Set Info
                                                                txt_name.setText(Common.currentUser.getName());
                                                                txt_phone.setText(Common.currentUser.getPhone());
                                                            }


                                                            Toast.makeText(HomeActivity.this, "Account Exists: True", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        swipeRefreshLayout.setRefreshing(false);
                                                        Toast.makeText(HomeActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    } else {
                                        //Else, Resister User
                                        Toast.makeText(HomeActivity.this, "Account Exists: False \nPhone Num:" + account.getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(HomeActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                                    if (t instanceof IOException) {
                                        Toast.makeText(HomeActivity.this, "Network Failure :\n" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(HomeActivity.this, "Error occured \nPhone: " + account.getPhoneNumber().toString()+"\n"+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        // todo log to some central bug tracking service
                                    }

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("ERROR", accountKitError.getErrorType().getMessage());

                }
            });



        }
        else{
            AccountKit.logOut();

            //Clear all Activity
            Intent intent = new Intent (HomeActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void showRegisterDialog(final String phone) {

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("REGISTER");

        LayoutInflater inflater = this.getLayoutInflater();
        View dialog_register = inflater.inflate(R.layout.dialog_register, null);

        final MaterialEditText edit_name = (MaterialEditText) dialog_register.findViewById(R.id.editTextName);
        final MaterialEditText edit_address = (MaterialEditText) dialog_register.findViewById(R.id.editTextAddress);
        final MaterialEditText edit_bithdate = (MaterialEditText) dialog_register.findViewById(R.id.editTextBirthday);

        Button btn_register = (Button) dialog_register.findViewById(R.id.buttonRegister);

        edit_bithdate.addTextChangedListener(new PatternedTextWatcher("####-##-##"));

        builder.setView(dialog_register);
        final AlertDialog dialog = builder.create();

        //Event
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                if (TextUtils.isEmpty((edit_name.getText().toString()))) {
                    Toast.makeText(HomeActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty((edit_bithdate.getText().toString()))) {
                    Toast.makeText(HomeActivity.this, "Please Enter Your Bithdate", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty((edit_address.getText().toString()))) {
                    Toast.makeText(HomeActivity.this, "Please Enter Your Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please waiting...");

                mService.registerNewUser(phone,
                        edit_name.getText().toString(),
                        edit_bithdate.getText().toString(),
                        edit_address.getText().toString())
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                waitingDialog.dismiss();

                                User user = response.body();

                                if (TextUtils.isEmpty(user.getError_msg())) {
                                    Toast.makeText(HomeActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                    Common.currentUser = response.body();

                                    //Start new Activity
                                    startActivity(new Intent(HomeActivity.this, HomeActivity.class));
                                    finish(); //Close MainActivity
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                waitingDialog.dismiss();

                                if (t instanceof IOException) {
                                    Toast.makeText(HomeActivity.this, "Network Failure :\n" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                                    // logging probably not necessary
                                } else {
                                    Toast.makeText(HomeActivity.this, "Error occured \nPhone: +19023292556", Toast.LENGTH_SHORT).show();
                                    // todo log to some central bug tracking service
                                }
                            }
                        });
            }

        });
        dialog.show();
    }

    private void initDB() {
        Common.projectRoomDatabase = ProjectRoomDatabase.getInstance(this);
        Common.cartRepository = CartRepository.getInstance(CartDataSource.getInstance(Common.projectRoomDatabase.cartDAO()));
        Common.favoriteRepository = FavoriteRepository.getInstance(FavoriteDataSource.getInstance(Common.projectRoomDatabase.favoriteDAO()));
    }

    private void getToppingList() {
        compositeDisposable.add(mService.getDrink(Common.TOPPING_MENU_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Drink>>() {
                    @Override
                    public void accept(List<Drink> drinks) throws Exception {
                        Common.toppingList = drinks;
                    }
                }));
    }

    private void getMenu() {
        compositeDisposable.add(mService.getMenu()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Category>>() {
                    @Override
                    public void accept(List<Category> categories) throws Exception {
                        displayMenuImage(categories);
                    }
                }));
    }

    private void displayMenuImage(List<Category> categories) {
        CategoryAdapter categoryAdapter= new CategoryAdapter(this,categories);
        first_menu.setAdapter(categoryAdapter);

        swipeRefreshLayout.setRefreshing(false);


    }

    private void getBannerImage() {
        compositeDisposable.add(mService.getBanners()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Banner>>() {
            @Override
            public void accept(List<Banner> banners) throws Exception {
                displayBannerImage(banners);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void displayBannerImage(List<Banner> banners) {
        HashMap<String,String> bannerMap = new HashMap<>();

        for (Banner item:banners)
        {
            bannerMap.put(item.getName(),item.getLink());
        }

        for (String name:bannerMap.keySet())
        {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView.description(name)
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            sliderLayout.addSlider(textSliderView);
        }
    }

    //EXIT Application when BACK Button is clicked
    boolean isBackButtonClicked = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(isBackButtonClicked){
                super.onBackPressed();
                return;
            }
            this.isBackButtonClicked = true;
            Toast.makeText(this, "Please Click BACK again to EXIT", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        View view = menu.findItem(R.id.cart_menu).getActionView();
        badge = view.findViewById(R.id.badge);
        cart_icon = view.findViewById(R.id.cart_icon);
        cart_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,CartActivity.class));
            }
        });
        updateCartCount();
        return true;
    }

    private void updateCartCount() {
        if (badge == null) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(Common.cartRepository.countCartItems() == 0){
                    badge.setVisibility(View.INVISIBLE);
                }
                else{
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(Common.cartRepository.countCartItems()));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart_menu) {
            return true;
        }
        else if (id == R.id.search_menu) {
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sign_out) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit Application");
            builder.setMessage("Do you want to exit this application");

            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AccountKit.logOut();

                    //Clear all Activity
                    Intent intent = new Intent (HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            });

            builder.setPositiveButton("Cancle", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }

        else if (id == R.id.nav_favorite){
            if (Common.currentUser != null){
                Intent intent = new Intent(HomeActivity.this, FavoriteListActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Please Login to use this Feature!", Toast.LENGTH_SHORT).show();
            }
        }

        else if (id == R.id.nav_show_orders){
            if (Common.currentUser != null){
                Intent intent = new Intent(HomeActivity.this, OrderActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(this, "Please Login to use this Feature!", Toast.LENGTH_SHORT).show();
            }
        }

        else if (id == R.id.nav_nearby_store){
            Intent intent = new Intent(HomeActivity.this, NearByStoreActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartCount();
        isBackButtonClicked = false;
    }

}
