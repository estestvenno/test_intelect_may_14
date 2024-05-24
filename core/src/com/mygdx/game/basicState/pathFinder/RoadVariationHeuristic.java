package com.mygdx.game.basicState.pathFinder;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;

public class RoadVariationHeuristic implements Heuristic<pathNodes>{
    @Override
    public float estimate(pathNodes n0, pathNodes n1) {
        return new Vector2(n0.coordinates).dst(n1.coordinates);
    }
}
