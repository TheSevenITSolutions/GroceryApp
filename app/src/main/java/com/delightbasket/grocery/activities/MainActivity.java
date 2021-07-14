package com.delightbasket.grocery.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.databinding.ActivityMainBinding;
import com.delightbasket.grocery.fragments.CartFragment;
import com.delightbasket.grocery.fragments.ComplainFragment;
import com.delightbasket.grocery.fragments.FAQsFragment;
import com.delightbasket.grocery.fragments.HomeFragment;
import com.delightbasket.grocery.fragments.NotificationFragment;
import com.delightbasket.grocery.fragments.ProfileFragment;
import com.delightbasket.grocery.fragments.RatingFragment;
import com.delightbasket.grocery.fragments.WishlistFragment;
import com.delightbasket.grocery.fragments.subscribedproduct.SubscribedProductFragment;
import com.delightbasket.grocery.model.User;
import com.delightbasket.grocery.retrofit.Const;

import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
import static android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    public ObservableInt pos = new ObservableInt(0);
    ObservableInt lastPos = new ObservableInt(0);
    private String title;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.clearFlags(FLAG_TRANSLUCENT_STATUS);
        window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initView();


        Intent intent = getIntent();
        if(intent != null) {
            String str = intent.getStringExtra("work");
            Log.d("TAG", "onCreate:intent " + str);
            if(str != null) {
                Log.d("TAG", "onCreate:notnull " + str);
                if(str.equals("gotocart")) {
                    Log.d("TAG", "onCreate:yes " + str);
                    loadFragment(new CartFragment());
                    binding.tvNavtitle.setText("Cart");
                    pos.set(2);
                }
            }
        }


        initListener();
        initUser();
        binding.drawerLayout.setViewScale(GravityCompat.START, 0.9f); //set height scale for main view (0f to 1f)
        binding.drawerLayout.setViewElevation(GravityCompat.START, 30); //set main view elevation when drawer open (dimension)
        binding.drawerLayout.setViewScrimColor(GravityCompat.START, ContextCompat.getColor(this, R.color.colorPrimary)); //set drawer overlay coloe (color)
        binding.drawerLayout.setDrawerElevation(30); //set drawer elevation (dimension)
        //  binding.drawerLayout.setContrastThreshold(0); //set maximum of contrast ratio between white text and background color.
        binding.drawerLayout.setRadius(GravityCompat.START, 25);
        binding.drawerLayout.setViewRotation(GravityCompat.START, 0);
    }

    private void initUser() {
        if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
            User user = sessionManager.getUser();
            binding.navToolbar.drawerTvUname.setText(user.getData().getFullname());
            binding.navToolbar.drawerTvUmobile.setText(user.getData().getEmail() );
//            Log.d("TAG", "initUser: " + user.getData().getProfileImage());


            Bitmap placeholder = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), placeholder);
            circularBitmapDrawable.setCircular(true);

//            Glide.with(this)
//                    .load(Const.BASE_IMG_URL + user.getData().getProfileImage())
//                    .circleCrop()
//                    //  .placeholder(circularBitmapDrawable)
//                    .into(binding.navToolbar.drawerImgProfile);

        }
    }

    private void initView() {
        sessionManager = new SessionManager(this);
        HomeFragment fragment = new HomeFragment();
        binding.tvNavtitle.setText("Home");
        pos.set(1);
        closeDrawer();
        loadFragment(fragment);
    }

    private void initListener() {

        binding.appbarImgsearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        binding.appbarImgcart.setOnClickListener(v -> {
            pos.set(2);
            closeDrawer();
            loadFragment(new CartFragment());
            setTitleText("Cart");

        });


        binding.navToolbar.navHome.setOnClickListener(view -> {
            pos.set(1);
            closeDrawer();
        });
        binding.navToolbar.navCart.setOnClickListener(view -> {
            pos.set(2);
            closeDrawer();
        });
        binding.navToolbar.navProfile.setOnClickListener(view -> {
            if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(3);
                closeDrawer();
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        binding.navToolbar.navNotification.setOnClickListener(view -> {
            if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(4);
                closeDrawer();
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        binding.navToolbar.navRating.setOnClickListener(view -> {
            if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(5);
                closeDrawer();
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        binding.navToolbar.navWishlist.setOnClickListener(view -> {
            if(sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(6);
                closeDrawer();
            }else{
                startActivity(new Intent(this, LoginActivity.class));
            }

        });
        binding.navToolbar.navComplaint.setOnClickListener(view -> {
            if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(7);
                closeDrawer();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }

        });
        binding.navToolbar.navFaqs.setOnClickListener(view -> {
            pos.set(8);
            closeDrawer();
        });
        binding.navToolbar.navSubscribed.setOnClickListener(view -> {
            if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(9);
                closeDrawer();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
        binding.navToolbar.navSubscribed.setOnClickListener(view -> {
            if (sessionManager.getBooleanValue(Const.IS_LOGIN)) {
                pos.set(10);
                closeDrawer();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        binding.appbarImgmenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(Gravity.LEFT, true));
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
//ll
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.appbarImgmenu.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_sharp_arrow_back_24));

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.appbarImgmenu.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_sharp_menu_24));

                setDefultUI();
                Fragment fragment = null;
                switch(pos.get()) {
                    case 1:
                        fragment = new HomeFragment();
                        title = "Home";
                        binding.appbarImgsearch.setVisibility(View.VISIBLE);
                        binding.appbarImgcart.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        fragment = new CartFragment();
                        title = "Cart";

                        break;
                    case 3:
                        fragment = new ProfileFragment();
                        title = "Profile";
                        break;
                    case 4:
                        fragment = new NotificationFragment();
                        title = "Notification";
                        break;
                    case 5:
                        fragment = new RatingFragment();
                        title = "Rating & Reviews";
                        break;
                    case 6:
                        fragment = new WishlistFragment();
                        binding.appbarImgcart.setVisibility(View.VISIBLE);
                        title = "My Wishlist";
                        break;
                    case 7:
                        fragment = new ComplainFragment();
                        title = "Complains";
                        break;
                    case 8:
                        fragment = new FAQsFragment();
                        title = "FAQs";
                        break;
                    case 9:
                        fragment = new SubscribedProductFragment();
                        title = "Subscription";
                        break;
                    case 10:
                        fragment = new SubscribedProductFragment();
                        title = "Subscribed";
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + pos.get());
                }
                if(pos.get() != lastPos.get()) {
                    loadFragment(fragment);
                    binding.tvNavtitle.setText(title);
                }
                binding.appbar.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
//ll
            }
        });
    }

    public void setTitleText(String title) {
        this.title = title;
        binding.tvNavtitle.setText(title);
    }

    private void setDefultUI() {
        binding.appbarImgsearch.setVisibility(View.GONE);
        binding.appbarImgcart.setVisibility(View.GONE);
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.frame, fragment).commit();

        lastPos.set(pos.get());
    }

    public void closeDrawer() {
        binding.drawerLayout.closeDrawer(Gravity.LEFT);
        if(pos.get() != lastPos.get()) {
            boldText();
            unBoldText();
        }
    }

    private void boldText() {
        Typeface face = ResourcesCompat.getFont(this, R.font.gilroyextrabold);

        switch(pos.get()) {
            case 1:
                binding.navToolbar.tvHome.setTypeface(face);
                break;
            case 2:
                binding.navToolbar.tvCart.setTypeface(face);
                break;
            case 3:
                binding.navToolbar.tvProfile.setTypeface(face);
                break;
            case 4:
                binding.navToolbar.tvNotofication.setTypeface(face);
                break;
            case 5:
                binding.navToolbar.tvRating.setTypeface(face);
                break;
            case 6:
                binding.navToolbar.tvWishlist.setTypeface(face);
                break;
            case 7:
                binding.navToolbar.tvComplain.setTypeface(face);
                break;
            case 8:
                binding.navToolbar.tvFaqs.setTypeface(face);
                break;
            case 9:
            case 10:
                binding.navToolbar.tvSubscribed.setTypeface(face);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + pos.get());
        }

    }

    private void unBoldText() {
        Typeface face = ResourcesCompat.getFont(this, R.font.gilroymedium);

        switch(lastPos.get()) {
            case 1:
                binding.navToolbar.tvHome.setTypeface(face);
                break;
            case 2:
                binding.navToolbar.tvCart.setTypeface(face);
                break;
            case 3:
                binding.navToolbar.tvProfile.setTypeface(face);
                break;
            case 4:
                binding.navToolbar.tvNotofication.setTypeface(face);
                break;
            case 5:
                binding.navToolbar.tvRating.setTypeface(face);
                break;
            case 6:
                binding.navToolbar.tvWishlist.setTypeface(face);
                break;
            case 7:
                binding.navToolbar.tvComplain.setTypeface(face);
                break;
            case 8:
                binding.navToolbar.tvFaqs.setTypeface(face);
                break;
            case 9:
            case 10:
                binding.navToolbar.tvSubscribed.setTypeface(face);
                break;
            default:

        }

    }

    @Override
    public void onBackPressed() {
        if(pos.get() == 1) {
            super.onBackPressed();
            finish();
        } else {
            initView();
            initListener();
            binding.appbarImgsearch.setVisibility(View.VISIBLE);
            binding.appbarImgcart.setVisibility(View.VISIBLE);
        }

    }
}