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

import org.json.JSONException;
import org.json.JSONObject;

import com.welcomeinterruption.wisdk.TesApiException;
import com.welcomeinterruption.wisdk.TesWIApp;
import com.welcomeinterruption.wisdk.TesConfig;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements TesWIApp.TesWIAppListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PROVIDER_KEY = "5b53e675ec8d831eb30242d3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TesWIApp.createManager(this, this, R.layout.activity_main);
        TesConfig config = new TesConfig(PROVIDER_KEY);

        config.authAutoAuthenticate = true;
        config.deviceTypes = TesConfig.deviceTypeFCM;
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

        TesWIApp app = TesWIApp.manager();
        app.listener = this;
        app.start(config);
    }


    @Override
    public void authorizeFailure(int statusCode, byte[] data, boolean notModified, long networkTimeMs, Map<String, String> headers) {
        Log.i(TAG, String.format("-->authorizeFailure: %d", statusCode));
    }

    @Override
    public void onAutoAuthenticate(int status, @Nullable JSONObject responseObject, @Nullable TesApiException error) {
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
        try {
            TesWIApp app = TesWIApp.manager();
            String event_id = data.getString("event_id");
            app.updateEventAck(event_id, true, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
}
