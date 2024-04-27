package quietquest.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;

import static javafx.application.Application.launch;

public class Quest {

  // attributes of Quest
  private String title;
  private String description;


  // constructors
  public Quest() {
  }

  public Quest(String title, String description) {
    this.title = title;
    this.description = description;
  }

  // getters
  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }
    // setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setDescription(String description){
        this.description = description;
    }


  @Override
  public String toString() {
    return title;
  }

}





