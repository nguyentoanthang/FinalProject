package com.example.mac.finalproject;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v4.app.Fragment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ProjectFragment.OnNewItemClickListener,
                                                               ProjectFragment.OnNewItemLongClickListener,
                                                               WorkFragment.OnNewItemClickListener,
                                                               WorkFragment.OnNewItemLongClick{

    private ParseUser currentUser;
    private ArrayList<Project> listProject = new ArrayList<>();
    private ArrayList<ParseObject> listParseProject = new ArrayList<>();

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.home1) ImageButton homeBtn;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.add) ImageButton addBtn;
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
    private ProfileFragment profileFragment;
    private WorkFragment workFragment;

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

        // get current user
        currentUser = ParseUser.getCurrentUser();

        // Setup toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        title.setText("Project");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
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
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, projectFragment);
                    if (projectFragment.isHide() == true) {
                        fragmentTransaction.addToBackStack(null);
                    }
                    fragmentTransaction.commit();
                } else if (item.getItemId() == R.id.navigation_item_profile) {

                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.frame, profileFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });

        //registerReceiver(new NetworkStateChange(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        projectFragment = new ProjectFragment();
        profileFragment = new ProfileFragment();
        workFragment = new WorkFragment();


        if (currentUser != null) {
            new PullingData().execute();
            new GetProject().execute();
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }


    // Project fragment item select
    public void OnItemPick(int position) {
        new GetWork().execute(position);

        Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
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
                newProject.put("DoneWork", 0);
                CheckInternet check = new CheckInternet(MainActivity.this);
                if (check.isOnline()) {
                    newProject.saveInBackground();
                } else {
                    newProject.saveEventually();
                }

                Project project = new Project();
                project.setName(projectName.getText().toString());
                project.setNumOfDone(0);
                if (projectFragment.isHide() == false) {
                    projectFragment.updateData(project);
                    projectFragment.refreshData();
                } else {
                    projectFragment.updateData(project);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, projectFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
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

    private void showInputDialog(final int index) {
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
                ParseObject newWork = new ParseObject("Work");
                newWork.put("Name", projectName.getText().toString());

                newWork.put("Project", listParseProject.get(index));
                newWork.put("Member", 0);
                CheckInternet check = new CheckInternet(MainActivity.this);
                if (check.isOnline()) {
                    newWork.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                if (workFragment.isHide()) {
                                    new GetWork().execute(index);
                                } else {

                                }
                            }
                        }
                    });
                } else {
                    newWork.saveEventually();
                }

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
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {
                    new PullingData().execute();
                    new GetProject().execute();
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

    private class GetProject extends AsyncTask<Void, Void, Void> {

        ArrayList<Project> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
        }

        @Override
        protected Void doInBackground(Void... params) {

            CheckInternet check = new CheckInternet(MainActivity.this);

            if (check.isOnline()) {
                ParseQuery<ParseObject> project = ParseQuery.getQuery("Project");
                project.whereEqualTo("User", currentUser);
                project.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            int n = objects.size();
                            ParseObject current;
                            for (int i = 0; i < n; i ++) {
                                current = objects.get(i);
                                Project pj = new Project();
                                pj.setName(current.getString("Name"));
                                pj.setNumOfDone(current.getNumber("DoneWork").intValue());
                                list.add(pj);
                                listParseProject.add(current);
                            }
                            publishProgress();
                        } else {

                        }
                    }
                });
            } else {
                onErrorInternet();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            //Toast.makeText(MainActivity.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
            projectFragment.setList(list);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, projectFragment);
            ft.commit();
        }
    }

    public void onErrorInternet() {
        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemLongClick(int position) {
        showChoiceDialog(position);
    }

    public void showChoiceDialog(final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Project");
        builder.setItems(R.array.projectChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        break;
                    case 1: {
                        showInputDialog(index);
                        break;
                    }
                }
                Toast.makeText(MainActivity.this, String.valueOf(which), Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private class GetWork extends AsyncTask<Integer, Void, Void> {
        ArrayList<Work> list = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            workFragment.setList(list);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
            ft.replace(R.id.frame, workFragment);
            ft.addToBackStack(null);
            ft.commit();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Integer... params) {

            int index = params[0];

            CheckInternet check = new CheckInternet(MainActivity.this);
            if (check.isOnline()) {
                ParseQuery<ParseObject> work = ParseQuery.getQuery("Work");
                work.whereEqualTo("Project", listParseProject.get(index));
                work.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            int n = objects.size();
                            ParseObject object;
                            for (int i = 0; i < n; i++) {
                                object = objects.get(i);
                                Work work = new Work();
                                work.setName(object.getString("Name"));
                                list.add(work);
                            }
                            publishProgress();
                        } else {

                        }
                    }
                });

            } else {
                onErrorInternet();

            }

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void OnItemCick(int Position) {

    }

    @Override
    public void OnNewItemLongClick(int position) {
        showChoiceDialogForWork(position);
    }

    public void showChoiceDialogForWork(final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Work");
        builder.setItems(R.array.workChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        break;
                    case 1: {
                        break;
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}

