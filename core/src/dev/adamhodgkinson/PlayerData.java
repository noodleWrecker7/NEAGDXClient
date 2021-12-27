package dev.adamhodgkinson;

public class PlayerData {
    InventoryData inventory;
    String texture = "game/sprites/chort";

    public PlayerData() {
        this.inventory = new InventoryData();
        this.inventory.equippedWeaponData = new WeaponData();
        this.inventory.equippedWeaponData.textureName = "game/weapons/weapon_anime_sword";
        this.inventory.equippedWeaponData.isMelee = true;
        this.inventory.equippedWeaponData.range = 20;
        this.inventory.equippedWeaponData.attackspeed = 100;
        this.inventory.equippedWeaponData.damage = 5;
    }

    public WeaponData getEquippedWeaponData() {
        return inventory.equippedWeaponData;
    }

    public String getTexture() {
        return texture;
    }
}
