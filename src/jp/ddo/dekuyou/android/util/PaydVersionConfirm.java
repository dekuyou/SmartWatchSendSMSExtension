package jp.ddo.dekuyou.android.util;

import jp.ddo.dekuyou.liveware.extension.sendsms.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


public class PaydVersionConfirm extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

	
		new AlertDialog.Builder(this)
		.setIcon(R.drawable.icon)
		.setTitle(getText(R.string.ConfirmTitle))
		.setMessage(getText(R.string.ConfirmMessage))
		.setPositiveButton(getText(R.string.ConfirmYes), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		        /* ここにYESの処理 */
				
				Intent intent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("market://details?id=jp.ddo.dekuyou.liveware.extension.sendsms.donate"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);


				
				finish();
		    }
		})
		.setNegativeButton(getText(R.string.ConfirmNo), new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		        /* ここにNOの処理 */

				
				finish();
		    }
		})
		.show();
		
	
	
	}
	
	

}
