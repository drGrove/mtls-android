package com.dannygrove.mtls;

import android.provider.BaseColumns;

public class ServerEntry implements BaseColumns {
    public static final String TABLE_NAME = "server";
    public static final String COLUMN_NAME_SERVER_NAME = "name";
    public static final String COLUMN_NAME_EMAIL = "email";
    public static final String COLUMN_NAME_FINGERPRINT = "fingerprint";
    public static final String COLUMN_NAME_COUNTRY = "country";
    public static final String COLUMN_NAME_STATE = "state";
    public static final String COLUMN_NAME_LOCALITY = "locality";
    public static final String COLUMN_NAME_ORGANIZATION_NAME = "organization_name";
    public static final String COLUMN_NAME_URL = "url";
    public static final String COLUMN_NAME_ISSUER = "issuer";
    public static final String COLUMN_NAME_LIFETIME = "lifetime";
}

