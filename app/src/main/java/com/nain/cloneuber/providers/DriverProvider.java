package com.nain.cloneuber.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nain.cloneuber.models.Client;
import com.nain.cloneuber.models.Driver;

import java.util.HashMap;
import java.util.Map;

public class DriverProvider {
    DatabaseReference mDatabase;

    public DriverProvider() {
        // Instancia de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
    }

    public Task<Void> create(Driver driver) {
        // cuando lo crea se cre con id por eso mateamos el cliente
        Map<String, Object> map = new HashMap<>();
        map.put("name", driver.getName());
        map.put("email", driver.getEmail());
        map.put("VehicleBrand", driver.getVehicleBrand());
        map.put("VehiclePlate", driver.getVehiclePlate());

        return mDatabase.child(driver.getId()).setValue(map);
    }

    public Task<Void> update(Driver driver) {
        // cuando lo crea se cre con id por eso mateamos el cliente
        Map<String, Object> map = new HashMap<>();
        map.put("name", driver.getName());
        map.put("image", driver.getImagen());
        map.put("VehicleBrand", driver.getVehicleBrand());
        map.put("VehiclePlate", driver.getVehiclePlate());

        return mDatabase.child(driver.getId()).updateChildren(map);
    }

    // para obtener los datos del Driver
    public DatabaseReference getDriver(String idDriver) {
        return mDatabase.child(idDriver);
    }
}
