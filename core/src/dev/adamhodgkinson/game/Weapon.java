package dev.adamhodgkinson.game;

public class Weapon {
    int damage;
    int range;
    int attackspeed; // time in ms between hits, higher value is slower
    String textureName;
    long timeOfLastHit;

    public Weapon(int _damage, int _range, int _attackspeed, String _textureName) {
        damage = _damage;
        range = _range;
        attackspeed = _attackspeed;
        textureName = _textureName;
    }

    public void attack() {
        System.out.println("Default weapon attack method not overridden for " + textureName);
    }

}
