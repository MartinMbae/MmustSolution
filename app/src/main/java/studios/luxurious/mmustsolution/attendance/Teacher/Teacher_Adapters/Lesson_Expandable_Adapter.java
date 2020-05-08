package studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
    private HashMap<String, ArrayList<ArrayList<Object>>> listDataChild;

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


        ArrayList<ArrayList<Object>> lessons = listDataChild.get(listDataGroup.get(groupPosition));


        ArrayList<Object> lesson = lessons.get(childPosition);


        String unit_nameString = (String) lesson.get(8);
        String course = (String) lesson.get(9);
        String unit_starttime = (String) lesson.get(3);
        String lesson_id = (String) lesson.get(0);

//
        String date = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault()).format(new Date(Long.parseLong(unit_starttime)));
        String starttime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(unit_starttime)));
//        String endtime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(unit_endtime)));

        String final_date = date + " at " + starttime + ".";

//        String unit_name = lesson.getUnit_name();
//        String unit_name = lesson.getUnit_name();


        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.teacher_lesson_card, null);
        }


        TextView unit_name, unit_date, unit_course, lesson_id_txt;

        unit_name = convertView.findViewById(R.id.unit_title);
        unit_course = convertView.findViewById(R.id.course);
        unit_date = convertView.findViewById(R.id.date);
        lesson_id_txt = convertView.findViewById(R.id.lesson_id);

        unit_name.setText(unit_nameString);
        unit_course.setText(course);
        unit_date.setText(final_date);
        lesson_id_txt.setText(lesson_id);


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
