package com.delightbasket.grocery.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.delightbasket.grocery.R;
import com.delightbasket.grocery.SessionManager;
import com.delightbasket.grocery.adapters.AreaAdapter;
import com.delightbasket.grocery.adapters.CityAdapter;
import com.delightbasket.grocery.adapters.PincodeAdapter;
import com.delightbasket.grocery.databinding.ActivityAddDeliveryAddressBinding;
import com.delightbasket.grocery.databinding.BottomSheetAddressBinding;
import com.delightbasket.grocery.model.Address;
import com.delightbasket.grocery.model.Area;
import com.delightbasket.grocery.model.CityRoot;
import com.delightbasket.grocery.model.Pincode;
import com.delightbasket.grocery.model.RestResponse;
import com.delightbasket.grocery.retrofit.Const;
import com.delightbasket.grocery.retrofit.RetrofitBuilder;
import com.delightbasket.grocery.retrofit.RetrofitService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddDeliveryAddressActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE = 101;
    private static final String ADDRESS_STR = "address";
    String user_id = "";
    ActivityAddDeliveryAddressBinding binding;
    String fName;
    String lName;
    String mobile;
    String mobile2;
    String houseno;
    String street;
    String landmark;
    String city;
    String aera;
    String soc;
    String pincode;
    SessionManager sessionManager;
    Context contextl;
    RetrofitService service;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private String token;
    private List<CityRoot.Datum> cites;
    private BottomSheetDialog bottomSheetDialog;
    private Long selectedCityId;
    private int selectedPincodeId;
    private boolean isCitySelected = false;
    private List<Area.Datum> areas;
    private List<Pincode.Data> pincodes;
    boolean isAreaSelected = false;
    boolean isPincodeSelected = false;
    private LatLng mylatlang;
    private boolean isUpdet = false;
    private String deleveryAddressId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_delivery_address);
        contextl = this;
        binding.rHome.setChecked(true);
        sessionManager = new SessionManager(contextl);
        token = sessionManager.getUser().getData().getToken();
        user_id = sessionManager.getUser().getData().getUserId();
        service = RetrofitBuilder.create(this);
        initListnear();
        redioListnear();

        Intent intent = getIntent();
        if(intent != null && intent.getStringExtra(ADDRESS_STR) != null && !intent.getStringExtra(ADDRESS_STR).equals("")) {
            Address.Datum address = new Gson().fromJson(intent.getStringExtra(ADDRESS_STR), Address.Datum.class);
            if(address != null) {
                isUpdet = true;
                binding.tvAddAddress.setText("Update");
                setAddress(address);
            }

        }

    }

    private void setAddress(Address.Datum address) {
        deleveryAddressId = address.getDeliveryAddressId();
        binding.etFName.setText(address.getFirstName());
        binding.etLName.setText(address.getLastName());
        binding.etMobile.setText(address.getMobileNumber());
        binding.etMobile2.setText(String.valueOf((address.getAltMobileNumber()) != null ? address.getAltMobileNumber() : ""));
        binding.etHouseNo.setText(address.getHomeNo());
        binding.etStreet.setText(address.getStreet());
        binding.etLandmark.setText(address.getLandmark());
        binding.etCity.setText(address.getCity());
        binding.etAera.setText(address.getArea());
        binding.etSocity.setText(address.getSociety());
        binding.etPincode.setText(address.getPincode());

        binding.chkAddressDefult.setChecked(address.getIsDefault() == 1);

        if (address.getAddressType() == 1) {
            binding.rHome.setChecked(true);
        } else if (address.getAddressType() == 2) {
            binding.rOffice.setChecked(true);
        } else if (address.getAddressType() == 3) {
            binding.rOther.setChecked(true);
        }

        if (address.getLatitude() != null && address.getLongitude() != null) {
            mylatlang = new LatLng(Double.parseDouble(address.getLatitude()), Double.parseDouble(address.getLongitude()));
        }

        binding.tvAddAddress.setOnClickListener(v -> addNewAddress());
    }

    private void redioListnear() {
        binding.rHome.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(binding.rHome.isChecked()) {
                binding.rOffice.setChecked(false);
                binding.rOther.setChecked(false);
            }
        });
        binding.rOffice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(binding.rOffice.isChecked()) {
                binding.rHome.setChecked(false);
                binding.rOther.setChecked(false);
            }

        });
        binding.rOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(binding.rOther.isChecked()) {
                binding.rOffice.setChecked(false);
                binding.rHome.setChecked(false);
            }

        });

    }

    private void initListnear() {
        initTextCheker();
        binding.tvAddAddress.setOnClickListener(v -> addNewAddress());
        binding.etCity.setOnClickListener(v -> showCity());
        binding.etAera.setOnClickListener(v -> {
            if(isPincodeSelected) {
                showArea();
            } else {
                Toast.makeText(contextl, "Please Select Pincode First", Toast.LENGTH_SHORT).show();
            }
        });

        binding.etPincode.setOnClickListener(v -> {
            if(isCitySelected) {
                showPincode();
            } else {
                Toast.makeText(contextl, "Please Select City First", Toast.LENGTH_SHORT).show();
            }
        });
        binding.lytLocation.setOnClickListener(v -> {
            binding.rvMap.setVisibility(View.VISIBLE);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(contextl);
            fetchLocation();

        });
        binding.btnDone.setOnClickListener(v -> {
            if(mylatlang != null) {
                binding.rvMap.setVisibility(View.GONE);
                binding.imglocationDone.setVisibility(View.VISIBLE);
                binding.tvLocation.setText("Location Fetched");
            } else {
                fetchLocation();
            }
        });
    }

    private void fetchLocation() {
        if(ContextCompat.checkSelfPermission(
                contextl, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                contextl, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location != null) {
                currentLocation = location;
                Toast.makeText(contextl, "Location Fetched", Toast.LENGTH_SHORT).show();
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.myMap);

                if(supportMapFragment != null) {
                    supportMapFragment.getMapAsync(this);

                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        }
    }

    private void showArea() {
        binding.pd.setVisibility(View.VISIBLE);
        Call<Area> call = service.getArea(Const.DEV_KEY, String.valueOf(selectedCityId));
        call.enqueue(new Callback<Area>() {
            @Override
            public void onResponse(Call<Area> call, Response<Area> response) {
                if(response.code() == 200 && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                    areas = response.body().getData();
                    openBottomSheet(null, areas, null,2);
                }else{
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(AddDeliveryAddressActivity.this, "No Areas found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Area> call, Throwable t) {
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(AddDeliveryAddressActivity.this, "No Areas found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCity() {
        binding.pd.setVisibility(View.VISIBLE);
        Call<CityRoot> call = service.getCity(Const.DEV_KEY);
        call.enqueue(new Callback<CityRoot>() {
            @Override
            public void onResponse(Call<CityRoot> call, Response<CityRoot> response) {
                if(response.code() == 200 && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                    cites = response.body().getData();
                    openBottomSheet(cites, null, null,1);
                }else{
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(AddDeliveryAddressActivity.this, "No Cities found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CityRoot> call, Throwable t) {
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(AddDeliveryAddressActivity.this, "No Cities found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPincode() {
        binding.pd.setVisibility(View.VISIBLE);
        Call<Pincode> call = service.getPincode(Const.DEV_KEY, String.valueOf(selectedCityId));
        call.enqueue(new Callback<Pincode>() {
            @Override
            public void onResponse(Call<Pincode> call, Response<Pincode> response) {
                if(response.code() == 200 && response.body().getStatus() == 200 && !response.body().getData().isEmpty()) {
                    pincodes = response.body().getData();
                    openBottomSheet(null, null, pincodes, 3);
                }else{
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(AddDeliveryAddressActivity.this, "No Pincodes found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Pincode> call, Throwable t) {
                    binding.pd.setVisibility(View.GONE);
                    Toast.makeText(AddDeliveryAddressActivity.this, "No Pincodes found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openBottomSheet(List<CityRoot.Datum> cites, List<Area.Datum> aera, List<Pincode.Data> pincode,int i) {
        binding.pd.setVisibility(View.GONE);

        if(i == 1) {
            if(contextl != null) {
                bottomSheetDialog = new BottomSheetDialog(contextl);
                BottomSheetAddressBinding addressBinding = DataBindingUtil.inflate(LayoutInflater.from(contextl), R.layout.bottom_sheet_address, null, false);
                bottomSheetDialog.setContentView(addressBinding.getRoot());
                CityAdapter cityAdapter = new CityAdapter(cites, city1 -> {
                    selectedCityId = city1.getId();
                    isCitySelected = true;
                    binding.etCity.setText(city1.getCityName());
                    bottomSheetDialog.dismiss();
                });
                addressBinding.rvNames.setAdapter(cityAdapter);
                addressBinding.tvCity.setText("Select City");

                bottomSheetDialog.show();
                addressBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());
                addressBinding.etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        //ll
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List<CityRoot.Datum> newCities = new ArrayList<>();
                        for(CityRoot.Datum city1 : cites) {
                            if(city1.getCityName() != null && (city1.getCityName().contains(s.toString().toUpperCase()) || city1.getCityName().contains(s.toString().toLowerCase()))) {

                                newCities.add(city1);
                            }
                            //something here
                        }
                        if(!newCities.isEmpty()) {

                            CityAdapter cityAdapter = new CityAdapter(newCities, city1 -> {
                                selectedCityId = city1.getId();
                                isCitySelected = true;
                                binding.etCity.setText(city1.getCityName());
                                bottomSheetDialog.dismiss();
                            });
                            addressBinding.rvNames.setAdapter(cityAdapter);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //ll
                    }
                });
            }
        } else if(i == 2) {
            bottomSheetDialog = new BottomSheetDialog(contextl);
            BottomSheetAddressBinding addressBinding = DataBindingUtil.inflate(LayoutInflater.from(contextl), R.layout.bottom_sheet_address, null, false);
            bottomSheetDialog.setContentView(addressBinding.getRoot());
            AreaAdapter areaAdapter = new AreaAdapter(aera, area1 -> {
                isAreaSelected = true;
                binding.etAera.setText(area1.getAreaName());
                bottomSheetDialog.dismiss();
            });
            addressBinding.rvNames.setAdapter(areaAdapter);
            addressBinding.tvCity.setText("Select Area");

            bottomSheetDialog.show();
            addressBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());
            addressBinding.etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //ll
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    List<Area.Datum> newArea = new ArrayList<>();
                    for(Area.Datum a : aera) {
                        if(a.getAreaName() != null && (a.getAreaName().contains(s.toString().toUpperCase()) || a.getAreaName().contains(s.toString().toLowerCase()))) {


                            newArea.add(a);
                        }
                        //something here
                    }

                    if(!newArea.isEmpty()) {

                        AreaAdapter areaAdapter = new AreaAdapter(newArea, area1 -> {
                            isAreaSelected = true;
                            binding.etAera.setText(area1.getAreaName());
                            bottomSheetDialog.dismiss();
                        });
                        addressBinding.rvNames.setAdapter(areaAdapter);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //ll
                }
            });
        }else if (i == 3){
            bottomSheetDialog = new BottomSheetDialog(contextl);
            BottomSheetAddressBinding addressBinding = DataBindingUtil.inflate(LayoutInflater.from(contextl), R.layout.bottom_sheet_address, null, false);
            bottomSheetDialog.setContentView(addressBinding.getRoot());
            PincodeAdapter pincodeAdapter = new PincodeAdapter(pincode, area1 -> {
                selectedPincodeId = area1.getId();
                isPincodeSelected = true;
                binding.etPincode.setText(String.valueOf(area1.getPincode()));
                bottomSheetDialog.dismiss();
            });

            addressBinding.rvNames.setAdapter(pincodeAdapter);
            addressBinding.tvCity.setText("Select Pincode");

            bottomSheetDialog.show();
            addressBinding.tvCancel.setOnClickListener(view -> bottomSheetDialog.dismiss());
            addressBinding.etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //ll
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    List<Pincode.Data> newArea = new ArrayList<>();
                    for(Pincode.Data a : pincode) {
                        String.valueOf(a.getPincode());
                        if(String.valueOf(a.getPincode()).contains(s.toString().toUpperCase()) || String.valueOf(a.getPincode()).contains(s.toString().toLowerCase())) {
                            newArea.add(a);
                        }
                        //something here
                    }

                    if(!newArea.isEmpty()) {

                        PincodeAdapter areaAdapter = new PincodeAdapter(newArea, area1 -> {
                            selectedPincodeId = area1.getId();
                            isPincodeSelected = true;
                            binding.etPincode.setText(String.valueOf(area1.getPincode()));
                            bottomSheetDialog.dismiss();
                        });
                        addressBinding.rvNames.setAdapter(areaAdapter);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    //ll
                }
            });
        }
    }

    private void addNewAddress() {
        fName = binding.etFName.getText().toString();
        lName = binding.etLName.getText().toString();
        mobile = binding.etMobile.getText().toString();
        mobile2 = binding.etMobile2.getText().toString();
        houseno = binding.etHouseNo.getText().toString();
        street = binding.etStreet.getText().toString();
        landmark = binding.etLandmark.getText().toString();
        city = binding.etCity.getText().toString();
        aera = binding.etAera.getText().toString();
        soc = binding.etSocity.getText().toString();
        pincode = binding.etPincode.getText().toString();

        if(fName.equals("")) {
            binding.etFName.setError("Please enter your first name");
            return;
        }
        if(lName.equals("")) {
            binding.etLName.setError("Please enter your last name");
            return;
        }
        if(mobile.equals("")) {
            binding.etMobile.setError("Please enter your mobile ");
            return;
        }

        if(houseno.equals("")) {
            binding.etHouseNo.setError("Please enter your house or apartment name ");
            return;
        }
        if(street.equals("")) {
            binding.etStreet.setError("Please enter your street ");
            return;
        }
        if(landmark.equals("")) {
            binding.etLandmark.setError("Please enter landmark ");
            return;
        }
        if(city.equals("City*")) {
            binding.etCity.setError("Please select city ");
            return;
        }
        if(aera.equals("Aera*")) {
            binding.etAera.setError("Please select aera ");
            return;
        }
        if(soc.equals("")) {
            binding.etSocity.setError("Please enter socity ");
            return;
        }
        if(pincode.equals("")) {
            binding.etPincode.setError("Please enter pincode ");
            return;
        }
//        if(mylatlang == null) {
//            Toast.makeText(contextl, "Please fetch location first", Toast.LENGTH_SHORT).show();
//
//            return;
//        }
        int i = getAddressType();
        int isDefult = chkDefult();


        HashMap<String, String> map = new HashMap<>();
        map.put("first_name", fName);
        map.put("last_name", lName);
        map.put("mobile_no", mobile);
        if(!mobile2.equals("")) {
            map.put("alt_mobile_no", mobile2);
        }
        map.put("home_no", houseno);
        map.put("street", street);
        map.put("landmark", landmark);
        map.put("city", city);
        map.put("area", aera);
        map.put("society", soc);
        map.put("pincode", pincode);
        map.put("address_type", String.valueOf(i));
        map.put("is_default", String.valueOf(isDefult));
        map.put("latitude", String.valueOf(1561));
        map.put("longitude", String.valueOf(6152));

        binding.pd.setVisibility(View.VISIBLE);
        if(isUpdet) {

            map.put("delivery_address_id", deleveryAddressId);

            Call<RestResponse> call = service.updateAddress(Const.DEV_KEY, token, map, user_id);
            call.enqueue(new Callback<RestResponse>() {
                @Override
                public void onResponse(Call<RestResponse> call, Response<RestResponse> response) {
                    if(response.code() == 200) {
                        if(response.body().getStatus() == 200) {

                            Toast.makeText(AddDeliveryAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddDeliveryAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    binding.pd.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<RestResponse> call, Throwable t) {
                    binding.pd.setVisibility(View.GONE);
                }
            });
        } else {
            Call<Address> call = service.addAddress(Const.DEV_KEY, token, map, user_id);
            call.enqueue(new Callback<Address>() {
                @Override
                public void onResponse(Call<Address> call, Response<Address> response) {
                    if(response.code() == 200) {
                        if(response.body().getStatus() == 200) {
                            Toast.makeText(contextl, response.message(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AddDeliveryAddressActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                    binding.pd.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<Address> call, Throwable t) {
                    binding.pd.setVisibility(View.GONE);
                }
            });

        }


    }

    private void initTextCheker() {
        binding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && s.toString().length() < 10) {
                    binding.etMobile.setError("Should be 10 digit");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });
        binding.etMobile2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals("") && s.toString().length() < 10) {
                    binding.etMobile2.setError("Should be 10 digit");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
//ll
            }
        });
    }

    private int chkDefult() {
        if(binding.chkAddressDefult.isChecked()) {
            return 1;
        } else {
            return 0;
        }

    }

    private int getAddressType() {
        if(binding.rHome.isChecked()) {
            return 1;
        } else if(binding.rOffice.isChecked()) return 2;
        else if(binding.rOther.isChecked()) {
            return 3;
        }
        return 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
        mylatlang = latLng;
        googleMap.setOnMapClickListener(latLng1 -> {
            googleMap.clear();
            MarkerOptions options = new MarkerOptions();
            options.position(latLng1);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.addMarker(options);
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng1));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 14));
            mylatlang = latLng1;
        });
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}