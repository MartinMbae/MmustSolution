package studios.luxurious.mmustsolution.attendance.Student.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import studios.luxurious.mmustsolution.R;

public class Units_adapter extends RecyclerView.Adapter<Units_adapter.ViewHolderUnits> {

    private ArrayList<ArrayList<Object>> Unitss;


    public Units_adapter(ArrayList<ArrayList<Object>> unitss) {
        Unitss = unitss;
    }



    @Override
    public int getItemCount() {
        return Unitss.size();
    }


    @NonNull
    @Override
    public ViewHolderUnits onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_unit_card, parent, false);


        return new ViewHolderUnits(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolderUnits myViewHolder, int i) {

        
        ArrayList<Object> lesson = Unitss.get(i);
        String unit_id = (String) lesson.get(0);
        String unit_code = (String) lesson.get(1);
        String unit_name = (String) lesson.get(2);

        myViewHolder.setUnit_Details(unit_id,unit_code,unit_name);

    }


    public class ViewHolderUnits extends RecyclerView.ViewHolder {
        public View view;
        TextView Txt_unit_id;
        TextView Txt_unit_code;
        TextView Txt_unit_name;

        private ViewHolderUnits(View v) {
            super(v);
            view = v;
        }

        void setUnit_Details(String unit_id, String unit_code, String unit_name) {
            Txt_unit_id = view.findViewById(R.id.unit_id);
            Txt_unit_code = view.findViewById(R.id.unit_code);
            Txt_unit_name = view.findViewById(R.id.unit_name);

            Txt_unit_id.setText(unit_id);
            Txt_unit_code.setText(unit_code);
            Txt_unit_name.setText(unit_name);
        }
    }

}

