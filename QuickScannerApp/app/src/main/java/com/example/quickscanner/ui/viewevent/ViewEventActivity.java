package com.example.quickscanner.ui.viewevent;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.quickscanner.R;
import com.example.quickscanner.controller.FirebaseAttendanceController;
import com.example.quickscanner.controller.FirebaseEventController;
import com.example.quickscanner.controller.FirebaseImageController;
import com.example.quickscanner.controller.FirebaseQrCodeController;
import com.example.quickscanner.controller.FirebaseUserController;
import com.example.quickscanner.databinding.ActivityVieweventBinding;
import com.example.quickscanner.model.Event;
import com.example.quickscanner.model.User;
import com.example.quickscanner.ui.addevent.QRCodeDialogFragment;
import com.example.quickscanner.ui.adminpage.BrowseEventsActivity;
import com.example.quickscanner.ui.viewevent.map.MapActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ViewEventActivity extends AppCompatActivity
{
    String eventID;
    private FirebaseEventController fbEventController;
    private FirebaseImageController fbImageController;
    private FirebaseUserController fbUserController;
    private FirebaseAttendanceController fbAttendanceController;
    private Event event;
    private ActivityVieweventBinding binding;
    private ProgressBar loading;
    private RelativeLayout contentLayout;


    private FirebaseQrCodeController fbQRCodeController;
    // UI reference
    Switch toggleGeolocation;


    private Event currentEvent;
    private Bitmap qrCodeBitmap;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityVieweventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //references
        fbEventController = new FirebaseEventController();
        fbImageController = new FirebaseImageController();
        fbQRCodeController = new FirebaseQrCodeController();
        fbUserController = new FirebaseUserController();
        fbAttendanceController = new FirebaseAttendanceController();
        toggleGeolocation = findViewById(R.id.toggle_geolocation); // geolocation switch
        loading = findViewById(R.id.loading);
        contentLayout = findViewById(R.id.contentLayout);
        loading.setVisibility(View.VISIBLE);
        contentLayout.setVisibility(View.GONE);
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh the event data
                fetchEventData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // Display Back Button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Grab any Intent bundle/parameters
        Bundle inputBundle = getIntent().getExtras();
        if (inputBundle != null)
        {
            eventID = inputBundle.getString("eventID");
            Log.d("Beans", eventID);
        }
        if (eventID != null)
        {
            Log.e("halpp", "Event ID is: " + eventID);
        }
        fetchEventData();
        //Toast.makeText(this, eventID, Toast.LENGTH_SHORT).show();


        // Get a reference to the announcement button
        FloatingActionButton announcementButton = findViewById(R.id.announcement_button);

        // Set an OnClickListener to the button
        announcementButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Create an EditText for the user to input their announcement
                final EditText input = new EditText(ViewEventActivity.this);

                // Create an AlertDialog
                new AlertDialog.Builder(ViewEventActivity.this)
                        .setTitle("Announcement")
                        .setMessage("What do you wish to announce?")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int whichButton)
                            {
                                // Get the user's input
                                String announcement = input.getText().toString();

                                // TODO: Handle the announcement (e.g., send it to Firebase)

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });


        // Get a reference to the share button
        FloatingActionButton shareButton = findViewById(R.id.share_button);

        // Set an OnClickListener to the button
        shareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Convert the QR code Bitmap to a Uri
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), qrCodeBitmap, "QR Code", null);
                Log.d("beans", "this is wheere it fails");
                Uri qrCodeUri = Uri.parse(path);

                // Create an Intent with ACTION_SEND action
                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                // Put the Uri of the image and the text you want to share in the Intent
                shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri);
                Log.d("halpp", "event is null? " + (event == null));
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Join my event titled " + event.getName() + " using this QR code");

                shareIntent.setType("image/jpeg");

                // Start the Intent
                //java.lang.NullPointerException: Attempt to invoke virtual method
                //'java.lang.String com.example.quickscanner.model.Event.getName()' on a null object reference
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });

        // implement geolocation switch behaviour
        toggleGeolocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton switchView, boolean isChecked)
            {
                if (event != null)
                {
                    // toggle user's geolocation preferences
                    event.toggleIsGeolocationEnabled();
                    // update user's geolocation preferences in firebase
                    fbEventController.updateEvent(event)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully Updated"))
                            .addOnFailureListener(e -> Log.d(TAG, "Event failed to update"));

                }
                else
                {
                    Log.e("ViewEventActivity", "Event object is null");
                }

            }
        });

    }

    // Fetches the event data from Firestore
    private void fetchEventData()
    {
        String UiD = fbUserController.getCurrentUserUid();
        fbEventController.getEvent(eventID).addOnSuccessListener(new OnSuccessListener<Event>()
        {
            @Override
            public void onSuccess(Event gotEvent)
            {
                if (gotEvent == null)
                {
                    throw new RuntimeException("No such Event");
                }
                event = gotEvent;
                boolean oldIsGeolocationEnabled = event.getIsGeolocationEnabled();
                toggleGeolocation.setChecked(event.getIsGeolocationEnabled());
                event.setGeolocationEnabled(oldIsGeolocationEnabled);
                fbEventController.updateEvent(event)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully Updated"))
                        .addOnFailureListener(e -> Log.d(TAG, "Event failed to update"));
                qrCodeBitmap = generateQRCode(event.getPromoQrCode());
                setEventDataToUI(event, UiD);

                binding.signUpButton.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v) {
                        String buttonText = binding.signUpButton.getText().toString();
                        if (buttonText.equals("Cancel Sign Up")) {
                            confirmCancelSignUp(UiD, eventID);
                            setEventDataToUI(event, UiD);
                        } else if (buttonText.equals("Sign Up for Event")) {
                            fbAttendanceController.signUp(UiD, eventID).addOnSuccessListener(new OnSuccessListener<Void>()
                            {
                                @Override
                                public void onSuccess(Void unused)
                                {
                                    setEventDataToUI(event, UiD);
                                }


                            })
                                    .addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            if ("Event is full".equals(e.getMessage()))
                                            {
                                                Toast.makeText(ViewEventActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(ViewEventActivity.this, "Error, please try again!", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    });
                            fetchEventData();
                        } else if (buttonText.equals("Attendance Information")) {
                            // Handle other button text cases here
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e("ViewEventActivity", "Error fetching event data: " + e.getMessage());
            }
        });

    }


    private void setEventDataToUI(Event event, String UiD)
    {

        //use event object to update all the views
        binding.eventTitleText.setText(event.getName());
        binding.eventDescriptionText.setText(event.getDescription());
        binding.locationTextview.setText(event.getLocation());
        fbUserController.getUser(event.getOrganizerID()).addOnSuccessListener(new OnSuccessListener<User>()
        {
            public void onSuccess(User user)
            {
                if (user != null && user.getUserProfile() != null)
                {
                    //disable this organiser text line if creating new event crashes
                    binding.organiserText.setText(user.getUserProfile().getName());
                }
                else
                {
                    Log.d("halpp", "Document not retrieved, setting default image");
                    binding.organiserText.setText("Unknown");
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Log.e("ViewEventActivity", "Error fetching user data: " + e.getMessage());
            }
        });
        binding.eventTimeText.setText(event.getTimeAsString());
        // Check if the current user is the organizer
        if (UiD.equals(event.getOrganizerID()))
        {
            binding.signUpButton.setText("Attendance Information");
        }
        else
        {
            // Check if the user is signed up for the event
            fbAttendanceController.isUserSignedUp(eventID, UiD).addOnCompleteListener(new OnCompleteListener<Boolean>()
            {
                @Override
                public void onComplete(@NonNull Task<Boolean> task)
                {
                    if (task.isSuccessful())
                    {
                        boolean isSignedUp = task.getResult();
                        if (isSignedUp)
                        {
                            binding.signUpButton.setText("Cancel Sign Up");
                            binding.signUpButton.setEnabled(true);
                            binding.signUpButton.setBackgroundColor(ContextCompat
                                    .getColor(ViewEventActivity.this, R.color.purple_500));
                        }
                        else
                        {
                            if (event.getMaxSpots() != null && event.getMaxSpots() <= event.getTakenSpots())
                            {
                                binding.signUpButton.setText("Event Full");
                                binding.signUpButton.setEnabled(false);
                                binding.signUpButton.setBackgroundColor(Color.GRAY);
                            }
                            else
                            {
                                binding.signUpButton.setText("Sign Up for Event");
                                binding.signUpButton.setEnabled(true);
                                binding.signUpButton.setBackgroundColor(ContextCompat
                                        .getColor(ViewEventActivity.this, R.color.purple_500));
                            }
                        }
                    }
                    else
                    {
                        Log.e(TAG, "Failed at checking sign up", task.getException());
                    }
                }
            });
        }

        //
        fbImageController.downloadImage(event.getImagePath()).addOnCompleteListener(task1 ->
        {
            if (task1.isSuccessful())
            {
                String url = String.valueOf(task1.getResult());
                Picasso.get().load(url).into(binding.eventImageImage);
            }
            else
            {
                Log.d("halppp", "Document not retrieved, setting default image");
                binding.eventImageImage.setImageResource(R.drawable.ic_home_black_24dp);
            }
            loading.setVisibility(View.GONE);
            contentLayout.setVisibility(View.VISIBLE);
        });


        binding.eventTimeText.setText(event.getTimeAsString());

        Log.d("halpp", "Event ID is: " + eventID);


        //just generate the qr code from the event id and set it to the qr code image view
        //and get the image from the firebase storage and set it to the image view
        fbImageController.downloadImage(event.getImagePath()).addOnCompleteListener(task1 ->
        {
            if (task1.isSuccessful())
            {
                String url = String.valueOf(task1.getResult());
                Picasso.get().load(url).into(binding.eventImageImage);
            }
            else
            {
                Log.d("halppp", "Document not retrieved, setting default image");
                binding.eventImageImage.setImageResource(R.drawable.ic_home_black_24dp);
            }
        });

    }


//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e("ViewEventActivity", "Error fetching event data: " + e.getMessage());
//            }
//        });


    private void showQRCodeDialog()
    {
        // Check if the event object is available
        if (event != null)
        {
            // Create and show the QR code dialog fragment
            QRCodeDialogFragment.newInstance(eventID).show(getSupportFragmentManager(), "QRCodeDialogFragment");
        }
    }

//    // Handles The Top Bar menu clicks
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            // Handle the Back button press
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//
//    }

    //generates QR code
    private Bitmap generateQRCode(String text)
    {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try
        {
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /*         Inflate Handle Top Menu Options        */
    // Create the Top Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.viewevent_top_nav_menu, menu);
        return true;
    }

    /*    Handle click events for the Top Menu Bar    */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_attendance_list)
        {// Handle Edit Profile click
            // Handle click
            Toast.makeText(this, "navigation_attendance_list clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (itemId == R.id.navigation_QR_check_in)
        {
            // Handle Click
            Toast.makeText(this, "navigation_QR_check_in clicked", Toast.LENGTH_SHORT).show();

            //todo: implement dialog fragment for check in qr code
            String checkinQrCode = event.getCheckInQrCode();
            Log.d("testerrrr", "checkinQrCode: " + checkinQrCode);

            // Create and show the QR code dialog fragment
            QRCodeDialogFragment.newInstance(checkinQrCode).show(getSupportFragmentManager(), "QRCodeDialogFragment");

            return true;

        }
        else if (itemId == R.id.navigation_QR_promotional)
        {
            // Handle click
            Toast.makeText(this, "navigation_QR_promotional clicked", Toast.LENGTH_SHORT).show();

            //todo: implement dialog fragment for promotional qr code
            String promoQrCode = event.getPromoQrCode();

            Log.d("testerrrr", "promoQRcode: " + promoQrCode);


            // Create and show the QR code dialog fragment
            QRCodeDialogFragment.newInstance(promoQrCode).show(getSupportFragmentManager(), "QRCodeDialogFragment");


            return true;

        }
        else if (itemId == R.id.map)
        {
            // Handle Map click
            Intent intent = new Intent(ViewEventActivity.this, MapActivity.class);
            Bundle bundle = new Bundle();
            // Check if Geolocation is Enabled
            if (!event.getIsGeolocationEnabled())
            {
                Toast.makeText(this, R.string.enable_geolocation, Toast.LENGTH_SHORT).show();
                return true;
            }
            // Add the hashed location
            bundle.putString("geoHash", event.getGeoLocation());
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        }
        else if(itemId == R.id.navigation_delete)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this Event?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton)
                        {
                            fbEventController.deleteEvent(eventID)
                                    .addOnSuccessListener(new OnSuccessListener<Void>()
                                    {
                                        @Override
                                        public void onSuccess(Void aVoid)
                                        {
                                            Toast.makeText(ViewEventActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewEventActivity.this, BrowseEventsActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(ViewEventActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        }
        else if (item.getItemId() == android.R.id.home)
        {
            // Handle the Back button press
            finish();
            return true;
        }
        return false;

    }

    public void confirmCancelSignUp(String UiD, String eventID)
    {
        // Create an AlertDialog
        new AlertDialog.Builder(ViewEventActivity.this)
                .setTitle("Cancel Sign Up")
                .setMessage("Are you sure you want to cancel your sign up?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int yesButton)
                    {
                        fbAttendanceController.removeFromSignUp(UiD, eventID).addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        // Refresh the UI upon successful sign-up cancellation
                                        setEventDataToUI(event, UiD);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        // Display a toast message upon failure
                                        Toast.makeText(ViewEventActivity.this, "Removal failed, please try again!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}

