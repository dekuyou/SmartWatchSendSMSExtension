/*
 Copyright (c) 2011, Sony Ericsson Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB nor the names
 of its contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jp.ddo.dekuyou.liveware.extension.sendsms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ddo.dekuyou.android.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.sdk.R;

/**
 * The sample control for SmartWatch handles the control on the accessory.
 * This class exists in one instance for every supported host application that
 * we have registered to
 */
class SendSMSControlSmartWatch extends ControlExtension {




	private Handler mHandler;

	List<Map<KEY, String>> smsList = new ArrayList<Map<KEY, String>>();
	List<String> mMsgList = null;

	int msgNo = 1;
	int phoneNo = 0;
	MODE mode = MODE.ID;

	SharedPreferences mSharedPreferences = null;

	Context context;

    private final int width;

    private final int height;

    /**
     * Create sample control.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     * @param handler The handler to use
     */
    SendSMSControlSmartWatch(final String hostAppPackageName, final Context context,
            Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        mHandler = handler;
        width = getSupportedControlWidth(context);
        height = getSupportedControlHeight(context);
        this.context = context;
        
		if (mSharedPreferences == null) {
			mSharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(context);
		}
    }

    /**
     * Get supported control width.
     *
     * @param context The context.
     * @return the width.
     */
    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
    }

    /**
     * Get supported control height.
     *
     * @param context The context.
     * @return the height.
     */
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
    }

    @Override
    public void onDestroy() {

        Log.d("SendSMSControlSmartWatch onDestroy");

        mHandler = null;
    };

    @Override
    public void onStart() {
		if (mHandler == null) {
			mHandler = new Handler();
		}

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {
		msgNo = 1;
		phoneNo = 0;
		mode = MODE.ID;
		smsList = new ArrayList<Map<KEY, String>>();


		if(mSharedPreferences.getBoolean("contact0",false)){
			
			setFixContact();
			setSmsContact();
		}else{
			setSmsContact();
			setFixContact();
			
		}

		sendBitmap();


    }
    
	private void setFixContact(){
		
		for (int i = 1; i <= Const.FIX_CONTACT_SIZE; i++) {

			if (!"".equals(mSharedPreferences.getString("contact"
					+ String.valueOf(i), ""))) {
				
				Map<KEY, String> map = new HashMap<KEY, String>();

				map.put(KEY.PHONE,  mSharedPreferences.getString("contact"
						+ String.valueOf(i), ""));
				map.put(KEY.NAME,  mSharedPreferences.getString("contact"
						+ String.valueOf(i), ""));
				
				smsList.add(map);
			}

		}
		
	}

	private void setSmsContact() {
		Uri uriSms = Uri.parse("content://sms/inbox");
		// Uri uriSms = Uri.parse("content://mms-sms/inbox");

		String columns[] = new String[] { "distinct address" };
		Cursor c = context.getContentResolver()
				.query(uriSms, columns, null, null, null);

		c.moveToFirst();
		CharSequence[] list = new CharSequence[c.getCount()];

		for (int i = 0; i < list.length; i++) {

			String[] proj = new String[] { Phone._ID, Phone.DISPLAY_NAME,
					Phone.NUMBER };

			Uri _uri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri
					.encode(c.getString(0)));

			Cursor _cursor = context.getContentResolver().query(_uri, proj, null, null,
					null);
			Map<KEY, String> map = new HashMap<KEY, String>();

			if (_cursor.getCount() > 0) {
				_cursor.moveToFirst();

				map.put(KEY.PHONE, c.getString(0));
				map.put(KEY.NAME, _cursor.getString(1));

			} else {
				map.put(KEY.PHONE, c.getString(0));
				map.put(KEY.NAME, c.getString(0));
			}

			_cursor.close();

			smsList.add(map);
			c.moveToNext();
		}

		c.close();
	}
	
	private String getName() {
		
		if(smsList.size() == 0 ){
			return "There is no SMS.";
		}

		Map<KEY, String> tmpMap = smsList.get(phoneNo);

		return tmpMap.get(KEY.NAME);
	}

	private String getPhoneNo() {

		Map<KEY, String> tmpMap = smsList.get(phoneNo);

		return tmpMap.get(KEY.PHONE);
	}	
	
	private void sendBitmap() {

		String msg = "";

		switch (mode) {
		case ID:
			msg = getName();
			break;
		case MSG:
			msg = mMsgList.get(msgNo - 1);
			break;

		default:
			break;
		}
		
		if("".equals(msg)){
			msg = "No List.";
		}

		sendTextBitmap( msg, 128, 12);

	}

    @Override
    public void onPause() {

    }



    @Override
    public void onTouch(final ControlTouchEvent event) {
        Log.d(SendSMSExtensionService.LOG_TAG, "onTouch() " + event.getAction());
        if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE
        		&& event.getX() > 100 && event.getY() > 100) {
        	
        	
			switch (mode) {
			case MSG:

				sendTextBitmap("Sending....", 128, 12);

				sendSMS();

				sendBitmap();
				break;

			default:
				break;
			}
        	
        	
        }
    }

	@Override
	public void onSwipe(int direction) {

		super.onSwipe(direction);
		
		switch (direction) {
		case Control.Intents.SWIPE_DIRECTION_UP:
			switch (mode) {
			case ID:
				if (phoneNo == smsList.size() - 1) {
					phoneNo = 0;
				} else {
					phoneNo += 1;
				}

				sendBitmap();
				break;
			case MSG:
				if (msgNo == mMsgList.size()) {
					msgNo = 1;
				} else {
					msgNo += 1;
				}

				sendBitmap();

				break;
			default:
				break;
			}			
			break;

		case Control.Intents.SWIPE_DIRECTION_DOWN:
			switch (mode) {
			case ID:
				if (phoneNo == 0) {
					phoneNo = smsList.size() - 1;
				} else {
					phoneNo -= 1;
				}

				sendBitmap();

				break;
			case MSG:
				if (msgNo == 1) {
					msgNo = mMsgList.size();
				} else {
					msgNo -= 1;
				}

				sendBitmap();

				break;
			default:
				break;
			}

			
			break;

		case Control.Intents.SWIPE_DIRECTION_RIGHT:
			mode = MODE.ID;
			clearDisplay();
			sendBitmap();
			break;

		case Control.Intents.SWIPE_DIRECTION_LEFT:
			if(smsList.size() == 0){
				break;
			}
			
			msgNo = 1;
			mode = MODE.MSG;
			mMsgList = new ArrayList<String>();

			for (int i = 1; i <= Const.MSG_LIST_SIZE; i++) {

				if (!"".equals(mSharedPreferences.getString("Msg"
						+ String.valueOf(i), ""))) {

					mMsgList.add(String.valueOf(i)
							+ ":"
							+ mSharedPreferences.getString("Msg"
									+ String.valueOf(i), ""));
				}

			}
			if (mMsgList.size() == 0) {
				mMsgList.add("0:SendSMS");
			}

			sendBitmap();

			doDrawName();			
			break;

		default:
			break;
		}
		
	};
	private void doDrawName() {
		// 

		mHandler.postDelayed(new Runnable() {
			public void run() { //
				try {

					Bitmap bitmap = null;
					try {
						bitmap = Bitmap.createBitmap(128, 12,
								Bitmap.Config.RGB_565);
					} catch (IllegalArgumentException e) {
						return;
					}

					Canvas canvas = new Canvas(bitmap);

					// Set the text properties in the canvas
					TextPaint textPaint = new TextPaint();
					textPaint.setTextSize(12);
					textPaint.setColor(Color.WHITE);

					// Create the text layout and draw it to the canvas
					Layout textLayout = new StaticLayout(getName(), textPaint,
							128, Layout.Alignment.ALIGN_NORMAL, 1, 1, false);
					textLayout.draw(canvas);

					showBitmap(bitmap, 0, 20);

					bitmap.recycle();
					
					Bitmap bmp = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.sendbutton);

					showBitmap(bmp, 100, 108);
					
					bmp.recycle();
				} catch (Exception e) {
					Log.e( "Failed to drowBitmap .");
				}

			}
		}, 10);
		

	}

	private void sendSMS() {
		// 

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 
				SmsManager smsManager = SmsManager.getDefault();

				smsManager.sendTextMessage(getPhoneNo(), null, mMsgList.get(
						msgNo - 1).substring(
						mMsgList.get(msgNo - 1).indexOf(":") + 1), null, null);

				ContentValues values = new ContentValues();
				values.put("address", getPhoneNo());
				values.put("body", mMsgList.get(msgNo - 1).substring(
						mMsgList.get(msgNo - 1).indexOf(":") + 1));
				context.getContentResolver().insert(Uri.parse("content://sms/sent"),
						values);

			}
		}, 1000L);

	}
	
    public void sendTextBitmap( String text, int bitmapSizeX, int fontSize) {
        // Empty bitmap and link the canvas to it
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(bitmapSizeX, fontSize, Bitmap.Config.RGB_565);
        }
        catch(IllegalArgumentException  e) {
            return;
        }
        
        Canvas canvas = new Canvas(bitmap);

        // Set the text properties in the canvas
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(fontSize);
        textPaint.setColor(Color.WHITE);

        // Create the text layout and draw it to the canvas
        Layout textLayout = new StaticLayout(text, textPaint, bitmapSizeX, Layout.Alignment.ALIGN_CENTER, 1, 1, false);
        textLayout.draw(canvas);
   
        showBitmap(bitmap, centerX(bitmap), centerY(bitmap));

   
    }
    
    /**
     * Get centered X axle
     * 
     * @param bitmap
     * @return
     */
    private int centerX(Bitmap bitmap) {
        return (width/2) - (bitmap.getWidth()/2);
    }
    
    /**
     * Get centered Y axle
     * 
     * @param bitmap
     * @return
     */
    private int centerY(Bitmap bitmap) {
        return (height/2) - (bitmap.getHeight()/2);
    }

}



enum MODE {
	ID, MSG;
}

enum KEY {
	PHONE, NAME;
}