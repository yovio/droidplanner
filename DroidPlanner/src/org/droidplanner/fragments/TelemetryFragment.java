package org.droidplanner.fragments;

import org.droidplanner.DroidPlannerApp;
import org.droidplanner.drone.Drone;
import org.droidplanner.drone.DroneInterfaces.DroneEventsType;
import org.droidplanner.drone.DroneInterfaces.OnDroneListner;
import org.droidplanner.widgets.newHUD.newHUD;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.droidplanner.R;

public class TelemetryFragment extends Fragment implements OnDroneListner{

	private newHUD hud;
	private Drone drone;
	private TextView roll;
	private TextView yaw;
	private TextView pitch;
	private TextView groundSpeed;
	private TextView airSpeed;
	private TextView climbRate;
	private TextView altitude;
	private TextView targetAltitude;
	private boolean headingModeFPV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_telemetry, container,
				false);
		hud = (newHUD) view.findViewById(R.id.hudView);

		roll = (TextView) view.findViewById(R.id.rollValueText);
		yaw = (TextView) view.findViewById(R.id.yawValueText);
		pitch = (TextView) view.findViewById(R.id.pitchValueText);


		groundSpeed = (TextView) view.findViewById(R.id.groundSpeedValue);
		airSpeed = (TextView) view.findViewById(R.id.airSpeedValue);
		climbRate = (TextView) view.findViewById(R.id.climbRateValue);
		altitude = (TextView) view.findViewById(R.id.altitudeValue);
		targetAltitude = (TextView) view.findViewById(R.id.targetAltitudeValue);

		drone = ((DroidPlannerApp) getActivity().getApplication()).drone;
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		drone.events.addDroneListener(this);
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity().getApplicationContext());		
		headingModeFPV = prefs.getBoolean("pref_heading_mode", false);
	}

	@Override
	public void onStop() {
		super.onStop();
		drone.events.removeDroneListener(this);
	}

	@Override
	public void onDroneEvent(DroneEventsType event, Drone drone) {
		switch (event) {
		case NAVIGATION:
			break;
		case ORIENTATION:
			onOrientationUpdate(drone);
			break;
		case SPEED:
			onSpeedAltitudeAndClimbRateUpdate(drone);
			break;
		default:
			break;		
		}
		
	}
	
	public void onOrientationUpdate(Drone drone) {
		float r = (float) drone.orientation.getRoll();
		float p = (float) drone.orientation.getPitch();
		float y = (float) drone.orientation.getYaw();
		
		if (!headingModeFPV & y<0 ) {
			y = 360+y;			
		}
		
		hud.setAttitude(r, p, y);

		roll.setText(String.format("%3.0f\u00B0", r));
		pitch.setText(String.format("%3.0f\u00B0", p));
		yaw.setText(String.format("%3.0f\u00B0", y));

	}

	public void onSpeedAltitudeAndClimbRateUpdate(Drone drone) {
		airSpeed.setText(String.format("%3.1f", drone.speed.getAirSpeed()));
		groundSpeed.setText(String.format("%3.1f", drone.speed.getGroundSpeed()));
		climbRate.setText(String.format("%3.1f", drone.speed.getVerticalSpeed()));
		double alt = drone.altitude.getAltitude();
		double targetAlt = drone.altitude.getTargetAltitude();
		altitude.setText(String.format("%3.1f", alt));
		targetAltitude.setText(String.format("%3.1f", targetAlt));

	}


}
