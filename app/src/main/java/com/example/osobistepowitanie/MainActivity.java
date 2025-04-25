package com.example.osobistepowitanie;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        editText = findViewById(R.id.editTextImie);
        button = findViewById(R.id.buttonPrzywitanie);

        button.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "Proszę wpisać swoje imię!", Toast.LENGTH_SHORT).show();
            } else {
                showAlertDialog();
            }
        });


    }

    private void showAlertDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Potwierdzenie");
        builder.setMessage("Cześć " + editText.getText().toString() + "! Czy chcesz otrzymać powiadomienie powitalne?");
        builder.setPositiveButton("Tak, poproszę", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogInterface.OnClickListener context = this; // Lub getApplicationContext()
                String CHANNEL_ID = "DEFAULT_CHANNEL_ID"; // Musi pasować do ID utworzonego kanału
                int notificationId = 101;
                Intent intent = new Intent(MainActivity.this, MainActivity.class); // Otwórz MainActivity
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                int flags = PendingIntent.FLAG_UPDATE_CURRENT;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    flags |= PendingIntent.FLAG_IMMUTABLE; // Dodaj flagę IMMUTABLE jeśli dostępna (API 23+)
                }
                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent, flags);


// 3. Zbuduj powiadomienie używając NotificationCompat.Builder
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.claiste) // OBOWIĄZKOWA mała ikona
                        .setContentTitle("Nowa Wiadomość")
                        .setContentText("Otrzymałeś nową wiadomość od Anny.")
                        .setPriority(NotificationCompat.PRIORITY_HIGH) // Ważność dla Android < 8.0 (dla 8.0+ decyduje kanał)
                        .setContentIntent(pendingIntent) // Akcja po kliknięciu treści
                        .setAutoCancel(true);
// 4. Pobierz NotificationManagerCompat
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                100);
                    }
                }
                    notificationManager.notify(notificationId, builder.build());

            }
        });
        builder.setNegativeButton("Nie, dziękuję", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Kanał Ogólny";
            String description = "Domyślny kanał dla powiadomień";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("DEFAULT_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}