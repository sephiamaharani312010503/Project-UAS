package com.sephia.sephia_databaseapiproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import com.sephia.sephia_databaseapiproject.adapter.ListAdapterHandphone;
import com.sephia.sephia_databaseapiproject.model.Handphone;
import com.sephia.sephia_databaseapiproject.server.AsyncInvokeURLTask;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = "MainActivity";
    private ListView listView;
    private ActionMode actionMode;
    private ActionMode.Callback amCallback;
    private List<Handphone> listhp;
    private ListAdapterHandphone adapter;
    private Handphone selectedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview_main);
        amCallback = new ActionMode.Callback() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {actionMode = null;
            }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.activity_main_action, menu);
                return true;
            }
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_menu_edit:
                        showUpdateForm();
                        break;
                    case R.id.action_menu_delete:
                        delete();
                        break;
                }
                mode.finish();
                return false;
            }
        };
        listhp = new ArrayList<>();
        loadDataHP();
    }
    private void showUpdateForm() {
        Intent in = new Intent(getApplicationContext(), FormHandphone.class);

        in.putExtra("id", selectedList.getId().toString());
        in.putExtra("nama", selectedList.getPhone_name());
        in.putExtra("harga", selectedList.getPrice());
        startActivity(in);
    }
    private void delete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete " + selectedList.getPhone_name() + " ?");
        builder.setTitle("Delete");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                listhp.remove(selectedList);
                Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
        });
        AlertDialog alert = builder.create();
        alert.setIcon(android.R.drawable.ic_menu_delete);
        alert.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.option_menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView == null) {
            MenuItemCompat.setShowAsAction(searchItem, MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_ALWAYS);
            MenuItemCompat.setActionView(searchItem, searchView = new SearchView(MainActivity.this));
        }
        @SuppressLint("DiscouragedApi") int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",
        null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("nama");
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_menu_new) {
            Intent in = new Intent(getApplicationContext(), FormHandphone.class);
            startActivity(in);
        }
        return super.onOptionsItemSelected(item);
    }
    private void processResponse(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONArray jsonArray = jsonObj.getJSONArray("handphone");
            Log.d(TAG, "data length: " + jsonArray.length());
            Handphone handphone = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                handphone = new Handphone();
                handphone.setId(obj.getInt("id"));
                handphone.setPhone_name(obj.getString("nama"));
                handphone.setPrice(obj.getString("harga"));
                this.listhp.add(handphone);
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }
    }
    private void populateListView() {
        adapter = new ListAdapterHandphone(getApplicationContext(), this.listhp);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View v, int pos, long id) {
                if (actionMode != null) {
                    return false;
                }
                actionMode = startActionMode(amCallback);
                v.setSelected(true);
                selectedList = (Handphone) adapter.getItem(pos);
                return true;
            }
        });
        listView.setOnItemClickListener((adapterView, v, pos, id) -> {
            selectedList = (Handphone) adapter.getItem(pos);
            Intent in = new Intent(getApplicationContext(), DetailHandphone.class);
            in.putExtra("id", selectedList.getId().toString());
            in.putExtra("nama", selectedList.getPhone_name());
            in.putExtra("harga", selectedList.getPrice());
            startActivity(in);
        });
    }
    public void loadDataHP() {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
            AsyncInvokeURLTask task = new AsyncInvokeURLTask(nameValuePairs, result -> {// TODO Auto-generated method stub
                Log.d("TAG", "Login:" + result);
                if (result.equals("timeout") || result.trim().equalsIgnoreCase("Tidak dapat terkoneksi ke database")) {
                    Toast.makeText(getBaseContext(), "Tidak dapat terkoneksi dengan server", Toast.LENGTH_SHORT).show();
                    } else {
                        processResponse(result);
                        populateListView();
                    }
                });
        task.showdialog = true;
        task.message = "Load Data HP Please Wait...";
        task.applicationContext = MainActivity.this;
        task.mNoteItWebUrl = "/list_phone.php";
        task.execute();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void deleteData() {
        try {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<>(0);
            AsyncInvokeURLTask task = new AsyncInvokeURLTask(nameValuePairs, result -> {// TODO Auto-generated method stub
                Log.d("TAG", "Login:" + result);
                if (result.equals("timeout") || result.trim().equalsIgnoreCase("Tidak dapat terkoneksi ke database")) {
                    Toast.makeText(getBaseContext(), "Tidak dapat terkoneksi dengan server", Toast.LENGTH_SHORT).show();
                } else {
                    processResponse(result);
                    populateListView();
                }
            });
        task.showdialog = true;
        task.message = "Load Data HP Harap Tunggu..";
        task.applicationContext = MainActivity.this;
        task.mNoteItWebUrl = "/delete_phone.php";
        task.execute();
    } catch (Exception e) {
        e.printStackTrace();
        }
        }
@Override
public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
        }
@Override
public boolean onQueryTextSubmit(String query) {
        return false;
    }
}
