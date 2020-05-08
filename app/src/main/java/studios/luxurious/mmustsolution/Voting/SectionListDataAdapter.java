package studios.luxurious.mmustsolution.Voting;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import studios.luxurious.mmustsolution.R;

public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.ViewHolder> {

    private ArrayList<Item> itemsList;
    private Context mContext;



    String colorUnvoted = "#673BB7";
    String colorvoted = "#4BAA50";

    RadioGroup rgp;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;


    public SectionListDataAdapter(Context context, ArrayList<Item> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.voting_single_card, null);
        ViewHolder mh = new ViewHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {

        holder.setData(i);
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private ImageView imageView;
        private RelativeLayout relativeLayout;
        private RadioButton radioButton;


        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.textView);
            imageView = v.findViewById(R.id.imageView);
            relativeLayout = v.findViewById(R.id.relativeLayout);
            radioButton = v.findViewById(R.id.radiobtn);


        }

        public void setData(int position) {

            Item item = itemsList.get(position);
            textView.setText(item.text);
            imageView.setImageResource(item.drawable);
            relativeLayout.setBackgroundColor(Color.parseColor(item.color));

            radioButton.setOnCheckedChangeListener((compoundButton, b) -> {

                if (b){

                    relativeLayout.setBackgroundColor(Color.parseColor(colorvoted));
                }else {

                    relativeLayout.setBackgroundColor(Color.parseColor(colorUnvoted));
                }

            });

            radioButton.setOnClickListener(v -> {


                if ((position != mSelectedPosition && mSelectedRB != null)) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton) v;


            });


            relativeLayout.setOnClickListener(v -> {

                radioButton.setChecked(true);

                if ((position != mSelectedPosition && mSelectedRB != null)) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = radioButton;
            });

            if (mSelectedPosition != position) {
                radioButton.setChecked(false);
            } else {
                radioButton.setChecked(true);
                if (mSelectedRB != null && radioButton != mSelectedRB) {
                    mSelectedRB = radioButton;
                }
            }

        }

    }
}