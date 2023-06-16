package org.nsu.fit.golenko_dmitriy.tdc.model;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserData {
    private String username;
    private int score;

    public void setScore(int score){
        if (this.score > score){
            return;
        }
        this.score = score;
    }
}
