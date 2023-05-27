package view_model;

import javafx.beans.property.*;
import model.Model;
import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {
    public Model model;
    public BooleanProperty vertical;
    public BooleanProperty check;
    public StringProperty word;
    public StringProperty board;
    public StringProperty col;
    public StringProperty row;

    public ViewModel(Model model){
        this.model = model;
        this.vertical = new SimpleBooleanProperty();
        this.check = new SimpleBooleanProperty();
        this.word = new SimpleStringProperty();
        this.board = new SimpleStringProperty();
        this.col = new SimpleStringProperty();
        this.row = new SimpleStringProperty();
    }

    public void trySetWord(){
        this.model.tryPlaceWord(word.get() ,col.get() ,row.get() ,vertical.get());
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o==model){
            this.board.set(model.board.get_as_string());
        }
    }
}
