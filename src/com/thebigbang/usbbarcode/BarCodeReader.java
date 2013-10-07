/*This file is part of USBBarCodeReader.
*
* USBBarCodeReader is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.
*
* USBBarCodeReader is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Foobar. If not, see <http://www.gnu.org/licenses/>.
*
* Copyright (c) Meï-Garino Jérémy
*/
package com.thebigbang.usbbarcode;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * The event handling interface about the final BarCode decoded value.
 *
 * @author thebigbang
 */
interface BarCodeListener {

    void computed(String result);
}


/**
 * usage: override {@link Activity#onKeyUp(int, android.view.KeyEvent) } and
 * call {@link #hook(int, android.view.KeyEvent)} in it.
 * <br/>
 * It will automatically handle virtual key press and physical one (at the
 * moment any physical keyboard is a Barcode reader for us... next version will
 * add deviceId recognition or something equivalent).
 * <br/>
 * usage Example:
 * <pre>
 * public class Main extends Activity {
 *    ...
 *    public void onCreate(Bundle savedInstanceState) {
 *        ...
 *        BarCodeReader.addBarCodeListener(new BarCodeListener() {
 *            public void computed(String result) {
 *                v.append("\n\r"+result);
 *            }
 *        });
 *    }
 *    public boolean onKeyUp(int keyCode, KeyEvent event) {
 *        super.onKeyUp(keyCode, event);
 *        BarCodeReader.hook(keyCode, event);
 *        ...
 *    }
 * }
 * </pre>
 */
public class BarCodeReader {
    private static final String TAG = "BarCodeReader";

    private static List<Integer> sequence;
    private static List<BarCodeListener> listeners;

    /**
     * Add a listener for the BarCode decoded final value event.
     *
     * @param listener
     */
    public static void addBarCodeListener(BarCodeListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<BarCodeListener>();
        }
        listeners.add(listener);
    }

    /**
     * Remove a listener from the Barcode decoded final value event.
     *
     * @param listener
     */
    public static void removeBarCodeListener(BarCodeListener listener) {
        if (listener != null && !listeners.isEmpty()) {
            listeners.remove(listener);
        }
    }

    /**
     * Method to call on {@link Activity#onKeyUp(int, android.view.KeyEvent) },
     * will convert keyCode into readable value only if the input came from a
     * BarCode Reader.
     * <br/> Will call the event {@link BarCodeListener} on complete, returning
     * the final value as String.
     * <br/>
     * If API is >=16 will know if the event wad triggered by a physical or virtual keyboard. Otherwise will try to manage from a deviceId known list.
     *
     * @param keyCode
     * @param event
     */
    public static void hook(int keyCode, KeyEvent event) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (event.getDevice().isVirtual()) {
                return;
            }
        }
        //if 0 then it is virtual...
        else if (event.getDeviceId() == 0) {
            return;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            Log.d(TAG, "will decrypt keyCodes and will try find the barCode");
            compute();
            return;
        }
        /*String s = "keyCode=" + keyCode + "\n\r" + " char=" + event.getCharacters() + "\n\r";
         Log.d("barCodeDmp", s);
         v.append(s);*/
        if (sequence == null) {
            sequence = new ArrayList<Integer>();
        }
        sequence.add(keyCode);
    }

    /**
     * Will decode the final sequences, should be call by "enter" button as
     * final value send by the BarCode reader.
     */
    private static void compute() {

        String build = "";
        for (int i = 0; i < sequence.size(); i = i + 3) {
            String temp = sequence.get(i) + "" + sequence.get(i + 1) + "" + sequence.get(i + 2) + "";
            switch (Integer.parseInt(temp)) {
                case 14815257:
                    build += 0;
                    break;
                case 14815357:
                    build += 1;
                    break;
                case 14914457:
                    build += 2;
                    break;
                case 14914557:
                    build += 3;
                    break;
                case 14914657:
                    build += 4;
                    break;
                case 14914757:
                    build += 5;
                    break;
                case 14914857:
                    build += 6;
                    break;
                case 14914957:
                    build += 7;
                    break;
                case 14915057:
                    build += 8;
                    break;
                case 14915157:
                    build += 9;
                    break;
            }
        }
        if (listeners != null) {
            for (BarCodeListener l : listeners) {
                l.computed(build);
            }
        }
        //once computed sequence is reset:
        sequence.clear();
        sequence = null;
    }
}
