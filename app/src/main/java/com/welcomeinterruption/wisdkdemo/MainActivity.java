//  Created by Phillp Frantz on 13/07/2017.
//  Copyright © 2012-2018 3 Electric Sheep Pty Ltd. All rights reserved.
//
//  The Welcome Interruption Software Development Kit (SDK) is licensed to you subject to the terms
//  of the License Agreement. The License Agreement forms a legally binding contract between you and
//  3 Electric Sheep Pty Ltd in relation to your use of the Welcome Interruption SDK.
//  You may not use this file except in compliance with the License Agreement.
//
//  A copy of the License Agreement can be found in the LICENSE file in the root directory of this
//  source tree.
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License
//  Agreement is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
//  express or implied. See the License Agreement for the specific language governing permissions
//  and limitations under the License Agreement.

package com.welcomeinterruption.wisdkdemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.welcomeinterruption.wisdk.TesApi;
import com.welcomeinterruption.wisdk.TesLocationInfo;
import com.welcomeinterruption.wisdk.TesWIApp;
import com.welcomeinterruption.wisdk.TesConfig;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements TesWIApp.TesWIAppListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    static final String PROVIDER_KEY = "5bb54bd58f3f541552dd0097";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TesWIApp app = TesWIApp.createManager(this);
        TesConfig config = new TesConfig(PROVIDER_KEY);

        if (BuildConfig.DEBUG) {
            config.environment = TesConfig.ENV_TEST;
            //config.testServer = "http://10.0.2.2:9010";
        }
        else {
            config.environment = TesConfig.ENV_PROD;
        }

        config.authAutoAuthenticate = true;
        config.deviceTypes = TesConfig.deviceTypeFCM | TesConfig.deviceTypePassive;
        config.fcmSenderId = "955521662890"; // from the firebird console
        try {
            config.authCredentials = new JSONObject();
            config.authCredentials.put("anonymous_user", true);
        }
        catch (JSONException e){
            Log.e(TAG, "Failed to create authentication details: "+e.getLocalizedMessage());
        }

        config.testPushProfile = "wisdk-example-fcm";
        config.pushProfile = "wisdk-example-fcm";

        if (!app.checkPlayServices(this)) {
            Log.i(TAG, "Play service not available or out of date - location monitoring will not work");
        }

        app.listener = this;
        app.start(config);

    }

    @Override
    public void onStartupComplete(boolean isAuthorized) {
        Log.i(TAG, "Startup complete");

        TesWIApp app = TesWIApp.manager();

        JSONObject params = new JSONObject();
        try {
            params.put("first_name", "John");
            params.put("last_name", "Smith");
            params.put("email", "jsmith@3es.com.au");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        app.updateAccountProfile(params, new TesApi.TesApiListener() {
            @Override
            public void onSuccess(JSONObject result) {
                // the call succeeded returns a dictionary with a data field containing the updated user info
                // a  dictionary containing success=1 and a data field with updated profile info.
                JSONObject data = null;
                try {
                    data = result.getJSONObject("data");
                    Log.i(TAG, String.format("--> updateAccountProfile Success %s", data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(JSONObject result) {
                // the call made it to the server but there was a logical failure (ie. invalid data)
                // A dictionary containing success=0, and a field called msg which contains the error string
                String msg = null;
                try {
                    msg = result.getString("msg");
                    Log.i(TAG, String.format("--> updateAccountProfile Fail %s", msg));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOtherError(Exception error) {
                // a network or other transport type error
                Log.i(TAG, String.format("--> updateAccountProfile error: %s", error.getLocalizedMessage()));

            }
        });

        params = new JSONObject();
        try {
            params.put("relative_start", "20d");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        app.listAlertedEvents(params, new TesApi.TesApiListener() {
            @Override
            public void onSuccess(JSONObject result) {
                // the call succeeded returns a dictionary with a data field containing the updated user info
                // a  dictionary containing success=1 and a data field with updated profile info.
                JSONArray data = null;
                try {
                    data = result.getJSONArray("data");
                    Log.i(TAG, String.format("--> listAlertedEvents Success %s", data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(JSONObject result) {
                // the call made it to the server but there was a logical failure (ie. invalid data)
                // A dictionary containing success=0, and a field called msg which contains the error string
                String msg = null;
                try {
                    msg = result.getString("msg");
                    Log.i(TAG, String.format("--> listAlertedEvents Fail %s", msg));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onOtherError(Exception error) {
                // a network or other transport type error
                Log.i(TAG, String.format("--> listAlertedEvents error: %s", error.getLocalizedMessage()));

            }
        });
    }

    @Override
    public boolean onLocationPermissionCheck(String result, boolean just_blocked) {
        if (result == "restricted" && !just_blocked)
            return true;

        return false;
    }

    @Override
    public void onLocationUpdate(TesLocationInfo loc) {
        Log.i(TAG, String.format("--> onLocationUpdate: %s", loc.toString()));
    }

    @Override
    public void onGeoLocationUpdate(TesLocationInfo loc) {
        Log.i(TAG, String.format("--> onGeoLocationUpdate: %s", loc.toString()));
    }

    @Override
    public void authorizeFailure(int statusCode, byte[] data, boolean notModified, long networkTimeMs, Map<String, String> headers) {
        Log.i(TAG, String.format("-->authorizeFailure: %d", statusCode));
    }

    @Override
    public void onAutoAuthenticate(int status, @Nullable JSONObject responseObject, @Nullable Exception error) {
        Log.i(TAG, String.format("--> onAutoAuthenticate: %d %s", status, responseObject.toString()));
    }

    @Override
    public void newAccessToken(@Nullable String token) {
        Log.i(TAG, String.format("--> newAccessToken: %s", token));

    }

    @Override
    public void newDeviceToken(@Nullable String token) {
        Log.i(TAG, String.format("--> newDeviceToken: %s", token));
    }

    @Override
    public void newPushToken(@Nullable String token) {
        Log.i(TAG, String.format("--> newPushToken: %s", token));
    }

    @Override
    public void onRemoteNotification(@Nullable JSONObject data) {
        Log.i(TAG, String.format("--> onRemoteNotification: %s", data.toString()));
    }

    @Override
    public void onRemoteDataNotification(@Nullable JSONObject data) {
        Log.i(TAG, String.format("--> onRemoteDataNotification: %s", data.toString()));
        try {
            TesWIApp app = TesWIApp.manager();
            String event_id = data.getString("event_id");
            app.updateEventAck(event_id, true, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWalletNotification(@Nullable JSONObject data) {
        Log.i(TAG, String.format("--> onWalletNotification: %s", data.toString()));
    }

    @Override
    public void onRefreshToken(@NonNull String token) {
        Log.i(TAG, String.format("--> onRefreshToken: %s", token));
    }

    @Override
    public void saveWallet(int requestCode, int resultCode, Intent data, String msg) {
        Log.i(TAG, String.format("--> saveWallet: %d %d %s Intent: %s", requestCode, resultCode, msg, data.toString()));
    }

    @Override
    public void onError(String errorType, Exception error) {
        Log.i(TAG, String.format("--> onError: %s %s", errorType, error.getMessage()));
    }
}
