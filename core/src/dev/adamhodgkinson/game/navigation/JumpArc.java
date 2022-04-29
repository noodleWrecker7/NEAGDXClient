package dev.adamhodgkinson.game.navigation;

/**
 * Stores the weight of an arc in the navgraph and any information required to perform that jump
 */
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
