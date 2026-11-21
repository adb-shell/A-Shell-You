package com.openworld.me.localadb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;

/** Shell-to-app Binder handoff only. Exported for UID 2000; never a public command endpoint. */
public final class PrivilegedProvider extends ContentProvider {
    @Override public boolean onCreate() { return true; }

    @Override public Bundle call(String method, String arg, Bundle extras) {
        int uid = Binder.getCallingUid();
        if ((uid != 2000 && uid != 0) || !"attach".equals(method) || extras == null)
            throw new SecurityException("Privileged handoff denied");
        return PrivilegedBridge.get(getContext()).accept(
                extras.getBinder("service"), extras.getString("generation", ""), uid);
    }

    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) { throw new SecurityException(); }
    @Override public String getType(Uri uri) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { throw new SecurityException(); }
    @Override public int delete(Uri uri, String selection, String[] args) { throw new SecurityException(); }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { throw new SecurityException(); }
}
