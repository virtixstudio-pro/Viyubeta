package com.virtixstudio.viyubeta;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Active la base de données locale (Hors Ligne)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
