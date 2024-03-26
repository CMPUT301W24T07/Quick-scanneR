package com.example.quickscanner.controller;

import androidx.annotation.NonNull;

import com.example.quickscanner.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseUserController
{
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final CollectionReference usersRef;

    public FirebaseUserController()
    {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        usersRef = db.collection("users");
    }
    /**
     * Validates the ID of the user or event, throwing an IllegalArgumentException if the ID is null or empty.
     *
     * @param id The ID to validate. It should be a non-null and non-empty string.
     * @throws IllegalArgumentException If the ID is null or empty.
     */
    private void validateId(String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new IllegalArgumentException("ID cannot be null or empty in User controller");
        }
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
        validateId(userId);
        return usersRef.document(userId).delete();
    }

    /**
     * Retrieves all users from Firestore.
     *
     * @return a Task that will be completed after list is fetched
     */
    public Task<List<User>> getUsers() {
        Query query = usersRef.orderBy(FieldPath.documentId()).limit(30);

        Task<QuerySnapshot> task = query.get();

        return task.continueWith(new Continuation<QuerySnapshot, List<User>>() {
            @Override
            public List<User> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    List<User> users = new ArrayList<>();
                    if (querySnapshot != null) {
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            User user = document.toObject(User.class);
                            users.add(user);
                        }
                    }
                    return users;
                } else {
                    throw task.getException();
                }
            }
        });
    }
    public Task<List<User>> continueGetUsers(String lastUserId) {
        validateId(lastUserId);
        DocumentReference lastUserRef = usersRef.document(lastUserId);
        Task<DocumentSnapshot> lastUserTask = lastUserRef.get();
        return lastUserTask.continueWithTask(new Continuation<DocumentSnapshot, Task<List<User>>>() {
            @Override
            public Task<List<User>> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    DocumentSnapshot lastUserSnapshot = task.getResult();

                    Query query = usersRef.orderBy("name").startAfter(lastUserSnapshot).limit(30);

                    Task<QuerySnapshot> nextUsersTask = query.get();

                    return nextUsersTask.continueWith(new Continuation<QuerySnapshot, List<User>>() {
                        @Override
                        public List<User> then(@NonNull Task<QuerySnapshot> task) throws Exception {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                List<User> users = new ArrayList<>();
                                if (querySnapshot != null) {
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        User user = document.toObject(User.class);
                                        users.add(user);
                                    }
                                }
                                return users;
                            } else {
                                throw task.getException();
                            }
                        }
                    });
                } else {
                    return Tasks.forException(task.getException());
                }
            }
        });
    }

    /**
     * Retrieves specific user from Firestore.
     *
     * @param userId the ID of the user to retrieve
     * @return a Task that will be completed once user is fetched
     */
    public Task<User> getUser(String userId)
    {
        validateId(userId);
        Task<DocumentSnapshot> task = usersRef.document(userId).get();
        return task.continueWithTask(new Continuation<DocumentSnapshot, Task<User>>() {
            @Override
            public Task<User> then(@NonNull Task<DocumentSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        return Tasks.forResult(user);
                    } else {
                        return Tasks.forException(new Exception("No such User"));
                    }
                } else {
                    return Tasks.forException(task.getException());
                }
            }
        });
    }


    /**
     * Retrieves specific user from Firestore.
     * The difference is this one returns a task instead of user object
     *
     * @param userId the ID of the user to retrieve
     * @return a Task holding a snapshot of the document
     */
    public Task<DocumentSnapshot> getUserTask(String userId)
    {
        //TODO remove this and replace with other get user method
        return usersRef.document(userId).get();
    }
}
