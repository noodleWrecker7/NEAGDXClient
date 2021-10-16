package dev.adamhodgkinson.game.navigation;

public class JumpArc extends Arc {
    final double xSpeed;
    final double jumpSpeed;

    public JumpArc(short weight, double xSpeed, double jumpSpeed) {
        super(weight);
        this.xSpeed = xSpeed;
        this.jumpSpeed = jumpSpeed;
    }

    public boolean isJump() {
        return true;
    }
}
