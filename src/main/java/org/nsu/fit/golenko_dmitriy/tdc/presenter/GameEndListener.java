package org.nsu.fit.golenko_dmitriy.tdc.presenter;


// CR: merge interfaces
public interface GameEndListener {
    //  CR: also inherit in game view
    void end(int score);
}
