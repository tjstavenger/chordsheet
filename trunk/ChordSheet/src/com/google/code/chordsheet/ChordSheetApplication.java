package com.google.code.chordsheet;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dHVzaVRYSXd0MFpFQTlrZ1ZSb0pWNFE6MQ")
public class ChordSheetApplication extends Application {

	@Override
	public void onCreate() {
		ACRA.init(this);

		super.onCreate();
	}
}
