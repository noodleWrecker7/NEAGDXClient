package dev.adamhodgkinson;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import dev.adamhodgkinson.game.GameSprite;
import dev.adamhodgkinson.game.MeleeWeapon;
import dev.adamhodgkinson.game.Weapon;

public class WeaponData {
    public int damage;
    public int range;
    public int attackspeed; // time in ms between hits, higher value is slower
    public float knockback;
    public String textureName;
    public boolean isMelee;

    /**Creates weapon data and fills with default values*/
    public WeaponData(){

    }

}
