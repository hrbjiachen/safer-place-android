package ca.bcit.psychopass;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CrimeListAdaptor extends ArrayAdapter<Crime> implements View.OnClickListener{

    private ArrayList<Crime> dataSet;
    Context mContext;
    Location curLocation;

    // View lookup cache
    private static class ViewHolder {
        TextView offense;
        TextView street_name;
        TextView reportedTime;
        TextView coordinate;
        TextView ReportedDateText;
        ImageView info;
    }

    public CrimeListAdaptor(ArrayList<Crime> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }

    public void setCurLocation(Location location) {
        curLocation = location;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Crime crime = getItem(position);

        Toast.makeText(mContext, "Clicked on" + crime.getOffense(), Toast.LENGTH_LONG).show();
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Crime crime = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.offense = convertView.findViewById(R.id.offense);
            viewHolder.street_name = convertView.findViewById(R.id.street_name);
            viewHolder.reportedTime = convertView.findViewById(R.id.reportedTime);
            viewHolder.coordinate = convertView.findViewById(R.id.coordinate);
            viewHolder.ReportedDateText =  convertView.findViewById(R.id.ReportedDateText);
            viewHolder.info =  convertView.findViewById(R.id.item_info);
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        Location tempLocation = new Location("Service Provider");
        tempLocation.setLatitude(crime.getLatitude());
        tempLocation.setLongitude(crime.getLongitude());

        int distance = (int) tempLocation.distanceTo(curLocation);

        viewHolder.offense.setText(crime.getOffense());
        viewHolder.street_name.setText((crime.getHouseNumber()==null? crime.getHouseNumber() +" " : "")+crime.getStreetName());
        viewHolder.reportedTime.setText("Time: " +new SimpleDateFormat("HH:mm:ss").format(crime.getReportedTime()));
        viewHolder.coordinate.setText("Distances: " + distance + " meters");
        viewHolder.ReportedDateText.setText(crime.getReportedDateText());
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}
