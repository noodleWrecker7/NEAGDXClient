package dev.adamhodgkinson;

public class WeaponData {
    int damage;
    int range;
    int attackspeed; // time in ms between hits, higher value is slower
    long timeOfLastHit;
    float knockback;

    /**Creates weapon data and fills with default values*/
    public WeaponData(){

    }

}
