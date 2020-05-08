package studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;

public class Lessons_adapter extends RecyclerView.Adapter<Lessons_adapter.MyViewHolder> {

    private ArrayList<ArrayList<Object>> allLessons;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        TextView unit_name, unit_date, unit_course,lesson_id_txt;

        public MyViewHolder(View v) {
            super(v);
            view = v;
        }

        public void setUnit_Details(String name, String date, String course,String lesson_id) {
            unit_name = view.findViewById(R.id.unit_title);
            unit_course = view.findViewById(R.id.course);
            unit_date = view.findViewById(R.id.date);
            lesson_id_txt = view.findViewById(R.id.lesson_id);

            unit_name.setText(name);
            unit_course.setText(course);
            unit_date.setText(date);
            lesson_id_txt.setText(lesson_id);
        }
    }
    public Lessons_adapter(ArrayList<ArrayList<Object>> lessonList) {
        this.allLessons = lessonList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_lesson_card, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ArrayList<Object> lesson = allLessons.get(i);

        String unit_name = (String) lesson.get(8);
        String course = (String) lesson.get(9);
        String unit_starttime = (String) lesson.get(3);
        String lesson_id =(String) lesson.get(0);

//
        String date = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault()).format(new Date(Long.parseLong(unit_starttime)));
        String starttime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(unit_starttime)));
//        String endtime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(unit_endtime)));

        String final_date = date +" at "+starttime+ ".";

//        String unit_name = lesson.getUnit_name();
//        String unit_name = lesson.getUnit_name();

        myViewHolder.setUnit_Details(unit_name,final_date,course,lesson_id);

    }

    @Override
    public int getItemCount() {
        return allLessons.size();
    }
}

