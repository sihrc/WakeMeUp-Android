package com.sihrc.wakemeup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

public class AlarmListAdapter extends BaseAdapter {

    private MainActivity activity;
    private List<AlarmModel> mAlarms;
    int repeatColor;

    
    public AlarmListAdapter(MainActivity activity, List<AlarmModel> alarms) {
        this.activity = activity;
        mAlarms = alarms;
        repeatColor = activity.getResources().getColor(android.R.color.holo_blue_dark);
    }

    public void setAlarms(List<AlarmModel> alarms) {
        mAlarms = alarms;
    }

    @Override
    public int getCount() {
        if (mAlarms != null) {
            return mAlarms.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mAlarms != null) {
            return mAlarms.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (mAlarms != null) {
            return mAlarms.get(position).id;
        }
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        AlarmHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.alarm_list_item, parent, false);

            holder = new AlarmHolder();
            holder.txtTime = (TextView) view.findViewById(R.id.alarm_item_time);
            holder.txtName = (TextView) view.findViewById(R.id.alarm_item_name);

            holder.sunday = (TextView) view.findViewById(R.id.alarm_item_sunday);
            holder.monday = (TextView) view.findViewById(R.id.alarm_item_monday);
            holder.tuesday = (TextView) view.findViewById(R.id.alarm_item_tuesday);
            holder.wednesday = (TextView) view.findViewById(R.id.alarm_item_wednesday);
            holder.thursday = (TextView) view.findViewById(R.id.alarm_item_thursday);
            holder.friday = (TextView) view.findViewById(R.id.alarm_item_friday);
            holder.saturday = (TextView) view.findViewById(R.id.alarm_item_saturday);

            holder.ringTone = (TextView) view.findViewById(R.id.alarm_item_chime);
            holder.toggleButton = (ToggleButton) view.findViewById(R.id.alarm_item_toggle);

            view.setTag(R.id.ALARM_VIEW_TAG, holder);

        } else {
            holder = (AlarmHolder) view.getTag(R.id.ALARM_VIEW_TAG);
        }

        AlarmModel model = (AlarmModel) getItem(position);

        holder.txtTime.setText(Utils.formatTimeAmPm(model.timeHour, model.timeMinute));
        holder.txtName.setText(model.name);

        updateTextColor(holder.sunday, model.getRepeatingDay(AlarmModel.SUNDAY));
        updateTextColor(holder.monday, model.getRepeatingDay(AlarmModel.MONDAY));
        updateTextColor(holder.tuesday, model.getRepeatingDay(AlarmModel.TUESDAY));
        updateTextColor(holder.wednesday, model.getRepeatingDay(AlarmModel.WEDNESDAY));
        updateTextColor(holder.thursday, model.getRepeatingDay(AlarmModel.THURSDAY));
        updateTextColor(holder.friday, model.getRepeatingDay(AlarmModel.FRIDAY));
        updateTextColor(holder.saturday, model.getRepeatingDay(AlarmModel.SATURDAY));

        holder.ringTone.setText("-  " + RingtoneManager.getRingtone(activity, model.alarmTone).getTitle(activity));

        holder.toggleButton.setChecked(model.isEnabled);
        holder.toggleButton.setTag(model.id);
        holder.toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setAlarmEnabled((Long) buttonView.getTag(), isChecked);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Switch to Detail View
                activity.switchToFragment(AlarmDetailsFragment.newInstance((Long) view.getTag(R.id.ALARM_ID_TAG)), true);
            }
        });

        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteAlarm((Long) v.getTag(R.id.ALARM_ID_TAG));
                return true;
            }
        });

        view.setTag(R.id.ALARM_ID_TAG, model.id);

        return view;
    }

    private void updateTextColor(TextView view, boolean isOn) {
        if (isOn) {
            view.setTextColor(repeatColor);
        } else {
            view.setTextColor(Color.BLACK);
        }
    }

    public void deleteAlarm(long id) {
        final long alarmId = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Please confirm")
                .setTitle("Delete set?")
                .setCancelable(true)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancel Alarms
                        AlarmManagerHelper.cancelAlarms(activity);
                        //Delete alarm from DB by id
                        WakeApp.dbHelper.deleteAlarm(alarmId);
                        //Refresh the list of the alarms in the adaptor
                        setAlarms(WakeApp.dbHelper.getAlarms());
                        //Notify the adapter the data has changed
                        notifyDataSetChanged();
                        //Set the alarms
                        AlarmManagerHelper.setAlarms(activity);
                    }
                }).show();
    }

    private void setAlarmEnabled(long id, boolean isEnabled) {
        AlarmManagerHelper.cancelAlarms(activity);

        AlarmModel model = WakeApp.dbHelper.getAlarm(id);
        model.isEnabled = isEnabled;
        WakeApp.dbHelper.updateAlarm(model);

        AlarmManagerHelper.setAlarms(activity);
    }

    private static class AlarmHolder {
        TextView txtTime, txtName, sunday, monday, tuesday, wednesday, thursday, friday, saturday, ringTone;
        ToggleButton toggleButton;
    }

}
