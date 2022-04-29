package dev.adamhodgkinson.game.navigation;

/**
 * Stores the weight of an arc in the navgraph
 */
public class Arc {
    final public short weight;

    public Arc(short weight) {
        this.weight = weight;
    }

    public boolean isJump() {
        return false;
    }
}
