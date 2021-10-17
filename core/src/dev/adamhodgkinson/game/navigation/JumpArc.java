package dev.adamhodgkinson.game.navigation;

public class JumpArc extends Arc {
    public final float xSpeed;
    public final float jumpSpeed;

    public JumpArc(short weight, float xSpeed, float jumpSpeed) {
        super(weight);
        this.xSpeed = xSpeed;
        this.jumpSpeed = jumpSpeed;
    }

    public boolean isJump() {
        return true;
    }
}
