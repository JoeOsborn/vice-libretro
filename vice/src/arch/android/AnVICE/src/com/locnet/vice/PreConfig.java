/*
 *  Copyright (C) 2011 Locnet (android.locnet@gmail.com)
 *  
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package com.locnet.vice;

import java.io.File;

import org.ab.nativelayer.ImportView;
import org.ab.uae.FloppyImportView;
import org.ab.uae.RomImportView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreConfig extends PreferenceActivity
implements OnSharedPreferenceChangeListener
{
	public static final String PREF_KEY_START = "pref_key_start";
	//public static final String PREF_KEY_FAMEC_ON = "pref_key_famec_on";
	public static final String PREF_KEY_NTSC_ON = "pref_key_ntsc_on";
	public static final String PREF_KEY_ROM = "pref_key_rom";
	//public static final String PREF_KEY_FLOPPY_COUNT = "pref_key_floppy_count";
	public static final String PREF_KEY_FLOPPY1 = "pref_key_floppy1";
	public static final String PREF_KEY_FLOPPY2 = "pref_key_floppy2";
	public static final String PREF_KEY_FLOPPY3 = "pref_key_floppy3";
	public static final String PREF_KEY_FLOPPY4 = "pref_key_floppy4";
	//public static final String PREF_KEY_SCREEN_HEIGHT = "pref_key_screen_height";
	public static final String PREF_KEY_TRUE_DRIVE_ON = "pref_key_true_drive_on";
	public static final String PREF_KEY_SCREEN_BORDER_ON = "pref_key_screen_border_on";

	public static final String KERNAL_NAME = "KERNAL";
	public static final String BASIC_NAME = "BASIC";
	public static final String CHARGEN_NAME = "CHARGEN";
	public static final String DRIVE_NAME = "DRIVES";
		
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.prefs);
		
		final Context context = this;

		String key = PREF_KEY_START;
		Preference pref;
		
		key = PREF_KEY_START;
		pref = (Preference) getPreferenceScreen().findPreference(key);
		if (pref != null) {
			setEnableStart();
			pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {		
				public boolean onPreferenceClick(Preference preference) {
					Intent settingsIntent = new Intent();
					settingsIntent.setClass(context, DosBoxLauncher.class);
			   		startActivityForResult(settingsIntent, 20);
					
					return true;
				}
			});
		}

		setRomHandler(PREF_KEY_ROM, Globals.PREFKEY_ROM_INT);
		setFloppyHandler(PREF_KEY_FLOPPY1, Globals.PREFKEY_F1_INT);
		setFloppyHandler(PREF_KEY_FLOPPY2, Globals.PREFKEY_F2_INT);
		setFloppyHandler(PREF_KEY_FLOPPY3, Globals.PREFKEY_F3_INT);
		setFloppyHandler(PREF_KEY_FLOPPY4, Globals.PREFKEY_F4_INT);

		{
			 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
			 
			 setFileSummary(PREF_KEY_ROM, sp.getString(Globals.PREFKEY_ROM, null));
			 setFileSummary(PREF_KEY_FLOPPY1, sp.getString(Globals.PREFKEY_F1, null));
			 setFileSummary(PREF_KEY_FLOPPY2, sp.getString(Globals.PREFKEY_F2, null));
			 setFileSummary(PREF_KEY_FLOPPY3, sp.getString(Globals.PREFKEY_F3, null));
			 setFileSummary(PREF_KEY_FLOPPY4, sp.getString(Globals.PREFKEY_F4, null));
	
			//TODO
			 //setEnableFloppy();
		}
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);		
	}
	
	@Override
	protected void onDestroy() {
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	void setEnableStart() {
		Preference pref = (Preference) getPreferenceScreen().findPreference(PREF_KEY_START);
		if (pref != null) {
			String path = PreferenceManager.getDefaultSharedPreferences(this).getString(Globals.PREFKEY_ROM, null);  
			if ((path != null) && (path.length() > 0)) {
				pref.setEnabled(true);
			}
		}
	}
	
	void setRomHandler(String key, final int requestCode) {
		setFileHandler(this, key, requestCode, true);
	}

	void setFloppyHandler(String key, final int requestCode) {
		setFileHandler(this, key, requestCode, false);
	}
	
	void setFileHandler(final Context context, String key, final int requestCode, final boolean rom) {
		Preference pref = (Preference) getPreferenceScreen().findPreference(key);
		if (pref != null)
			pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {		
				public boolean onPreferenceClick(Preference preference) {
					Intent settingsIntent = new Intent();
					settingsIntent.setClass(context, ImportView.class);
					settingsIntent.putExtra("import", (rom)?(new RomImportView()):(new FloppyImportView()));
	           		startActivityForResult(settingsIntent, requestCode);
	           		
					return true;
				}
			});					
	}
	
	String getWarning(String path, String file) {
		String result = "";

		try {
			File temp = new File(path, file);
			if (!temp.exists())
				result = " " + file;
			temp = null;
		}
		catch (SecurityException e) {
		}
		
		return result;
	}
	
	void setTrueDriveSummary(String path) {
		Preference pref = (Preference) getPreferenceScreen().findPreference(PREF_KEY_TRUE_DRIVE_ON);
		if (pref != null) {
			String summary = getString(R.string.pref_true_drive_on_summary);
			boolean	found = false; 
			
			if ((path != null) && (path.length() > 0)) {
				File temp = new File(path, DRIVE_NAME);
				try {
					if (temp.exists())
						found = true;
				}		
				catch (SecurityException e) {
				}
			}
			if (!found)
				summary += "\nWarning: missing " + DRIVE_NAME + " ROMs";
			
			pref.setSummary(summary);
		}
	}
	
	void setFileSummary(String key, String file) {
		Preference pref = (Preference) getPreferenceScreen().findPreference(key);
		if (pref != null) {
			if (PREF_KEY_ROM.equals(key) && (file != null)) {
				String path = new File(file).getParent();
				String warning = "";

				if (path.endsWith("c64") || (path.endsWith("C64"))) {
					warning += getWarning(path, KERNAL_NAME);
					warning += getWarning(path, BASIC_NAME);
					warning += getWarning(path, CHARGEN_NAME);

					if (warning.length() > 0)
						warning = "\nWarning: missing" + warning;

					setTrueDriveSummary(new File(path).getParent());
				}
				else
					warning = "\nWarning: ROM must in folder named \"c64\"";
				
				pref.setSummary(file + warning);
			}
			else
				pref.setSummary((file != null)?file:"(empty)");
		}
	}
	
	//TODO
	/*void setEnableFloppy() {
		 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

		 int floppyCount = Integer.valueOf(sp.getString(PREF_KEY_FLOPPY_COUNT, getString(R.string.pref_default_floppy_count)));
		 enableFloppy(PREF_KEY_FLOPPY1, (floppyCount >= 1));
		 enableFloppy(PREF_KEY_FLOPPY2, (floppyCount >= 2));
		 enableFloppy(PREF_KEY_FLOPPY3, (floppyCount >= 3));
		 enableFloppy(PREF_KEY_FLOPPY4, (floppyCount >= 4));
	}*/
	
	void enableFloppy(String key, boolean enabled) {
		Preference pref = (Preference) getPreferenceScreen().findPreference(key);
		if (pref != null) {
			pref.setEnabled(enabled);
		}
	}
	
	 @Override
	 protected void onActivityResult(final int requestCode, final int resultCode, final Intent extras) {
		 if (resultCode == RESULT_OK) {
			 String prefKey = null;
			 String path = null;
			 if (requestCode == Globals.PREFKEY_ROM_INT) {
				 prefKey = Globals.PREFKEY_ROM;
				 path = extras.getStringExtra("currentFile");
				 setFileSummary(PREF_KEY_ROM, path);
			 } else if (requestCode == Globals.PREFKEY_F1_INT) {
				 prefKey = Globals.PREFKEY_F1;
				 path = extras.getStringExtra("currentFile");
				 setFileSummary(PREF_KEY_FLOPPY1, path);
			 } else if (requestCode == Globals.PREFKEY_F2_INT) {
				 prefKey = Globals.PREFKEY_F2;
				 path = extras.getStringExtra("currentFile");
				 setFileSummary(PREF_KEY_FLOPPY2, path);
			 } else if (requestCode == Globals.PREFKEY_F3_INT) {
				 prefKey = Globals.PREFKEY_F3;
				 path = extras.getStringExtra("currentFile");
				 setFileSummary(PREF_KEY_FLOPPY3, path);
			 } else if (requestCode == Globals.PREFKEY_F4_INT) {
				 prefKey = Globals.PREFKEY_F4;
				 path = extras.getStringExtra("currentFile");
				 setFileSummary(PREF_KEY_FLOPPY4, path);
			 }
			 if (prefKey != null) {
				 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				 Editor e = sp.edit();
				 e.putString(prefKey, path);
				 e.commit();
			 }
		 }
		 else {
			 String prefKey = null;
			 if (requestCode == Globals.PREFKEY_F1_INT) {
				 prefKey = Globals.PREFKEY_F1;
				 setFileSummary(PREF_KEY_FLOPPY1, null);
			 } else if (requestCode == Globals.PREFKEY_F2_INT) {
				 prefKey = Globals.PREFKEY_F2;
				 setFileSummary(PREF_KEY_FLOPPY2, null);
			 } else if (requestCode == Globals.PREFKEY_F3_INT) {
				 prefKey = Globals.PREFKEY_F3;
				 setFileSummary(PREF_KEY_FLOPPY3, null);
			 } else if (requestCode == Globals.PREFKEY_F4_INT) {
				 prefKey = Globals.PREFKEY_F4;
				 setFileSummary(PREF_KEY_FLOPPY4, null);
			 }
			 if (prefKey != null) {
				 SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
				 Editor e = sp.edit();
				 e.remove(prefKey);
				 e.commit();
			 }
		 }
		 setEnableStart();
	 }

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if ((sharedPreferences != null) && (key != null)) {
			//TODO
			/*if (key.equals(PREF_KEY_FLOPPY_COUNT)) {
				setEnableFloppy();
			}*/
		}
		
	}
}