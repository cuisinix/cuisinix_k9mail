/*  
Modified by :
Pierre GALERNEAU for Cuisinix (www.cuisinix.fr)
*/
package com.fsck.k9;

import java.io.Serializable;

import android.text.SpannableString;
import android.text.Spanned;

public class Identity implements Serializable {
    private static final long serialVersionUID = -1666669071480985760L;
    private String mDescription;
    private String mName;
    private String mEmail;
    private String mSignature;
    private boolean mSignatureUse = true;
    private String replyTo;

    public synchronized String getName() {
        return mName;
    }

    public synchronized void setName(String name) {
        mName = name;
    }

    public synchronized String getEmail() {
        return mEmail;
    }

    public synchronized void setEmail(String email) {
        mEmail = email;
    }

    public synchronized boolean getSignatureUse() {
        return mSignatureUse;
    }

    public synchronized void setSignatureUse(boolean signatureUse) {
        mSignatureUse = signatureUse;
    }

    public synchronized Spanned getSignature() {
        return new SpannableString(mSignature);
    }

    public synchronized void setSignature(Spanned signature) {
        mSignature = signature.toString();
    }

    public synchronized String getDescription() {
        return mDescription;
    }

    public synchronized void setDescription(String description) {
        mDescription = description;
    }

    public synchronized String getReplyTo() {
        return replyTo;
    }

    public synchronized void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public synchronized String toString() {
        return "Account.Identity(description=" + mDescription + ", name=" + mName + ", email=" + mEmail + ", replyTo=" + replyTo + ", signature=" + mSignature.toString();
    }
}
