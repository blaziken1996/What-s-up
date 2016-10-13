package whatsup.connect;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import whatsup.client.Client;
import whatsup.client.LoginTask;

public class LoginActivity extends AppCompatActivity {
    public static final int INTERNET_PERMISSION_CODE = 100;
    private EditText txtServerAddress;
    private EditText txtPortNumber;
    private EditText txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        txtServerAddress = (EditText) findViewById(R.id.serverAddress);
        txtPortNumber = (EditText) findViewById(R.id.portNumber);
        txtName = (EditText) findViewById(R.id.userName);
    }

    public void onClickBtnConnect(View view) {
        askInternetPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LoginTask loginTask = new LoginTask();
        loginTask.execute(txtServerAddress.getText().toString(), txtPortNumber.getText().toString(), txtName.getText().toString());
        Client.mainClient = null;
        try {
            Client.mainClient = loginTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (Client.mainClient != null) {
                Intent intent = new Intent(this, MainActivity.class);
                finish();
                startActivity(intent);
            } else
                Toast.makeText(this, "Unable to connect to server with IP: " + txtServerAddress.getText().toString() + " and port number: " + txtPortNumber.getText().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void askInternetPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.INTERNET}, INTERNET_PERMISSION_CODE);
            }
        }
    }
}

