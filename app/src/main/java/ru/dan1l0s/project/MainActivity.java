package ru.dan1l0s.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ru.dan1l0s.project.recipe.Recipe;
import ru.dan1l0s.project.recycler_view_adapter.RecipeAdapter;

/** MainActivity class, where the list itself is located */
public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeListener{
  private RecyclerView ListRecyclerView;
  private TextView textView;
  private RecipeAdapter recipeAdapter;
  private List<Recipe> list;

  private DatabaseReference database;

  private FirebaseAuth mAuth;
  private Button btnLogout;

  /** onCreate override to initialize required variables and create screen */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    textView = findViewById(R.id.userTitle);
    mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();
    if (user == null) {
      finish();
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
    } else {
      Constants.USER_UID = user.getUid();
      textView.setText(getString(R.string.username_show) + " " +
                       mAuth.getCurrentUser().getEmail());
    }

    ListRecyclerView = findViewById(R.id.listRecyclerView);

    Objects.requireNonNull(getSupportActionBar()).hide();
    database =
        FirebaseDatabase
            .getInstance(Constants.DATABASE_LINK)
            .getReference(Constants.RECIPES_KEY);

    initialisation();
    getDataFromDB();

    btnLogout = findViewById(R.id.logoutButton);
    btnLogout.setOnClickListener(v -> {
      mAuth.signOut();
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
                .makeText(MainActivity.this,
                          getString(R.string.empty_task_received),
                          Toast.LENGTH_SHORT)
                .show();
            continue;
          }
          list.add(recipe);
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
  }

  @Override
  public void onRecipeClick(int pos) {
    Log.i("APP", "OnRecipe");
    getDataFromDB();
  }

  @Override
  public void onButtonClick(int pos) {
    Log.i("APP", "OnButton");
    getDataFromDB();
  }

  @Override
  public void onFavoriteClick(int pos) {
    Log.i("APP", "OnFavorite");
    getDataFromDB();
  }
}