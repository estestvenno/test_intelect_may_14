package com.mygdx.game.basicState.pathFinder;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class RoadGameGraph implements IndexedGraph<pathNodes> {

    public Array<pathNodes> nodes;
    public Array<pathConnection> streets = new Array<>();

    /**
     * Map of Cities to Streets starting in that City.
     */
    ObjectMap<pathNodes, Array<Connection<pathNodes>>> nodeConnectionMap;

    public RoadGameGraph(pathNodes[][] nodes_matrix) {
        this.nodes = new Array<>();
        nodeConnectionMap = new ObjectMap<>();
        int ind = 0;

        int ParityIndicator = -1;
        int z = 0;
        while (ParityIndicator == -1 && z < nodes_matrix.length) {
            if (nodes_matrix[z][0] != null) {
                ParityIndicator = (int) ((nodes_matrix[z][0].coordinatesInMatrix.y + z) % 2);
            }
            z++;
        }


        for (int i = 0; i < nodes_matrix.length; i++) {
            for (int j = 0; j < nodes_matrix[i].length; j++) {
                if (nodes_matrix[i][j] == null) {
                    continue;
                }

                nodes.add(nodes_matrix[i][j]);
                nodes_matrix[i][j].index = ind;
                ind++;

                int[][] directions;
                if (j % 2 == ParityIndicator) {
                    directions = new int[][]{{1, 0}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
                } else {
                    directions = new int[][]{{1, 0}, {1, 1}, {0, 1}, {-1, 0}, {0, -1}, {1, -1}};
                }

                nodeConnectionMap.put(nodes_matrix[i][j], new Array<>());
                for (int[] offset : directions) {
                    int ni = i + offset[0];
                    int nj = j + offset[1];
                    if (ni >= 0 && ni < nodes_matrix.length && nj >= 0 && nj < nodes_matrix[i].length) {
                        if (nodes_matrix[ni][nj] == null) {
                            continue;
                        }
                        nodeConnectionMap.get(nodes_matrix[i][j]).add(new pathConnection(nodes_matrix[i][j], nodes_matrix[ni][nj]));
                    }
                }
            }
        }
    }

    public GraphPath<pathNodes> findRoadPathGraph(pathNodes startPathNodes, pathNodes goalPathNodes, Heuristic<pathNodes> heuristic) {
        GraphPath<pathNodes> path = new DefaultGraphPath<>();
        for (pathNodes a : nodes){
            if (a.equals(startPathNodes)){
                startPathNodes = a;
            }
            if (a.equals(goalPathNodes)){
                goalPathNodes = a;
            }
        }

        IndexedAStarPathFinder<pathNodes> pf = new IndexedAStarPathFinder<>(this);
        if (pf.searchNodePath(startPathNodes, goalPathNodes, heuristic, path)) {
            return path;
        } else {
            return null;
        }
    }

    @Override
    public int getIndex(pathNodes node) {
        return node.index;
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<pathNodes>> getConnections(pathNodes fromNode) {
        if (nodeConnectionMap.containsKey(fromNode)) {
            return nodeConnectionMap.get(fromNode);
        }
        return new Array<>(0);
    }
}