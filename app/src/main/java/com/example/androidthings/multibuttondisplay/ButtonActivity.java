/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.androidthings.multibuttondisplay;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.contrib.driver.ht16k33.AlphanumericDisplay;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

/**
 * Example of using Button driver for toggling a LED.
 *
 * This activity initialize an InputDriver to emit key events when the button GPIO pin state change
 * and flip the state of the LED GPIO pin.
 *
 * You need to connect an LED and a push button switch to pins specified in {@link BoardDefaults}
 * according to the schematic provided in the sample README.
 */
public class ButtonActivity extends Activity {
    private static final String TAG = ButtonActivity.class.getSimpleName();

    private Gpio mLed1;
    private Gpio mLed2;
    private Gpio mLed3;
    private ButtonInputDriver mButtonInputDriver1;
    private ButtonInputDriver mButtonInputDriver2;
    private ButtonInputDriver mButtonInputDriver3;
    private AlphanumericDisplay mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Starting ButtonActivity");

        PeripheralManagerService pioService = new PeripheralManagerService();
        try {
            Log.i(TAG, "Configuring GPIO pins");
            mLed1 = pioService.openGpio(BoardDefaults.getLedGpioPin1());
            mLed1.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLed2 = pioService.openGpio(BoardDefaults.getLedGpioPin2());
            mLed2.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            mLed3 = pioService.openGpio(BoardDefaults.getLedGpioPin3());
            mLed3.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

            Log.i(TAG, "Registering button driver");

            // Initialize and register the InputDriver that will emit SPACE key events
            // on GPIO state changes.
            mButtonInputDriver1 = new ButtonInputDriver(
                    BoardDefaults.getButtonGpioPin1(),
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_A);
            mButtonInputDriver1.register();
            mButtonInputDriver2 = new ButtonInputDriver(
                    BoardDefaults.getButtonGpioPin2(),
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_B);
            mButtonInputDriver2.register();
            mButtonInputDriver3 = new ButtonInputDriver(
                    BoardDefaults.getButtonGpioPin3(),
                    Button.LogicState.PRESSED_WHEN_LOW,
                    KeyEvent.KEYCODE_C);
            mButtonInputDriver3.register();

        } catch (IOException e) {
            Log.e(TAG, "Error configuring GPIO pins", e);
        }
               try {
            mDisplay = new AlphanumericDisplay(BoardDefaults.getI2cBus());
            mDisplay.setEnabled(true);
            mDisplay.clear();
            Log.d(TAG, "Initialized I2C Display");
        } catch (IOException e) {
            Log.e(TAG, "Error initializing display", e);
            Log.d(TAG, "Display disabled");
            mDisplay = null;
        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_A) {
            // Turn on the LED
            setLedValue1(true);
            updateMessage("K->A");
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_B) {
            // Turn on the LED
            setLedValue2(true);
            updateMessage("K->B");
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_C) {
            // Turn on the LED
            setLedValue3(true);
            updateMessage("K->C");
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_A) {
            // Turn off the LED
            setLedValue1(false);
            updateMessage("");
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_B) {
            // Turn off the LED
            setLedValue2(false);
            updateMessage("");
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_C) {
            // Turn off the LED
            setLedValue3(false);
            updateMessage("");
            return true;
        }


        return super.onKeyUp(keyCode, event);
    }

    /**
     * Update the value of the LED output.
     */
    private void setLedValue1(boolean value) {
        try {
            mLed1.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }

    private void setLedValue2(boolean value) {
        try {
            mLed2.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }
    private void setLedValue3(boolean value) {
        try {
            mLed3.setValue(value);
        } catch (IOException e) {
            Log.e(TAG, "Error updating GPIO value", e);
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (mButtonInputDriver1 != null) {
            mButtonInputDriver1.unregister();
            try {
                mButtonInputDriver1.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Button driver", e);
            } finally{
                mButtonInputDriver1 = null;
            }
        }

        if (mLed1 != null) {
            try {
                mLed1.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally{
                mLed1 = null;
            }
            mLed1 = null;
        }
        if (mLed2 != null) {
            try {
                mLed2.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally{
                mLed2 = null;
            }
            mLed2 = null;
        }

        if (mLed3 != null) {
            try {
                mLed3.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing LED GPIO", e);
            } finally{
                mLed3 = null;
            }
            mLed3 = null;
        }

    }
    private void updateMessage(String message) {
        if (mDisplay != null) {
            try {
                mDisplay.display(message);
            } catch (IOException e) {
                Log.e(TAG, "Error setting display", e);
            }
        }
    }

}
