package io.theappx.simpletodo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.theappx.simpletodo.R;
import io.theappx.simpletodo.adapter.TodoAdapter;
import io.theappx.simpletodo.database.TodoContract;
import io.theappx.simpletodo.helper.SimpleItemTouchHelperCallback;
import io.theappx.simpletodo.model.TodoItem;
import io.theappx.simpletodo.utils.StorIOProvider;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity
        implements TodoAdapter.OnItemClickListener, TodoAdapter.OnItemDismissListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private TodoAdapter mTodoAdapter;
    private Subscription mSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        setUpRecyclerView();
        loadData();
    }

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<TodoItem>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //TODO Implement error view here
                    }

                    @Override
                    public void onNext(List<TodoItem> t) {
                        mTodoAdapter.setTodoItems(t);
                    }
                });

    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        //TODO Set number of columns according to available width
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));

        mTodoAdapter = new TodoAdapter(this);
        mTodoAdapter.setOnItemClickListener(this);
        mTodoAdapter.setOnItemDismissListener(this);

        recyclerView.setAdapter(mTodoAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mTodoAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        startActivity(CreateTodoActivity.getCallingIntent(this));
    }

    @Override
    public void onListItemClick(TodoItem pTodoItem) {
        startActivity(CreateTodoActivity.getCallingIntent(this, pTodoItem));
    }

    @Override
    public void onItemDismissed(int position, TodoItem todoItem) {

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }
}
