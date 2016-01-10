package com.example.mac.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.mikepenz.crossfader.Crossfader;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.adapter.BaseDrawerAdapter;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.MiniDrawerItem;
import com.mikepenz.materialdrawer.model.MiniProfileDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.crossfader.util.UIUtils;
import com.mikepenz.materialize.holder.StringHolder;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterCommunication, WorkAdapterCommunication{

    private ParseUser currentUser;
    private ArrayList<ParseObject> listParseProject = new ArrayList<>();
    private ArrayList<Project> listProject = new ArrayList<>();
    private Bitmap currentUserAvatar = null;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.title) TextView title;
    @Bind(R.id.add) ImageButton addBtn;
    private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private final String TAG = "TAG";
    private ProjectFragment projectFragment;
    private ProfileFragment profileFragment;
    private WorkFragment workFragment;
    private CommentFragment commentFragment;
    private Drawer navigation = null;
    private IProfile user;
    private AccountHeader header = null;
    private MiniDrawer miniDrawer = null;
    private Crossfader crossfader = null;
    PrimaryDrawerItem profileDrawerItem;
    PrimaryDrawerItem projectDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // get current user
        currentUser = ParseUser.getCurrentUser();

        // Setup toolbar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

        title.setText("Project");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });

        user = new ProfileDrawerItem().withName("User").withEmail("User@gmail.com").withIcon(R.drawable.profile_image);

        profileDrawerItem = new PrimaryDrawerItem().withName("Profile").withIcon(R.drawable.ic_profile).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {

                if (profileFragment.isHide()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, profileFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }

                return true;
            }
        }).withIdentifier(0);
        projectDrawerItem = new PrimaryDrawerItem().withName("Project").withIcon(R.drawable.project).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                if (projectFragment.isHide()) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, projectFragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
                return true;
            }
        }).withIdentifier(1);
        SecondaryDrawerItem logoutDrawer = new SecondaryDrawerItem().withName("Logout").withIcon(R.drawable.logout).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                ParseUser.logOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                return false;
            }
        });

        buildHeader(false, savedInstanceState);


        // get size of creen to update ui
        if (getInches() > 5.5) {
            navigation = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withActionBarDrawerToggleAnimated(true)
                    .withDisplayBelowStatusBar(false)
                    .withDrawerGravity(Gravity.START)
                    .withSavedInstance(savedInstanceState)
                    .withSelectedItem(1)
                    .withAccountHeader(header)
                    .withGenerateMiniDrawer(true)
                    .addDrawerItems(profileDrawerItem, projectDrawerItem, new DividerDrawerItem(), logoutDrawer)
                    .buildView();

            miniDrawer = navigation.getMiniDrawer();

            int firstWidth = (int) UIUtils.convertDpToPixel(300, this);
            int secondWidth = (int) UIUtils.convertDpToPixel(72, this);

            crossfader = new Crossfader()
                    .withContent(findViewById(R.id.frame))
                    .withFirst(navigation.getSlider(), firstWidth)
                    .withSecond(miniDrawer.build(this), secondWidth)
                    .withSavedInstance(savedInstanceState)
                    .build();

            miniDrawer.withCrossFader(new CrossfadeWrapper(crossfader));

            crossfader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);

        } else {
            navigation = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withDisplayBelowStatusBar(true)
                    .withActionBarDrawerToggleAnimated(true)
                    .withDrawerGravity(Gravity.START)
                    .withSavedInstance(savedInstanceState)
                    .withSelectedItem(1)
                    .withAccountHeader(header)
                    .addDrawerItems(profileDrawerItem, projectDrawerItem, new DividerDrawerItem(), logoutDrawer)
                    .build();

        }

        //registerReceiver(new NetworkStateChange(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        projectFragment = new ProjectFragment();
        profileFragment = new ProfileFragment();
        workFragment = new WorkFragment();
        commentFragment = new CommentFragment();

        if (currentUser != null) {
            ParseInstallation.getCurrentInstallation().put("UserEmail", currentUser.getEmail());
            ParseInstallation.getCurrentInstallation().saveInBackground();
            new GetAllOfData().execute();
            if (currentUser.getInt("Badge") == 0) {
                profileDrawerItem.withBadge("");
                navigation.updateItem(profileDrawerItem);
            } else {
                profileDrawerItem.withBadge(String.valueOf(currentUser.getInt("Badge")));
                navigation.updateItem(profileDrawerItem);
            }

        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }

    private void getDataOfCurrentUser() {
        String name = currentUser.getString("Name");
        String email = currentUser.getEmail();
        String phone = currentUser.getString("PhoneNumber");
        ArrayList<String> listDetail = new ArrayList<>();
        listDetail.add(name);
        listDetail.add(email);
        listDetail.add(phone);
        profileFragment.setData(name, email, currentUserAvatar, listDetail);
    }

    private int getDensity() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;
    }

    private double getInches() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)width/(double)dens;
        double hi=(double)height/(double)dens;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi, 2);
        return Math.sqrt(x+y);
    }

    private void buildHeader(boolean compact, Bundle savedInstanceSate) {
        header = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(compact)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(user)
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile iProfile, boolean b) {
                        selectImage();
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile iProfile, boolean b) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceSate).build();

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
                newProject.put("Work", 0);
                newProject.put("DoneWork", 0);
                listParseProject.add(newProject);

                Project currentP = new Project();
                currentP.setName(projectName.getText().toString());
                currentP.setHost(currentUserAvatar);
                currentP.setNumOfDone(0);
                currentP.setNumOfWork(0);

                CheckInternet check = new CheckInternet(MainActivity.this);

                if (check.isOnline()) {
                    newProject.saveInBackground();
                } else {
                    newProject.saveEventually();
                }

                if (projectFragment.isHide() == false) {
                    projectFragment.updateData(currentP);
                    projectFragment.refreshData();
                } else {
                    projectFragment.updateData(currentP);
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

                ParseObject project = listParseProject.get(index);

                project.increment("Work");

                project.saveInBackground();

                newWork.put("Project", project);
                newWork.put("Member", 0);
                newWork.put("isDone", false);

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
                    ParseInstallation.getCurrentInstallation().put("UserEmail", currentUser.getEmail());
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                    new GetAllOfData().execute();
                    if (currentUser.getInt("Badge") == 0) {
                        profileDrawerItem.withBadge("");
                        navigation.updateItem(profileDrawerItem);
                    } else {
                        profileDrawerItem.withBadge(String.valueOf(currentUser.getInt("Badge")));
                        navigation.updateItem(profileDrawerItem);
                    }

                }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");

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
            case android.R.id.home: {
                if (crossfader != null) {
                    crossfader.crossFade();
                } else {

                }
            }
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
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE_SELECT);
                } else if (options[item].equals("Cancel")) {
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
            user.withIcon(bitmap);
            header.updateProfile(user);
        }
    }

    private void PullingData() {
        CheckInternet check = new CheckInternet(MainActivity.this);

        if (check.isOnline()) {
            user.withName(currentUser.getString("Name"));
            user.withEmail(currentUser.getEmail());
            ParseFile fileImage = (ParseFile) currentUser.get("avatar");
            if (fileImage != null) {
                try {
                    byte[] data = fileImage.getData();
                    currentUserAvatar = BitmapFactory.decodeByteArray(data, 0, data.length);
                } catch (ParseException e) {
                    e.printStackTrace();
                    currentUserAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
                }
            } else {
                currentUserAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
            }
        } else {
            onErrorInternet();
            currentUserAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
        }

        user.withIcon(currentUserAvatar);
        header.updateProfile(user);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState = navigation.saveInstanceState(outState);
        outState = header.saveInstanceState(outState);
        if (crossfader != null) {
            outState = crossfader.saveInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
    }

    private void GetProject() {

        CheckInternet check = new CheckInternet(MainActivity.this);

        if (check.isOnline()) {
            ParseQuery<ParseObject> project = ParseQuery.getQuery("Project");
            project.whereEqualTo("User", currentUser);
            List<ParseObject> objects;
            try {
                objects = project.find();

                ParseObject current;

                int n = objects.size();

                for (int i = 0; i < n; i ++) {
                    current = objects.get(i);
                    Project currentP = new Project();

                    currentP.setName(current.getString("Name"));
                    currentP.setNumOfDone(current.getInt("DoneWork"));
                    currentP.setNumOfWork(current.getInt("Work"));
                    currentP.setHost(currentUserAvatar);

                    listProject.add(currentP);
                    listParseProject.add(current);
                }

                // get project of other user
                List<String> listObId = currentUser.getList("Project");

                if (listObId != null) {
                    int m = listObId.size();

                    for (int i = 0; i < m; i++) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Project");

                        query.whereEqualTo("objectId", listObId.get(i));
                        current = query.find().get(0);

                        Project currentP = new Project();

                        currentP.setName(current.getString("Name"));
                        currentP.setNumOfDone(current.getInt("DoneWork"));
                        currentP.setNumOfWork(current.getInt("Work"));
                        currentP.setHost(getAvatarOfUser(current.getParseUser("User").getObjectId()));
                        listProject.add(currentP);
                        listParseProject.add(current);
                    }
                }

                projectFragment.setList(listProject);


            } catch (ParseException e) {
                e.printStackTrace();
                projectFragment.setList(listProject);
            }
        } else {
            onErrorInternet();
            projectFragment.setList(listProject);

        }

    }

    public void onErrorInternet() {
        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
    }

    public void showChoiceDialog(final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Project");
        builder.setItems(R.array.projectChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        CheckInternet check = new CheckInternet(MainActivity.this);
                        if (check.isOnline()) {
                            new DeleteAllDataOfProject(index).execute();
                        } else {

                        }
                    }
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

    public ArrayList<Work> getAllWorkForProject(ParseObject project) {

        ArrayList<Work> listWork = new ArrayList<>();

        CheckInternet check = new CheckInternet(MainActivity.this);
        if (check.isOnline()) {
            ParseQuery<ParseObject> workQuery = ParseQuery.getQuery("Work");
            workQuery.whereEqualTo("Project", project);
            List<ParseObject> objects;
            try {
                objects = workQuery.find();
                int n = objects.size();
                ParseObject object;
                for (int i = 0; i < n; i++) {
                    object = objects.get(i);
                    Work work = new Work();
                    work.setName(object.getString("Name"));
                    work.setId(object.getObjectId());
                    work.setDone(object.getBoolean("isDone"));
                    work.setDeadLine(object.getDate("DeadLine"));
                    if (object.getList("ListMember") != null) {
                        work.setNumberOfMember(object.getList("ListMember").size());
                        ParseObject p = object.getParseObject("Project");
                        ParseUser u = p.getParseUser("User");
                        if(object.getList("ListMember").contains(currentUser.getEmail())) {
                            work.setForCurrentUser(true);
                            work.setPermission(false);
                        } else if (ParseUser.getCurrentUser().getObjectId() == u.getObjectId()) {
                            work.setForCurrentUser(true);
                            work.setPermission(true);
                        } else {
                            work.setForCurrentUser(false);
                            work.setPermission(false);
                        }
                    } else {
                        work.setNumberOfMember(0);
                        ParseObject p = object.getParseObject("Project");
                        ParseUser u = p.getParseUser("User");
                        if (ParseUser.getCurrentUser().getObjectId() == u.getObjectId()) {
                            work.setForCurrentUser(true);
                            work.setPermission(true);
                        } else {
                            work.setForCurrentUser(false);
                            work.setPermission(false);
                        }
                    }

                    listWork.add(work);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else {
            onErrorInternet();
        }

        return listWork;

    }

    private class GetWork extends AsyncTask<Integer, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            workFragment.setList(new ArrayList<Work>());
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
            ft.replace(R.id.frame, workFragment);
            ft.addToBackStack(null);
            ft.commit();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            workFragment.refreshData();
        }

        @Override
        protected Void doInBackground(Integer... params) {

            int index = params[0];
            workFragment.updateData(getAllWorkForProject(listParseProject.get(index)));

            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void showPermissionDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Permission");
        alert.setMessage("You have no permission to do that");
        alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialogAlert = alert.create();
        dialogAlert.show();
    }

    public void showChoiceDialogForWork(final Work w) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Work");
        builder.setItems(R.array.workChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        if (w.isPermission()) {
                            showInputEmailDialog(w.getId());
                        } else {
                            showPermissionDialog();
                        }
                    }
                        break;
                    case 1: {
                        if (w.isPermission()) {

                        } else {
                            showPermissionDialog();
                        }
                        break;
                    }
                    case 2: {
                        commentFragment.setData(getAllCommentForWork(w.getId()), w);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
                        ft.replace(R.id.frame, commentFragment);
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void Callback(View v, int index) {
        if (v.getId() == R.id.choice) {
            showChoiceDialog(index);
        } else if(v.getId() == R.id.arrow) {
            new GetWork().execute(index);
        }

    }

    @Override
    public void WorkCallBack(View v, Work w) {
        if (v.getId() == R.id.choice) {
            showChoiceDialogForWork(w);
        }

    }

    private ArrayList<Comment> getAllCommentForWork(String workId) {

        ParseQuery<ParseObject> cmt = ParseQuery.getQuery("Comment");
        cmt.whereEqualTo("forWork", workId);
        List<ParseObject> objects;
        ArrayList<Comment> list = new ArrayList<>();
        try {
            objects = cmt.find();

            ParseUser user;
            int n = objects.size();
            for (int i = 0; i < n; i++) {
                Comment newComment = new Comment();
                ParseObject parseObjectComment = objects.get(i);
                newComment.setCmt(parseObjectComment.getString("Comment"));
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("objectId", parseObjectComment.getString("ofUser"));
                try {
                    user = query.find().get(0);
                    newComment.setName(user.getString("Name"));
                    ParseFile fileImage = (ParseFile) user.get("avatar");
                    if (fileImage != null) {
                        try {
                            byte[] data = fileImage.getData();
                            newComment.setHost(BitmapFactory.decodeByteArray(data, 0, data.length));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            newComment.setHost(BitmapFactory.decodeResource(getResources(), R.drawable.profile_image));
                        }
                    } else {
                        newComment.setHost(BitmapFactory.decodeResource(getResources(), R.drawable.profile_image));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                list.add(newComment);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    private class DeleteAllDataOfProject extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                                            R.style.AppTheme_Dark_Dialog);
        private int index;

        public DeleteAllDataOfProject(int index) {
            this.index = index;
        }

        @Override
        protected void onPostExecute(Void parseObject) {
            super.onPostExecute(parseObject);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Deleting...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ParseQuery<ParseObject> work = ParseQuery.getQuery("Work");
            work.whereEqualTo("Project", listParseProject.get(index));
            work.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    ParseObject.deleteAllInBackground(objects, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                listParseProject.get(index).deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {

                                        if (e == null) {
                                            progressDialog.dismiss();

                                            listParseProject.remove(index);
                                            projectFragment.removeItemAtIndex(index);
                                            projectFragment.refreshData();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });

            return null;
        }
    }

    private void showInputEmailDialog(final String workId) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.input_dialog, null);
        final int currentVersion = Build.VERSION.SDK_INT;
        final AlertDialog.Builder alertDialog;
        final EditText projectName;
        if (currentVersion >= 20) {
            alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            alertDialog.setView(dialogView);
            alertDialog.setTitle("Enter Email");
            projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
            projectName.setTextColor(getResources().getColor(R.color.white));
        } else {
            alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setView(dialogView);
            alertDialog.setTitle("Enter Email");
            projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        }

        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereEqualTo("UserEmail", projectName.getText().toString());
                JSONObject data = new JSONObject();
                try {
                    data.put("title", "Invite");
                    data.put("alert", currentUser.getEmail() + " want to invite you to their project");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ParseObject notify = new ParseObject("Notification");
                notify.put("User", projectName.getText().toString());
                notify.put("forWork", workId);

                try {
                    notify.put("Message", data.getString("alert"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                notify.saveInBackground();
                // Send push notification to query
                ParsePush push = new ParsePush();
                push.setQuery(pushQuery); // Set our Installation query
                push.setData(data);
                push.sendInBackground();
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

    public Bitmap getAvatarOfUser(String userId) {

        ParseUser user;
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", userId);

        try {
            user = query.find().get(0);
            ParseFile imageFile = (ParseFile) user.get("avatar");
            if (imageFile != null) {
                try {
                    byte[] data = imageFile.getData();
                    return BitmapFactory.decodeByteArray(data, 0, data.length);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
                }
            } else {
                return BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
    }

    private class GetAllOfData extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            PullingData();
            GetProject();
            getDataOfCurrentUser();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, projectFragment);
            ft.commit();
        }
    }
}

