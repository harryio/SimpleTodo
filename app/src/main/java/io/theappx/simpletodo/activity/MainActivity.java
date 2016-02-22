package io.theappx.simpletodo.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import io.theappx.simpletodo.BuildConfig;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.adapter.TodoAdapter;
import io.theappx.simpletodo.customview.EmptyStateRecyclerView;
import io.theappx.simpletodo.database.TodoContract;
import io.theappx.simpletodo.helper.SimpleItemTouchHelperCallback;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.service.TodoService;
import io.theappx.simpletodo.utils.StorIOProvider;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class MainActivity extends AppCompatActivity
        implements TodoAdapter.OnItemClickListener, TodoAdapter.OnItemDismissListener {
    private static final int REQUEST_CODE_ITEM_STATUS = 1;
    private static final int REQUEST_CODE_SAVE = 2;
    private static final String STATE_LIST = "io.theappx.statelist";
    private static final String STATE_SELECTED_POSITION = "io.theappx.stateSelectedPosition";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    EmptyStateRecyclerView recyclerView;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.empty_view)
    LinearLayout emptyView;

    private TodoAdapter mTodoAdapter;
    private Subscription mSubscription;
    private int selectedTodoPosition;

    private PublishSubject<TodoItem> doneStatusObservable;
    private Subscription doneStatusSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Crashlytics setup
        Fabric.with(this, new Crashlytics.Builder().core(
                new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setUpRecyclerView();
        if (savedInstanceState == null) {
            //Load data from the database only if the activity is not started
            // because of configuration change
            loadData();
        }

        setUpDoneStatusObservable();
    }

    private void setUpDoneStatusObservable() {
        doneStatusObservable = PublishSubject.create();
        //Ignore setting done status of the item if user click
        // checkbox multiple time within 1.5 seconds
        doneStatusSubscription = doneStatusObservable
                .debounce(1500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<TodoItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(TodoItem todoItem) {
                        if (todoItem.isRemind()) {
                            if (todoItem.isDone()) {
                                //Delete alarm for item if user set item's status to done manually
                                TodoService.startActionDeleteAlarm(MainActivity.this, todoItem.getId());
                            } else {
                                //Again create alarm if the user unset item's status to done manually
                                TodoService.startActionCreateAlarm(MainActivity.this, todoItem);
                            }
                        }
                        //Save the updated item in the database in background thread
                        TodoService.startActionSaveTodo(MainActivity.this, todoItem);
                    }
                });
    }

    //Save activity state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<TodoItem> itemArrayList = new ArrayList<>(mTodoAdapter.getCurrentAdapterList());
        outState.putParcelableArrayList(STATE_LIST, itemArrayList);
        outState.putInt(STATE_SELECTED_POSITION, selectedTodoPosition);
    }

    //Restore activity state
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ArrayList<TodoItem> todoItems = savedInstanceState.getParcelableArrayList(STATE_LIST);
        selectedTodoPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        mTodoAdapter.setTodoItems(todoItems, false);
    }

    /**
     * Load data from the database in separate thread. Activity will be notified when
     * results are available
     */
    private void loadData() {
        StorIOSQLite lStorIOSQLite = StorIOProvider.getInstance(getApplicationContext());
        mSubscription = lStorIOSQLite
                .get()
                .listOfObjects(TodoItem.class)
                .withQuery(Query
                                .builder()
                                .table(TodoContract.TABLE_NAME)
                                .build()
                )
                .prepare()
                .createObservable()
                .take(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TodoItem>>() {
                    @Override
                    public void onCompleted() {
                        this.unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<TodoItem> t) {
                        //Set fetched items from the database to the list adapter
                        mTodoAdapter.setTodoItems(t, true);
                    }
                });

    }

    /**
     * Sets up RecyclerView in activity
     */
    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Sets empty view to be shown when there are no items in the database
        recyclerView.setEmptyView(emptyView);

        mTodoAdapter = new TodoAdapter();
        mTodoAdapter.setOnItemClickListener(this);
        mTodoAdapter.setOnItemDismissListener(this);

        recyclerView.setAdapter(mTodoAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mTodoAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Start CreateTodoActivity for creating new item
     */
    @OnClick(R.id.fab)
    public void onFabClick() {
        startActivityForResult(CreateTodoActivity.getCallingIntent(this), REQUEST_CODE_SAVE);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
    }

    /**
     * Start CreateTodoActivity for showing current item
     * @param position position of item in the list adapter
     * @param pTodoItem item to shown
     */
    @Override
    public void onListItemClick(int position, TodoItem pTodoItem) {
        selectedTodoPosition = position;
        startActivityForResult(CreateTodoActivity.getCallingIntent(this, pTodoItem), REQUEST_CODE_ITEM_STATUS);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
    }

    /**
     * List item checkbox check change listener
     * @param isChecked isChecked status
     * @param todoItem item for which this checkbox is associated
     */
    @Override
    public void onCheckChanged(boolean isChecked, TodoItem todoItem) {
        //Refactor this flag in item
        todoItem.setDone(isChecked);
        doneStatusObservable.onNext(todoItem);
    }

    /**
     * Callback when item is swiped/deleted from the RecyclerView
     * @param position position at which the item is swiped/deleted
     * @param todoItem item which is swiped/deleted
     */
    @Override
    public void onItemDismissed(final int position, final TodoItem todoItem) {
        //Show a snackbar allowing user to undo this action
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Item Moved to trash", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Add back item to the list on undo click
                        mTodoAdapter.addTodoItem(position, todoItem);
                    }
                })
                .setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        //Remove item from the database in the background thread
                        if (!(event == DISMISS_EVENT_ACTION)) {
                            TodoService.startActionDeleteTodo(MainActivity.this, todoItem);
                        }
                    }
                });

        //Set UI for snackbar
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_ITEM_STATUS:
                    boolean isItemDeleted = data
                            .getBooleanExtra(CreateTodoActivity.IS_ITEM_DELETED, false);
                    boolean isItemUpdated = data
                            .getBooleanExtra(CreateTodoActivity.IS_ITEM_UPDATED, true);

                    if (isItemDeleted) {
                        //Delete item from adapter with delay
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mTodoAdapter.onItemDismiss(selectedTodoPosition);
                            }
                        }, 500);
                    } else if (isItemUpdated) {
                        //Update item in adapter
                        TodoItem todoItem = data.getParcelableExtra(CreateTodoActivity.UPDATE_ITEM);
                        mTodoAdapter.replaceTodoItem(selectedTodoPosition, todoItem);
                    }
                    break;

                case REQUEST_CODE_SAVE:
                    emptyView.setVisibility(View.GONE);
                    final TodoItem todoItem =
                            data.getParcelableExtra(CreateTodoActivity.SAVE_ITEM);
                    Handler handler1 = new Handler();
                    //Add item to adapter with delay
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mTodoAdapter.addTodoItem(0, todoItem);
                        }
                    }, 500);
                    break;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;

            case R.id.action_delete_all:
                final List<TodoItem> deletedItems = mTodoAdapter.getCurrentAdapterList();
                if (deletedItems.size() > 0) {
                    mTodoAdapter.setTodoItems(new ArrayList<TodoItem>(), false);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "All Items deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mTodoAdapter.setTodoItems(deletedItems, false);
                                }
                            })
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    if (!(event == DISMISS_EVENT_ACTION)) {
                                        TodoService.startActionDeleteAll(MainActivity.this, deletedItems);
                                    }
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();
                } else {
                    Toast.makeText(MainActivity.this, "No items to be deleted", Toast.LENGTH_SHORT).show();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Unsubscribe from the observable to avoid memory leaks

        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }

        if (doneStatusSubscription != null && !doneStatusSubscription.isUnsubscribed()) {
            doneStatusSubscription.unsubscribe();
        }
    }
}
