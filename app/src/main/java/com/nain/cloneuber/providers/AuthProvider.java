package com.nain.cloneuber.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProvider {
    FirebaseAuth mAuth;

    public AuthProvider() {
        // Instancia de firebase
        mAuth = FirebaseAuth.getInstance();
    }

    // retorno de tareas de firebase
    public Task<AuthResult> register(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // cerrar usuario en firebase
    public void logout() {
        mAuth.signOut();
    }
}
