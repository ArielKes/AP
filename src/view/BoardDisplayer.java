package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class BoardDisplayer extends Canvas {
    int[][] boardData;

    public void setBoardData(int[][] boardData) {
        this.boardData = boardData;
        redraw();
    }

    public int[][] getBoardData() {
        return boardData;
    }

    public void redraw(){
        if(boardData!=null){
            double W = getWidth();
            double H = getHeight();
            double w = W / boardData[0].length;
            double h = H / boardData.length;

            GraphicsContext gc = getGraphicsContext2D();

            for(int i = 0 ; i < boardData.length ; i++){
                for(int j = 0 ; j < boardData[i]  .length ; j++){
                    if(boardData[i][j]!=0){
                        gc.fillRect(j*w ,i*h ,w ,h );

                    }
                }
            }

        }
    }


}
