package dev.adamhodgkinson.game.navigation;

public class Arc {
    final public byte weight;

    public Arc(byte weight) {
        this.weight = weight;
    }

    public boolean isJump() {
        return false;
    }
}
