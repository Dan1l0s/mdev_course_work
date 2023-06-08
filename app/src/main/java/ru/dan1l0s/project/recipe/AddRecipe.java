package ru.dan1l0s.project.recipe;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Objects;
import ru.dan1l0s.project.Constants;
import ru.dan1l0s.project.R;

/** AddTask activity */
public class AddRecipe extends AppCompatActivity {
    private EditText titleText, sourceText, timeText, ingredientsText, instructionText;
    private DatabaseReference database;

    /** Override onCreate to initialize database*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        initElem();
        database = FirebaseDatabase
                    .getInstance(Constants.DATABASE_LINK)
                    .getReference(Constants.NEW_RECIPES)
                    .child(Constants.USER_UID);
    }

    /** Method which handles interaction with text fields */
    private void initElem() {
        Objects.requireNonNull(getSupportActionBar()).hide();
        titleText = findViewById(R.id.addTitleText);
        sourceText = findViewById(R.id.addSourceText);
        timeText = findViewById(R.id.addCookingTimeText);
        ingredientsText = findViewById(R.id.addIngredientsText);
        instructionText = findViewById(R.id.addInstructionText);
    }

    /** 'Save' button handler */
    public void onClickSaveButt(View view) {
        String title = titleText.getText().toString();
        String src = sourceText.getText().toString();
        String time = timeText.getText().toString();
        String ingredients = ingredientsText.getText().toString();
        String instruction = instructionText.getText().toString();

        if (TextUtils.isEmpty(title))
            titleText.setError(getString(R.string.edit_text_empty));
        if (TextUtils.isEmpty(src))
            sourceText.setError(getString(R.string.edit_text_empty));
        if (TextUtils.isEmpty(time))
            timeText.setError(getString(R.string.edit_text_empty));
        if (TextUtils.isEmpty(ingredients))
            ingredientsText.setError(getString(R.string.edit_text_empty));
        if (TextUtils.isEmpty(instruction))
            instructionText.setError(getString(R.string.edit_text_empty));

        if (TextUtils.isEmpty(title))
            titleText.requestFocus();
        else if (TextUtils.isEmpty(src))
            sourceText.requestFocus();
        else if (TextUtils.isEmpty(ingredients))
            timeText.requestFocus();
        else if (TextUtils.isEmpty(ingredients))
            ingredientsText.requestFocus();
        else if (TextUtils.isEmpty(ingredients))
            instructionText.requestFocus();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(src) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(ingredients) && !TextUtils.isEmpty(instruction)) {
            String id = database.push().getKey();
            Log.d("APP", id);
            Recipe recipe = new Recipe(id, title, src, time, ingredients);
            database.child(id).setValue(recipe);
            Toast
                    .makeText(this, getString(R.string.save_recipe_succ),
                            Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
        else
            Toast.makeText(this, getString(R.string.save_recipe_error), Toast.LENGTH_SHORT).show();
    }
}