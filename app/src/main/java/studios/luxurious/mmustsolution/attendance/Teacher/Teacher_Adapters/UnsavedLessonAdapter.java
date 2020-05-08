package studios.luxurious.mmustsolution.attendance.Teacher.Teacher_Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;
import studios.luxurious.mmustsolution.attendance.Teacher.UnsavedLessonsSide;
import studios.luxurious.mmustsolution.attendance.Teacher.Utils.DBAdapter;

public class UnsavedLessonAdapter extends RecyclerView.Adapter<UnsavedLessonAdapter.MyViewHolder> {

    private ArrayList<ArrayList<Object>> allLessons;
    Context context;
    UnsavedLessonsSide fragment;

    DBAdapter dbAdapter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        TextView Txtunit_name;
        TextView Txtunit_date;
        TextView Txtunit_time;
        CardView cardView_sync;


        public MyViewHolder(View v) {
            super(v);
            view = v;
        }

        public void setUnit_Details(String unit_id, String teacher_code, String start_time, String sem_id, String year_id, String final_date, String finaltime, String id) {


            String[] unit_idss = unit_id.split(",");
            String unit_codes = null;

            for (int i = 0; i < unit_idss.length; i++) {


                ArrayList<String> details = dbAdapter.getUnitCodeandName(unit_idss[i]);


                if (unit_codes == null) {

                    unit_codes = details.get(0);
                } else {

                    unit_codes = unit_codes + "," + details.get(0);
                }


            }

            Txtunit_name = view.findViewById(R.id.unit_title);
            Txtunit_date = view.findViewById(R.id.date);
            Txtunit_time = view.findViewById(R.id.time);
            cardView_sync = view.findViewById(R.id.sync_card);
            Txtunit_name.setText(unit_codes);
            Txtunit_date.setText(final_date);
            Txtunit_time.setText(finaltime);


            cardView_sync.setOnClickListener(v -> {

                fragment.StartLesson(unit_id, teacher_code, start_time, sem_id, year_id, id);


            });

        }
    }

    public UnsavedLessonAdapter(ArrayList<ArrayList<Object>> lessonList, Context context, UnsavedLessonsSide fragment) {
        this.allLessons = lessonList;
        this.context = context;
        this.fragment = fragment;


        dbAdapter = new DBAdapter(context);
        dbAdapter.open();

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_unsaved_lesson_card, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ArrayList<Object> lesson = allLessons.get(i);

        String unit_id = (String) lesson.get(0);
        String teacher_code = (String) lesson.get(1);
        String start_time = (String) lesson.get(2);
        String sem_id = (String) lesson.get(3);
        String year_id = (String) lesson.get(4);
        String id = (String) lesson.get(5);

        String date = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault()).format(new Date(Long.parseLong(start_time)));
        String starttime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(start_time)));

        myViewHolder.setUnit_Details(unit_id, teacher_code, start_time, sem_id, year_id, date, starttime, id);

    }

    @Override
    public int getItemCount() {
        return allLessons.size();
    }
}

