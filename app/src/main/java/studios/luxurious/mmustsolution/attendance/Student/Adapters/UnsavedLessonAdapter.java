package studios.luxurious.mmustsolution.attendance.Student.Adapters;

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
import studios.luxurious.mmustsolution.attendance.Student.UnsavedLessonsSide;

public class UnsavedLessonAdapter extends RecyclerView.Adapter<UnsavedLessonAdapter.MyViewHolder> {

    private ArrayList<ArrayList<Object>> allLessons;
    private UnsavedLessonsSide fragment;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public  View view;
          TextView Txtunit_name;
          TextView Txtunit_date;
          TextView Txtunit_time;
          TextView Txtunit_mytime;
          CardView cardView_sync;




        MyViewHolder(View v) {
            super(v);
            view = v;
        }

        void setUnit_Details(String unit_id, String start_time, String unit_name, String unit_code, String final_date, String finaltime, String my_time, String finalMytime, String id, String regno) {


            Txtunit_name = view.findViewById(R.id.unit_title);
            Txtunit_date = view.findViewById(R.id.date);
            Txtunit_time = view.findViewById(R.id.time);
            Txtunit_mytime = view.findViewById(R.id.mytimetext);
            cardView_sync = view.findViewById(R.id.sync_card);
            Txtunit_name.setText(String.format("%s - %s", unit_code, unit_name));
            Txtunit_date.setText(final_date);
            Txtunit_time.setText(finaltime);
            Txtunit_mytime.setText(finalMytime);


            cardView_sync.setOnClickListener(v -> fragment.StartLesson(unit_id,start_time,my_time,regno,id));

        }
    }
    public UnsavedLessonAdapter(ArrayList<ArrayList<Object>> lessonList, UnsavedLessonsSide fragment) {
        this.allLessons = lessonList;
        this.fragment = fragment;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_unsaved_lesson_card, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ArrayList<Object> lesson = allLessons.get(i);

        String id = (String) lesson.get(0);
        String unit_id = (String) lesson.get(1);
        String start_time = (String) lesson.get(2);
        String my_time = (String) lesson.get(3);
        String regno = (String) lesson.get(4);
        String unit_code = (String) lesson.get(5);
        String unit_name = (String) lesson.get(6);

        String date = new SimpleDateFormat("dd-MMM-yy", Locale.getDefault()).format(new Date(Long.parseLong(start_time)));
        String starttime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(start_time)));
        String mytime = new SimpleDateFormat("hh-mm a", Locale.getDefault()).format(new Date(Long.parseLong(my_time)));

        myViewHolder.setUnit_Details(unit_id,start_time,unit_name,unit_code,date,starttime,my_time,mytime,id,regno);

    }

    @Override
    public int getItemCount() {
        return allLessons.size();
    }
}

