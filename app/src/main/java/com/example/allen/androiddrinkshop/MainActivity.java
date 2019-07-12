package com.example.allen.androiddrinkshop;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.allen.androiddrinkshop.Model.CheckUserResponse;
import com.example.allen.androiddrinkshop.Model.User;
import com.example.allen.androiddrinkshop.Retrofit.IDrinkShopAPI;
import com.example.allen.androiddrinkshop.Utils.Common;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.szagurskii.patternedtextwatcher.PatternedTextWatcher;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    Button btn_continue;

    IDrinkShopAPI mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mService = Common.getAPI();

        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //checkIfWorking()
                startLoginPage(LoginType.PHONE);
            }
        });

        //Check Session
        if(AccountKit.getCurrentAccessToken() != null)
        {
            final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
            alertDialog.show();
            alertDialog.setMessage("Please waiting ...");

            //Auto login
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

                                                        //If User already exists, just start new Activity
                                                        Toast.makeText(MainActivity.this, "Account Exists: True", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();

                                                        Common.currentUser = response.body();

                                                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                        finish(); //Close MainActivity
                                                    }

                                                    @Override
                                                    public void onFailure(Call<User> call, Throwable t) {
                                                        Toast.makeText(MainActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                    } else {
                                        //Else, Resister User
                                        alertDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Account Exists: False \nPhone Num:" + account.getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                        showRegisterDialog(account.getPhoneNumber().toString());
                                    }
                                }

                                @Override
                                public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                    alertDialog.dismiss();

                                    if (t instanceof IOException) {
                                        Toast.makeText(MainActivity.this, "Network Failure :\n" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Error occured \nPhone: " + account.getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                        // todo log to some central bug tracking service
                                    }

                                }
                            });
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.d("Error", accountKitError.getErrorType().getMessage());

                }
            });
        }

    }

    private void checkIfWorking() {
        mService.checkExistsUser("+19023292556")
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                        CheckUserResponse userResponse = response.body();
                        if (userResponse.isExists()) {
                            //If User already exists, just start new Activity
                            Toast.makeText(MainActivity.this, "Account Exists: True", Toast.LENGTH_SHORT).show();
                        } else {
                            //Else, Resister User
                            Toast.makeText(MainActivity.this, "Account Exists: False", Toast.LENGTH_SHORT).show();

                            showRegisterDialog("+19023292556");
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                        if (t instanceof IOException) {
                            Toast.makeText(MainActivity.this, "" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                            // logging probably not necessary
                        } else {
                            Toast.makeText(MainActivity.this, "Error occured \nPhone: +19023292556", Toast.LENGTH_SHORT).show();
                            // todo log to some central bug tracking service
                        }

                    }
                });
    }

    private void startLoginPage(LoginType loginType) {
        Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder builder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(loginType,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION, builder.build());
        startActivityForResult(intent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);

            if (result.getError() != null) {
                Toast.makeText(this, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
            } else if (result.wasCancelled()) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            } else {
                if (result.getAccessToken() != null) {
                    final AlertDialog alertDialog = new SpotsDialog(MainActivity.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please waiting ...");

                    //Get User phone and Check exists on server
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

                                                                //If User already exists, just start new Activity
                                                                //Toast.makeText(MainActivity.this, "Account Exists: True", Toast.LENGTH_SHORT).show();
                                                                alertDialog.dismiss();

                                                                Common.currentUser = response.body();

                                                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                                                finish(); //Close MainActivity

                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                Toast.makeText(MainActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();

                                                            }
                                                        });


                                            } else {
                                                //Else, Resister User
                                                alertDialog.dismiss();
                                                Toast.makeText(MainActivity.this, "Account Exists: False \nPhone Num:" + account.getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                                showRegisterDialog(account.getPhoneNumber().toString());
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                            alertDialog.dismiss();

                                            if (t instanceof IOException) {
                                                Toast.makeText(MainActivity.this, "Network Failure :\n" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                                //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Error occured \nPhone: " + account.getPhoneNumber().toString(), Toast.LENGTH_SHORT).show();
                                                // todo log to some central bug tracking service
                                            }

                                        }
                                    });
                        }

                        @Override
                        public void onError(AccountKitError accountKitError) {
                            Log.d("Error", accountKitError.getErrorType().getMessage());

                        }
                    });
                }
            }
        }
    }

    private void showRegisterDialog(final String phone) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                    Toast.makeText(MainActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty((edit_bithdate.getText().toString()))) {
                    Toast.makeText(MainActivity.this, "Please Enter Your Bithdate", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty((edit_address.getText().toString()))) {
                    Toast.makeText(MainActivity.this, "Please Enter Your Address", Toast.LENGTH_SHORT).show();
                    return;
                }

                final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
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
                                    Toast.makeText(MainActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                                    Common.currentUser = response.body();

                                    //Start new Activity
                                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                    finish(); //Close MainActivity
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                waitingDialog.dismiss();

                                if (t instanceof IOException) {
                                    Toast.makeText(MainActivity.this, "Network Failure :\n" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(MainActivity.this, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
                                    // logging probably not necessary
                                } else {
                                    Toast.makeText(MainActivity.this, "Error occured \nPhone: +19023292556", Toast.LENGTH_SHORT).show();
                                    // todo log to some central bug tracking service
                                }
                            }
                        });
            }

        });
        dialog.show();
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.allen.androiddrinkshop",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KEYHASH", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    //EXIT Application when BACK Button is clicked
    boolean isBackButtonClicked = false;

    @Override
    public void onBackPressed() {
        if(isBackButtonClicked){
            super.onBackPressed();
            return;
        }
        this.isBackButtonClicked = true;
        Toast.makeText(this, "Please Click BACK again to EXIT", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        isBackButtonClicked = false;
        super.onResume();
    }
}
