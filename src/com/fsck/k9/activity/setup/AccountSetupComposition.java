package com.fsck.k9.activity.setup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.fsck.k9.Account;
import com.fsck.k9.Preferences;
import com.fsck.k9.R;
import com.fsck.k9.activity.K9Activity;

public class AccountSetupComposition extends K9Activity {

    private static final String EXTRA_ACCOUNT = "account";

    private Account mAccount;

    private EditText mAccountSignature;
    private EditText mAccountEmail;
    private EditText mAccountAlwaysBcc;
    private EditText mAccountName;
    private CheckBox mAccountSignatureUse;
    private RadioButton mAccountSignatureBeforeLocation;
    private RadioButton mAccountSignatureAfterLocation;
    private LinearLayout mAccountSignatureLayout;

    public static void actionEditCompositionSettings(Activity context, Account account) {
        Intent i = new Intent(context, AccountSetupComposition.class);
        i.setAction(Intent.ACTION_EDIT);
        i.putExtra(EXTRA_ACCOUNT, account.getUuid());
        context.startActivity(i);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accountUuid = getIntent().getStringExtra(EXTRA_ACCOUNT);
        mAccount = Preferences.getPreferences(this).getAccount(accountUuid);

        setContentView(R.layout.account_setup_composition);

        /*
         * If we're being reloaded we override the original account with the one
         * we saved
         */
        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_ACCOUNT)) {
            accountUuid = savedInstanceState.getString(EXTRA_ACCOUNT);
            mAccount = Preferences.getPreferences(this).getAccount(accountUuid);
        }

        mAccountName = (EditText)findViewById(R.id.account_name);
        mAccountName.setText(mAccount.getName());

        mAccountEmail = (EditText)findViewById(R.id.account_email);
        mAccountEmail.setText(mAccount.getEmail());

        mAccountAlwaysBcc = (EditText)findViewById(R.id.account_always_bcc);
        mAccountAlwaysBcc.setText(mAccount.getAlwaysBcc());

        mAccountSignatureLayout = (LinearLayout)findViewById(R.id.account_signature_layout);

        mAccountSignatureUse = (CheckBox)findViewById(R.id.account_signature_use);
        boolean useSignature = mAccount.getSignatureUse();
        mAccountSignatureUse.setChecked(useSignature);
        mAccountSignatureUse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAccountSignatureLayout.setVisibility(View.VISIBLE);
                    mAccountSignature.setText(mAccount.getSignature());
                    boolean isSignatureBeforeQuotedText = mAccount.isSignatureBeforeQuotedText();
                    mAccountSignatureBeforeLocation.setChecked(isSignatureBeforeQuotedText);
                    mAccountSignatureAfterLocation.setChecked(!isSignatureBeforeQuotedText);
                } else {
                    mAccountSignatureLayout.setVisibility(View.GONE);
                }
            }
        });

        mAccountSignature = (EditText)findViewById(R.id.account_signature);

        mAccountSignatureBeforeLocation = (RadioButton)findViewById(R.id.account_signature_location_before_quoted_text);
        mAccountSignatureAfterLocation = (RadioButton)findViewById(R.id.account_signature_location_after_quoted_text);

        if (useSignature) {
            mAccountSignature.setText(mAccount.getSignature());

            boolean isSignatureBeforeQuotedText = mAccount.isSignatureBeforeQuotedText();
            mAccountSignatureBeforeLocation.setChecked(isSignatureBeforeQuotedText);
            mAccountSignatureAfterLocation.setChecked(!isSignatureBeforeQuotedText);
        } else {
            mAccountSignatureLayout.setVisibility(View.GONE);
        }
    }

    private void saveSettings() {
        mAccount.setEmail(mAccountEmail.getText().toString());
        mAccount.setAlwaysBcc(mAccountAlwaysBcc.getText().toString());
        mAccount.setName(mAccountName.getText().toString());
        mAccount.setSignatureUse(mAccountSignatureUse.isChecked());
        if (mAccountSignatureUse.isChecked()) {
        	Spanned s = Html.fromHtml(mAccountSignature.getText().toString());
            mAccount.setSignature(s);
            boolean isSignatureBeforeQuotedText = mAccountSignatureBeforeLocation.isChecked();
            mAccount.setSignatureBeforeQuotedText(isSignatureBeforeQuotedText);
        }

        mAccount.save(Preferences.getPreferences(this));
    }

    @Override
    public void onBackPressed() {
        saveSettings();
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_ACCOUNT, mAccount.getUuid());
    }

    public static void saveSignatureInHtmlFile(Context context, String s){
		//Lecture du contenu du fichier
		FileInputStream fIn = null; 
        InputStreamReader isr = null; 
        FileOutputStream fOut = null; 
        OutputStreamWriter osw = null; 
        
        char[] inputBuffer = new char[255]; 
        String data = ""; 

        try{ 
        	fIn = context.openFileInput("signatureVisualisation.html");       
        	isr = new InputStreamReader(fIn); 
        	isr.read(inputBuffer); 
        	data = new String(inputBuffer); 
			Log.i("FILE",  "Reading OK. ");  
        } 
        catch (Exception e) {       
			Log.i("FILE",  "Settings not read : " + e.getMessage());  
        }finally { 
        	try {  
        		isr.close(); 
        		fIn.close(); 
        	} catch (Exception e) { 
    			Log.i("FILE",  "Settings not read : " + e.getMessage());  
        	} 
        } 
        //Si il n'est pas vide, on le vide
    	if(!data.equals("")){
			File f = context.getFileStreamPath("signatureVisualisation.html");
			f.delete();
			Log.i("FILE", "Supression réussie."); 
    	}
        
        //Enregistrement de la signature HTML dans un fichier
		try{ 
			fOut = context.openFileOutput("signatureVisualisation.html", -1); 
			osw = new OutputStreamWriter(fOut);
			//Formatage de la chaine à enregistrer
			s = "<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" /></head><body>" +
			s + 
			"</body></html>"; 
			osw.write(s); 
			osw.flush(); 
			Log.i("FILE", "Settings saved"); 
		} 
		catch (Exception e) {       
			Log.i("FILE",  "Settings not saved : " + e.getMessage()); 
		} 
		finally { 
			try { 
				osw.close(); 
				fOut.close(); 
			} catch (Exception e) { 
				Log.i("FILE",  "Settings not saved"); 
			} 
		} 
	}
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAccount.save(Preferences.getPreferences(this));
        finish();
    }
}
