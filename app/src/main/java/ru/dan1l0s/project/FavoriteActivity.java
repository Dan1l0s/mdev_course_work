package ru.dan1l0s.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.dan1l0s.project.recipe.Recipe;
import ru.dan1l0s.project.recipe.RecipePage;
import ru.dan1l0s.project.recycler_view_adapter.RecipeAdapter;

/** MainActivity class, where the list itself is located */
public class FavoriteActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeListener{
    private RecyclerView ListRecyclerView;
    private TextView textView;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> list;

    private DatabaseReference database;
    private DatabaseReference user_database;

    private FirebaseAuth mAuth;
    private Button btnLogout;

    /** onCreate override to initialize required variables and create screen */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorites);

        textView = findViewById(R.id.userTitle);
        mAuth = FirebaseAuth.getInstance();

        textView.setText(getString(R.string.username_show) + " " + mAuth.getCurrentUser().getEmail());

        ListRecyclerView = findViewById(R.id.listRecyclerView);

        Objects.requireNonNull(getSupportActionBar()).hide();
        initialisation();
//        getDataFromDB();

        btnLogout = findViewById(R.id.logoutButton);
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(FavoriteActivity.this, LoginActivity.class));
        });

        user_database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getDataFromDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /** Default onStart method */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /** Method which requests info from remote database */
    private void getDataFromDB() {
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list.size() > 0)
                    list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Recipe recipe = ds.getValue(Recipe.class);
                    if (recipe == null) {
                        Toast
                                .makeText(FavoriteActivity.this,
                                        getString(R.string.empty_task_received),
                                        Toast.LENGTH_SHORT)
                                .show();
                        continue;
                    }
                    user_database.child(recipe.getName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                Log.w("APP", task.getResult().getValue()+" ::::: "+task.getResult().getKey()+" :::::: "+recipe.getName());

                                if (task.getResult().getValue() != null)
                                {
                                    list.add(recipe);
                                    recipeAdapter.notifyDataSetChanged();
                                }
//                                Log.w("APPLICATION", String.valueOf(task.getResult()));
//                                Log.e("APPLICATION", recipe.getName()+" :::: "+task.getResult().getKey()+" :::: "+((String)recipe.getName()==task.getResult().getKey()));
//                                if (task.getResult().exists())
//                                {
//                                    list.add(recipe);
//                                }
                            }
                        }
                    });
                }
                recipeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        database.addValueEventListener(vListener);
    }

    /** Addition to onCreate method */
    private void initialisation() {
        ListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<Recipe>();
        recipeAdapter = new RecipeAdapter(this, list, this);
        ListRecyclerView.setAdapter(recipeAdapter);
        database = FirebaseDatabase
                    .getInstance(Constants.DATABASE_LINK)
                    .getReference(Constants.RECIPES_KEY);

        user_database = FirebaseDatabase.getInstance(Constants.DATABASE_LINK)
                .getReference(Constants.USERS_KEY).child(Constants.USER_UID);

    }

    @Override
    public void onRecipeClick(int pos) {
        Log.i("APP", "OnRecipe");
//        getDataFromDB();
    }

    @Override
    public void onButtonClick(int pos) {
        Log.i("APP", "OnButton");
        Recipe recipe = list.get(pos);
        Intent intent = new Intent(FavoriteActivity.this, RecipePage.class);
        intent.putExtra("name", recipe.getName());
        startActivity(intent);
        getDataFromDB();
    }

    @Override
    public void onFavoriteClick(int pos) {
        Log.i("APP", "OnFavorite");
        Recipe item = list.get(pos);
        user_database.child(item.getName()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().exists())
                    {
                        user_database.child(item.getName()).setValue(null);
                        getDataFromDB();
                    }
                    else
                    {
                        user_database.child(item.getName()).setValue(true);
                        getDataFromDB();
                    }
                }
            }
        });
    }

}