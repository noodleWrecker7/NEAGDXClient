package dev.adamhodgkinson;

import com.google.gson.Gson;

import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

public class PlayerData {
    public InventoryData inventory;
    String texture = "game/sprites/knight_m";
    GDXClient client;
    Gson g = new Gson();

    public PlayerData(GDXClient client) {
        this.client = client;
        this.inventory = new InventoryData();


        // this is all default weapon info to prevent issues if a weapon cannot be pulled from server
        this.inventory.equippedWeaponData = new WeaponData();
        this.inventory.equippedWeaponData.textureName = "game/weapons/weapon_anime_sword";
        this.inventory.equippedWeaponData.isMelee = true;
        this.inventory.equippedWeaponData.range = 5;
        this.inventory.equippedWeaponData.attackspeed = 100;
        this.inventory.equippedWeaponData.damage = 2;
    }

    /**
     * Synchronously updates weapon data from server, once this method has returned the data stored in this object should be current and up to date
     */
    public void retrieveWeaponData() {
        try {
            HttpResponse<String> response = client.getRequest("/inventory").get();
            inventory.storedweapons = g.fromJson(response.body(), WeaponData[].class);

            HttpResponse<String> response2 = client.getRequest("/inventory/weapon/equipped").get();
            System.out.println(response2.body());
            inventory.equippedWeaponData = g.fromJson(response2.body(), WeaponData.class);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the currently equipped weapon's data object
     */
    public WeaponData getEquippedWeaponData() {
        return inventory.equippedWeaponData;
    }

    public String getTexture() {
        return texture;
    }
}
