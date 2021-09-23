package AdListing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fish.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyAdAdapter extends RecyclerView.Adapter<AdListing.MyAdAdapter.AdViewHolder> {

    Context context;
    ArrayList<Advertisement> list;
    ArrayList<String> IDs;
    AlertDialog.Builder builder;
    String childRef = "Advertisement";
    String userID;
    private static final String TAG = "MyAdAdapter";

    public MyAdAdapter(Context context, ArrayList<Advertisement> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AdListing.MyAdAdapter.AdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.my_advertisement, parent, false);
        return  new AdListing.MyAdAdapter.AdViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdListing.MyAdAdapter.AdViewHolder holder, int position) {

        Advertisement ad = list.get(position);
        holder.title.setText(ad.getTitle());
        holder.location.setText(ad.getLocation());
        holder.price.setText(ad.getPrice().toString());
//            holder.date.setText(ad.getDate());

        Glide.with(context).load(list.get(position).getImageUrlMain()).into(holder.imageView);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Advertisement").setMessage("Are you sure you want to delete this?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Remove data from database
                                userID = "user2";
                                DatabaseReference databaseReference =
                                        FirebaseDatabase.getInstance().getReference()
                                                .child(childRef)
                                                .child(userID)
                                                .child(ad.getKey());
                                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Delete Successful!", Toast.LENGTH_SHORT).show();
                                        notifyItemRemoved(position);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Not deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel delete
                        dialogInterface.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.changeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateNewAd.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("AD", ad);

                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {

        TextView title, location, price, date;
        ImageView imageView, deleteButton;
        Button changeDetails;

        public AdViewHolder(@NonNull View adView) {
            super(adView);

            title = adView.findViewById(R.id.tv_ad_box_title);
            location = adView.findViewById(R.id.tv_ad_box_location);
            price = adView.findViewById(R.id.tv_ad_box_price);
//                date = adView.findViewById(R.id.tv_ad_box_date);
            imageView = adView.findViewById(R.id.ad_box_img);
            changeDetails = adView.findViewById(R.id.btn_chanege_details);
            deleteButton = adView.findViewById(R.id.btn_delete_ad);
        }
    }
}