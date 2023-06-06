package ru.dan1l0s.project.recipe;

public class Recipe {
    private String title;
    private String source;
    private String cooking_time;
    private String ingredients;
    private String ingredients_short;

    private String instruction;
    private String image_link;

    public Recipe(String title, String source, String cooking_time, String ingredients, String ingredients_short, String image_link) {
        this.title = title;
        this.source = source;
        this.cooking_time = cooking_time;
        this.ingredients = ingredients;
        this.ingredients_short = ingredients_short;
        this.image_link = image_link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCooking_time() {
        return cooking_time;
    }

    public void setCooking_time(String cooking_time) {
        this.cooking_time = cooking_time;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getIngredients_short() {
        return ingredients_short;
    }

    public void setIngredients_short(String ingredients_short) {
        this.ingredients_short = ingredients_short;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }
}
