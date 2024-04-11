package quietquest.app;

import java.util.ArrayList;

public class Quest {

    // attributes of Quest
    private String title;
    private String description;
    private ArrayList<String> tasks;

    // constructors
    public Quest(){}
    public Quest(String title, String description, ArrayList<String> tasks){
        this.title = title;
        this.description = description;
        this.tasks = tasks;
    }

    // getters
    public String getTitle() {
        return this.title;
    }


}
