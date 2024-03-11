package com.example.quickscanner.controller;

import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class FirebaseUserController
{
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    public FirebaseUserController()
    {
        auth = FirebaseAuth.getInstance();
        usersRef = db.collection("users");
    }
    /**
     * Checks if the user is signing in for the first time by checking if the current user from FirebaseAuth is null.
     *
     * @return true if it's the first sign in, false otherwise
     */
    public boolean isFirstSignIn()
    {
        return auth.getCurrentUser() == null;
    }

    /**
     * Gets the unique ID (UID) of the current user from FirebaseAuth.
     *
     * @return the UID of the current user
     */
    public String getCurrentUserUid()
    {
        return Objects.requireNonNull(auth.getCurrentUser()).getUid();
    }

    /**
     * Creates an anonymous user in FirebaseAuth, allowing users to use the app without creating an account.
     * You can use the returned Task to attach listeners and handle success or failure.
     *
     * @return a Task that represents the asynchronous sign in operation
     */
    public Task<AuthResult> createAnonymousUser()
    {
        return auth.signInAnonymously();
    }

    // User operations

    /**
     * Adds a new user to Firestore. This operation is asynchronous and returns a Task.
     * You can use the returned Task to attach listeners and handle success or failure.
     *
     * @param user the user to be added
     * @return a Task that will be completed once the user is added
     */
    public Task<Void> addUser(User user)
    {
        return usersRef.document(user.getUid()).set(user);
    }

    /**
     * Updates an existing user in Firestore. it is asynchronous and returns a Task.
     * You can use the returned Task to attach listeners and handle success or failure.
     *
     * @param user the user with updated information
     * @return a Task that will do things once the user is updated
     */
    public Task<Void> updateUser(User user)
    {
        return usersRef.document(user.getUid()).set(user);
    }

    // User operations

    /**
     * Deletes a user from Firestore.
     *
     * @param userId the ID of the user to be deleted
     * @return a Task that will be completed once the user is deleted
     */
    public Task<Void> deleteUser(String userId)
    {
        return usersRef.document(userId).delete();
    }

    /**
     * Retrieves all users from Firestore.
     *
     * @return a Task that will be completed after list is fetched
     */
    public Task<QuerySnapshot> getUsers()
    {
        return usersRef.get();
    }

    /**
     * Retrieves specific user from Firestore.
     *
     * @param userId the ID of the user to retrieve
     * @return a Task that will be completed once user is fetched
     */
    public Task<DocumentSnapshot> getUser(String userId)
    {
        return usersRef.document(userId).get();
    }
}