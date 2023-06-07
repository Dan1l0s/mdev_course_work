package ru.dan1l0s.project.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionService;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ru.dan1l0s.project.Constants;
import ru.dan1l0s.project.R;

/** UpdateTask activity */
public class RecipePage extends AppCompatActivity {
    private TextView title, source, cooking_time, ingredients, instruction;
    private ImageView recipe_image, favorites;
    private DatabaseReference database;
    private DatabaseReference user_database;
    private Recipe recipe;

    /** Override onCreate to initialize database*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);
        user_database =
                FirebaseDatabase
                        .getInstance(Constants.DATABASE_LINK)
                        .getReference(Constants.USERS_KEY)
                        .child(Constants.USER_UID);

        database = FirebaseDatabase
                .getInstance(Constants.DATABASE_LINK)
                        .getReference(Constants.RECIPES_KEY);
        initElem();
    }

    /** Method which handles interaction with text fields */
    private void initElem() {
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");

        title = findViewById(R.id.recipe_page_title);
        source = findViewById(R.id.recipe_page_source);
        cooking_time = findViewById(R.id.recipe_page_cooking_time);
        ingredients = findViewById(R.id.recipe_page_ingredients);
        instruction = findViewById(R.id.recipe_page_instruction);

        instruction.setMovementMethod(new ScrollingMovementMethod());

        recipe_image = findViewById(R.id.recipe_page_image);

        favorites = findViewById(R.id.recipe_page_favorite);

        database.child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recipe = snapshot.getValue(Recipe.class);
                title.setText(recipe.getTitle().replace("\\n", "\n"));
                source.setText(recipe.getSource().replace("\\n", "\n"));
                cooking_time.setText(recipe.getCooking_time().replace("\\n", "\n"));
                ingredients.setText(recipe.getIngredients().replace("\\n", "\n"));
                instruction.setText(recipe.getInstruction().replace("\\n", "\n"));

                Glide.with(RecipePage.this).load(recipe.getImage_link()).into(recipe_image);
                user_database.child(recipe.getName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().exists())
                            {
                                favorites.setImageResource(R.drawable.baseline_favorite_24);
                                favorites.setTag(1);
                            }
                            else
                            {
                                favorites.setImageResource(R.drawable.baseline_favorite_border_24);
                                favorites.setTag(0);
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favorites.getTag() == (Object)1)
                {
                    favorites.setImageResource(R.drawable.baseline_favorite_border_24);
                    favorites.setTag(0);
                    user_database.child(recipe.getName()).setValue(null);
                }
                else
                {
                    favorites.setImageResource(R.drawable.baseline_favorite_24);
                    favorites.setTag(1);
                    user_database.child(recipe.getName()).setValue(true);
                }
            }
        });
    }
}