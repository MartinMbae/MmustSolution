package studios.luxurious.mmustsolution.attendance.Student.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;

public class Lessons_Adapter extends RecyclerView.Adapter<Lessons_Adapter.MyViewHolder> {

    private  ArrayList<ArrayList<Object>> lessons_from_db;
    private Context ctx;
    public  class MyViewHolder extends RecyclerView.ViewHolder {
        public View view;
        TextView unit_title, unit_date,unit_code;
        ImageView lesson_status_image;
        private MyViewHolder(View v) {
            super(v);
            view = v;


        }

        private void setLesson_details(String date,  String name, String lessonstatus,String code) {
            unit_title = view.findViewById(R.id.unit_title);
            unit_date = view.findViewById(R.id.date);
            unit_code = view.findViewById(R.id.unit_code);
            lesson_status_image = view.findViewById(R.id.lessonstatus_image);
            unit_title.setText(name);
            unit_date.setText(date);
            unit_code.setText(code);

            if (lessonstatus.equalsIgnoreCase("1")){
                lesson_status_image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.tick));
            }else{

                lesson_status_image.setImageDrawable(ctx.getResources().getDrawable(R.drawable.cancel));
            }
        }
    }

    public Lessons_Adapter(ArrayList<ArrayList<Object>> lessons, Context context) {

        this.lessons_from_db = lessons;
        ctx = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_lesson_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        ArrayList<Object> lessons_list = lessons_from_db.get(i);


        String unit_name = (String) lessons_list.get(0);
//        String teacher_name =  (String) lessons_list.get(1);
        String start_time =  (String) lessons_list.get(2);
//        String my_attendance_time =  (String) lessons_list.get(3);
        String status =  (String) lessons_list.get(4);
//        String lesson_id =  (String) lessons_list.get(5);
        String unit_code =  (String) lessons_list.get(6);
//        String sem_name =  (String) lessons_list.get(7);
//        String year_name =  (String) lessons_list.get(8);
//        String total_students =  (String) lessons_list.get(9);
//        String total_attendance =  (String) lessons_list.get(10);

        long start_time_long = Long.parseLong(start_time);


        String date_string = new SimpleDateFormat("EEE, dd/MMM/yyyy", Locale.getDefault()).format(new Date(start_time_long));
//        String start_string = getTime(start_time_long);


        myViewHolder.setLesson_details(date_string,unit_name, status,unit_code);
    }

    @Override
    public int getItemCount() {return lessons_from_db.size();}


}

