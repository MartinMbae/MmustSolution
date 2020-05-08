package studios.luxurious.mmustsolution.attendance.Student.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import studios.luxurious.mmustsolution.R;

public class Lesson_Expandable_Adapter extends BaseExpandableListAdapter {

    private Context context;

    // group titles
    private List<String> listDataGroup;

    // child data in format of header title, child title
    private HashMap<String, ArrayList<ArrayList<Object>> > listDataChild;

    public Lesson_Expandable_Adapter(Context context, List<String> listDataGroup,
                                     HashMap<String, ArrayList<ArrayList<Object>>> listChildData) {
        this.context = context;
        this.listDataGroup = listDataGroup;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {



        ArrayList<ArrayList<Object>>  lessons = listDataChild.get(listDataGroup.get(groupPosition));


        ArrayList<Object> lessons_list = lessons.get(childPosition);


        String unit_nameString = (String) lessons_list.get(0);
//        String teacher_name =  (String) lessons_list.get(1);
        String start_time =  (String) lessons_list.get(2);
//        String my_attendance_time =  (String) lessons_list.get(3);
        String status =  (String) lessons_list.get(4);
//        String lesson_id =  (String) lessons_list.get(5);
        String unit_codeString =  (String) lessons_list.get(6);
//        String sem_name =  (String) lessons_list.get(7);
//        String year_name =  (String) lessons_list.get(8);
//        String total_students =  (String) lessons_list.get(9);
//        String total_attendance =  (String) lessons_list.get(10);

        long start_time_long = Long.parseLong(start_time);


        String date_string = new SimpleDateFormat("EEE, dd/MMM/yyyy", Locale.getDefault()).format(new Date(start_time_long));
//        String start_string = getTime(start_time_long);




        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.student_lesson_card, null);
        }



        TextView unit_title = convertView.findViewById(R.id.unit_title);
        TextView unit_date = convertView.findViewById(R.id.date);
        TextView unit_code = convertView.findViewById(R.id.unit_code);
        ImageView lesson_status_image = convertView.findViewById(R.id.lessonstatus_image);
        unit_title.setText(unit_nameString);
        unit_date.setText(date_string);
        unit_code.setText(unit_codeString);

        if (status.equalsIgnoreCase("1")){
            lesson_status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tick));
        }else{

            lesson_status_image.setImageDrawable(context.getResources().getDrawable(R.drawable.cancel));
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataGroup.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataGroup.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataGroup.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_row_group, null);
        }

        TextView textViewGroup = convertView.findViewById(R.id.textViewGroup);
        textViewGroup.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
