package io.theappx.simpletodo.utils;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;

public class StorIOProvider {
    private static StorIOSQLite mStorIOSQLite;

    private StorIOProvider() {
    }

    private StorIOSQLite getInstance() {
        if (mStorIOSQLite == null) {
            //TODO Create instance here
        }

        return mStorIOSQLite;
    }
}
