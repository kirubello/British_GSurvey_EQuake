package org.me.gcu.British_GSurvey_EQuake.model;
/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.me.gcu.British_GSurvey_EQuake.R;

import java.util.LinkedList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private LinkedList<ItemClass> alist;
    private ConstraintLayout mConstraintLayout;


    public Adapter(Context context, LinkedList<ItemClass> alist) {
        this.layoutInflater = LayoutInflater.from(context);
        this.alist = alist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.eq_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
ItemClass itemClass = alist.get(i);
        // bind the textview with data received

        String location = itemClass.getLocation();
        viewHolder.textLocation.setText(location);

        String textPubDate = itemClass.getPubDate();
        viewHolder.textPubDate.setText(textPubDate);

        String textMag = " " + Double.toString(itemClass.getMagnitude());
        viewHolder.textMag.setText(textMag);

        String depth = itemClass.getDepth();
        viewHolder.textDepth.setText(depth);

        String cat = "EQ UK";
        viewHolder.textCat.setText(cat);

        String latlng = Double.toString(itemClass.getLat()) + " & " + Double.toString(itemClass.getLng());
        viewHolder.textLatLng.setText(latlng);

        if (itemClass.getMagnitude() < 1) {

            mConstraintLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemViewContext, R.drawable.backgroundgreen));


        } else if (itemClass.getMagnitude() >= 1 && itemClass.getMagnitude() <= 2) {

            mConstraintLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemViewContext, R.drawable.backgroundyellow));

        } else if (itemClass.getMagnitude() > 2 && itemClass.getMagnitude() < 3 ) {

            mConstraintLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemViewContext, R.drawable.backgroundorange));

        } else if (itemClass.getMagnitude() >= 3 && itemClass.getMagnitude()<= 5) {

            mConstraintLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemViewContext, R.drawable.backgroundred));

        } else if (itemClass.getMagnitude() > 5 && itemClass.getMagnitude() < 7) {

            mConstraintLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemViewContext, R.drawable.backgroundrebblack));

        } else if (itemClass.getMagnitude() >= 7) {

            mConstraintLayout.setBackground(ContextCompat.getDrawable(viewHolder.itemViewContext, R.drawable.backgroundrebblack));

        }
    }

    @Override
    public int getItemCount() {
        return alist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textPubDate, textLocation, textMag, textDepth, textCat, textLatLng;
        Context itemViewContext;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemViewContext = itemView.getContext();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(),Details.class);
                    i.putExtra("location", alist.get(getAdapterPosition()).getLocation());
                    i.putExtra("link",alist.get(getAdapterPosition()).getLink());
                    i.putExtra("pubdate",alist.get(getAdapterPosition()).getPubDate());
                    v.getContext().startActivity(i);
                }
            });
            mConstraintLayout =  itemView.findViewById(R.id.eq_card);
            textPubDate = itemView.findViewById(R.id.pubdate);
            textLocation = itemView.findViewById(R.id.location);
            textMag = itemView.findViewById(R.id.mag);
            textDepth = itemView.findViewById(R.id.depth);
            textCat = itemView.findViewById(R.id.cat);
            textLatLng = itemView.findViewById(R.id.latlng);
        }
        
        
    }
}
