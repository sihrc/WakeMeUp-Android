package com.sihrc.wakemeup;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class AlarmDetailsFragment extends TaggedFragment {
    MainActivity activity;
    AlarmModel alarmDetails;

    TimePicker timePicker;
    EditText edtName;
    CustomSwitch chkWeekly;
    CustomSwitch chkSunday;
    CustomSwitch chkMonday;
    CustomSwitch chkTuesday;
    CustomSwitch chkWednesday;
    CustomSwitch chkThursday;
    CustomSwitch chkFriday;
    CustomSwitch chkSaturday;
    TextView txtToneSelection;

    public static AlarmDetailsFragment newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);

        AlarmDetailsFragment fragment = new AlarmDetailsFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1: {
                    alarmDetails.alarmTone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    txtToneSelection.setText(RingtoneManager.getRingtone(activity, alarmDetails.alarmTone).getTitle(activity));
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        this.activity = (MainActivity) activity;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_details, container, false);

        timePicker = (TimePicker) rootView.findViewById(R.id.alarm_details_time_picker);
        edtName = (EditText) rootView.findViewById(R.id.alarm_details_name);
        chkWeekly = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_weekly);
        chkSunday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_sunday);
        chkMonday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_monday);
        chkTuesday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_tuesday);
        chkWednesday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_wednesday);
        chkThursday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_thursday);
        chkFriday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_friday);
        chkSaturday = (CustomSwitch) rootView.findViewById(R.id.alarm_details_repeat_saturday);
        txtToneSelection = (TextView) rootView.findViewById(R.id.alarm_label_tone_selection);

        long id = getArguments().getLong("id");

        if (id == -1) {
            alarmDetails = new AlarmModel();
        } else {
            alarmDetails = WakeApp.dbHelper.getAlarm(id);

            timePicker.setCurrentMinute(alarmDetails.timeMinute);
            timePicker.setCurrentHour(alarmDetails.timeHour);

            edtName.setText(alarmDetails.name);

            chkWeekly.setChecked(alarmDetails.repeatWeekly);
            chkSunday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SUNDAY));
            chkMonday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.MONDAY));
            chkTuesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.TUESDAY));
            chkWednesday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.WEDNESDAY));
            chkThursday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.THURSDAY));
            chkFriday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.FRIDAY));
            chkSaturday.setChecked(alarmDetails.getRepeatingDay(AlarmModel.SATURDAY));

            txtToneSelection.setText(RingtoneManager.getRingtone(activity, alarmDetails.alarmTone).getTitle(activity));
        }

        final LinearLayout ringToneContainer = (LinearLayout) rootView.findViewById(R.id.alarm_ringtone_container);
        ringToneContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, 1);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home: {
                activity.onBackPressed();
                break;
            }
            case R.id.action_save_alarm_details: {
                updateModelFromLayout();

                AlarmManagerHelper.cancelAlarms(activity);

                if (alarmDetails.id < 0) {
                    WakeApp.dbHelper.createAlarm(alarmDetails);
                } else {
                    WakeApp.dbHelper.updateAlarm(alarmDetails);
                }

                AlarmManagerHelper.setAlarms(activity);
                activity.onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateModelFromLayout() {
        alarmDetails.timeMinute = timePicker.getCurrentMinute();
        alarmDetails.timeHour = timePicker.getCurrentHour();
        alarmDetails.name = edtName.getText().toString();
        alarmDetails.repeatWeekly = chkWeekly.isChecked();
        alarmDetails.setRepeatingDay(AlarmModel.SUNDAY, chkSunday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.MONDAY, chkMonday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.TUESDAY, chkTuesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.WEDNESDAY, chkWednesday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.THURSDAY, chkThursday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.FRIDAY, chkFriday.isChecked());
        alarmDetails.setRepeatingDay(AlarmModel.SATURDAY, chkSaturday.isChecked());
        alarmDetails.isEnabled = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alarm_details, menu);
    }

    @Override
    public String tag() {
        return AlarmDetailsFragment.class.getSimpleName();
    }
}
