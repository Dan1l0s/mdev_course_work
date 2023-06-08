package ru.dan1l0s.project.recycler_view_adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import ru.dan1l0s.project.Constants;
import ru.dan1l0s.project.MainActivity;
import ru.dan1l0s.project.R;
import ru.dan1l0s.project.recipe.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private OnRecipeListener onRecipeListener;
    private Activity activity;
    private List<Recipe> list;
    private DatabaseReference database;
    private DatabaseReference user_database;

    /** Adapter default constructor */
    public RecipeAdapter(Activity activity, List<Recipe> list,
                         OnRecipeListener onRecipeListener) {
        this.onRecipeListener = onRecipeListener;
        this.list = list;
        this.activity = activity;
        database =
                FirebaseDatabase
                        .getInstance(Constants.DATABASE_LINK)
                        .getReference(Constants.RECIPES_KEY);

        user_database = FirebaseDatabase
                .getInstance(Constants.DATABASE_LINK)
                .getReference(Constants.USERS_KEY)
                .child(Constants.USER_UID);
    }

    /** onCreate method overrride for ViewHolder */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_recipe, parent, false);
        return new ViewHolder(itemView, onRecipeListener);
    }

    /** ViewHolder class */
    public class ViewHolder
            extends RecyclerView.ViewHolder implements View.OnClickListener {
        RelativeLayout relativeLayout;
        Button details_button;
        TextView title, source, cooking_time, ingredients;

        ImageView favoritesImage;
        ImageView photoImage;

        OnRecipeListener onRecipeListener;



        /** Viewholder constructor */
        public ViewHolder(View view, OnRecipeListener onRecipeListener) {
            super(view);
            relativeLayout = view.findViewById(R.id.cardLayout);
            details_button = view.findViewById(R.id.details_button);

            title = view.findViewById(R.id.recipe_title);
            source = view.findViewById(R.id.recipe_source);
            cooking_time = view.findViewById(R.id.recipe_cooking_time);
            ingredients = view.findViewById(R.id.recipe_ingredients);

            favoritesImage = view.findViewById(R.id.recipe_favorite);
            photoImage = view.findViewById(R.id.recipe_image);

            this.onRecipeListener = onRecipeListener;

            favoritesImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecipeListener != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onRecipeListener.onFavoriteClick(pos);
                        }
                    }
                }
            });

            photoImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecipeListener != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onRecipeListener.onButtonClick(pos);
                        }
                    }
                }
            });

            details_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecipeListener != null) {
                        int pos = getAbsoluteAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            onRecipeListener.onButtonClick(pos);
                        }
                    }
                }
            });

            view.setOnClickListener(this);
        }
        /** onClick listener for each recipe */
        @Override
        public void onClick(View v) {
            onRecipeListener.onRecipeClick(getAbsoluteAdapterPosition());
        }
    }

    /** ViewHolder 'update' method */
    public void onBindViewHolder(ViewHolder holder,
                                 @SuppressLint("RecyclerView") int position) {
        Recipe item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.source.setText(item.getSource());
        holder.cooking_time.setText(item.getCooking_time());
        holder.cooking_time.setTextSize(10);
        holder.ingredients.setText(item.getIngredients_short());
        holder.ingredients.setTextSize(12);

        holder.details_button.setTextSize(11);

        Glide.with(activity).load(list.get(position).getImage_link()).into(holder.photoImage);

        user_database.child(item.getName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().exists())
                    {
                        holder.favoritesImage.setImageResource(R.drawable.baseline_favorite_24);
                    }
                    else
                    {
                        holder.favoritesImage.setImageResource(R.drawable.baseline_favorite_border_24);
                    }
                }
                else
                {
                    Log.e("APP", "HELP ME PLEASE");
                }
            }
        });


    }

    /** Helper method */
    public int getItemCount() { return list.size(); }

    /** Interface for handling clicks on each recipe card */
    public interface OnRecipeListener {
        void onRecipeClick(int pos);
        void onButtonClick(int pos);
        void onFavoriteClick(int pos);
    }
}
