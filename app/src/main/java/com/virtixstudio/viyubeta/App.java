package com.virtixstudio.viyubeta;

import android.app.Application;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialisation globale de Firebase unique au démarrage de l'application
        FirebaseApp.initializeApp(this);
        
        // Active la persistance hors-ligne pour que l'app reste rapide même sans bon Wifi
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
