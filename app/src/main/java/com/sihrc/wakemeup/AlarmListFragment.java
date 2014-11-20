package com.sihrc.wakemeup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AlarmListFragment extends TaggedFragment {
    final public static String TAG = AlarmListFragment.class.getSimpleName();

    private AlarmListAdapter mAdapter;
    private MainActivity activity;

    public static AlarmListFragment newInstance() {
        return new AlarmListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        mAdapter = new AlarmListAdapter(activity, WakeApp.dbHelper.getAlarms());

        ListView listView = (ListView) rootView.findViewById(R.id.alarm_list);
        listView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alarm_list, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_add_new_alarm: {
                createAlarm(-1);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            mAdapter.setAlarms(WakeApp.dbHelper.getAlarms());
            mAdapter.notifyDataSetChanged();
        }
    }

    public void createAlarm(long id) {
        activity.switchToFragment(AlarmDetailsFragment.newInstance(id), true);
    }

    @Override
    public String tag() {
        return TAG;
    }
}
