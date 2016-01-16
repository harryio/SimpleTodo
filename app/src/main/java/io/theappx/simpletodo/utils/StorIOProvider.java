package io.theappx.simpletodo.utils;

import android.content.Context;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

import io.theappx.simpletodo.database.DatabaseHelper;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.model.TodoItemStorIOSQLiteDeleteResolver;
import io.theappx.simpletodo.model.TodoItemStorIOSQLiteGetResolver;
import io.theappx.simpletodo.model.TodoItemStorIOSQLitePutResolver;

public class StorIOProvider {
    private static StorIOSQLite mStorIOSQLite;

    private StorIOProvider() {
    }

    public static StorIOSQLite getInstance(Context pContext) {
        if (mStorIOSQLite == null) {
            mStorIOSQLite = DefaultStorIOSQLite.builder()
                    .sqliteOpenHelper(new DatabaseHelper(pContext))
                    .addTypeMapping(TodoItem.class, SQLiteTypeMapping.<TodoItem>builder()
                            .putResolver(new TodoItemStorIOSQLitePutResolver())
                            .getResolver(new TodoItemStorIOSQLiteGetResolver())
                            .deleteResolver(new TodoItemStorIOSQLiteDeleteResolver())
                            .build()
                    )
                    .build();
        }

        return mStorIOSQLite;
    }
}
