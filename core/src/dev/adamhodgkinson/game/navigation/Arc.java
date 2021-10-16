package dev.adamhodgkinson.game.navigation;

public class Arc {
    final public short weight;

    public Arc(short weight) {
        this.weight = weight;
    }

    public boolean isJump() {
        return false;
    }
}
