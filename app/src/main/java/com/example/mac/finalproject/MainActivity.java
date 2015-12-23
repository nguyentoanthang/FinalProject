package com.example.mac.finalproject;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private ParseUser currentUser;
    private ArrayList<Project> listProject = new ArrayList<>();
    private ProjectAdapter adapter = null;
    ListView lvProject = null;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    @Bind(R.id.navigation_view) NavigationView navigationView;
    private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private TextView name, email;
    private ImageView profile_image;
    private View headerView;
    private final String TAG = "TAG";
    private ProjectFragment projectFragment;
    private HeaderFragment headerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        headerView = navigationView.inflateHeaderView(R.layout.drawer_header);
        name = (TextView) headerView.findViewById(R.id.name);
        email = (TextView) headerView.findViewById(R.id.email);
        profile_image = (ImageView) headerView.findViewById(R.id.profile_image);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        currentUser = ParseUser.getCurrentUser();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.project);
        View actionView = item.getActionView();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                showInputDialog();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.logout) {
                    ParseUser.logOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_LOGIN);
                } else if (item.getItemId() == R.id.project) {
                    projectFragment = new ProjectFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, projectFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else if (item.getItemId() == R.id.navigation_item_profile){
                    Bundle bd = new Bundle();
                    bd.putString("txt", "This is data from mainActivity");
                    ProfileFragment profileFragment= new ProfileFragment();
                    profileFragment.setArguments(bd);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });

        registerReceiver(new NetworkStateChange(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        projectFragment = new ProjectFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, projectFragment);
        fragmentTransaction.commit();

        if (currentUser != null) {
            //new PullingData().execute();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }

    private void showInputDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.input_dialog, null);
        final int currentVersion = Build.VERSION.SDK_INT;
        final AlertDialog.Builder alertDialog;
        final EditText projectName;
        if (currentVersion >= 20) {
            alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            alertDialog.setView(dialogView);
            alertDialog.setTitle("Enter project name");
            projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
            projectName.setTextColor(getResources().getColor(R.color.white));
        } else {
            alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setView(dialogView);
            alertDialog.setTitle("Enter project name");
            projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        }



        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseObject newProject = new ParseObject("Project");
                newProject.put("Name", projectName.getText().toString());

                newProject.put("User", currentUser);

                newProject.saveInBackground();

                Project project = new Project();
                project.setName(projectName.getText().toString());
                project.setNumOfDone(0);

                listProject.add(project);


            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    //new PullingData().execute();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profile_image.setImageBitmap(bitmap);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                        e.printStackTrace();
                }
            }

        } else if (requestCode == 2){
            if (resultCode == RESULT_OK) {
                try {
                    InputStream inputStream = MainActivity.this.getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);


                    CheckInternet check = new CheckInternet(MainActivity.this);

                    if (check.isOnline()) {
                        new UploadImage(bitmap).execute();
                    } else {
                        onErrorInternet();
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        switch (id) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Add Photo!");

        builder.setCancelable(false);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }

                else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE_SELECT);
                }

                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private class UploadImage extends AsyncTask<Void, Void, Void> {

        Bitmap bitmap;

        public UploadImage(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Compress image to lower quality scale 1 - 100
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();

            // Create the ParseFile
            ParseFile file = new ParseFile("thang.png", image);
            // Upload the image into Parse Cloud
            file.saveInBackground();

            currentUser.put("avatar", file);
            currentUser.saveInBackground();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            profile_image.setImageBitmap(bitmap);//here set your image
        }
    }

    private class PullingData extends AsyncTask<Void, Void, Void> {

        String _name;
        String _email;
        Bitmap _bitmap;
        boolean image;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            name.setText(_name);
            email.setText(_email);
            if (image == true) {
                profile_image.setImageBitmap(_bitmap);
            } else {
                Toast.makeText(MainActivity.this, "faile to get profile image", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            CheckInternet check = new CheckInternet(MainActivity.this);

            if (check.isOnline()) {
                _name = currentUser.getString("Name");
                _email = currentUser.getEmail();
                ParseFile fileImage = (ParseFile) currentUser.get("avatar");
                fileImage.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            _bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            image = true;
                            publishProgress();
                        } else {
                            image = false;
                            publishProgress();
                        }
                    }
                });
            } else {
                onErrorInternet();
            }

            return null;
        }
    }

    public void onErrorInternet() {
        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
    }
}
