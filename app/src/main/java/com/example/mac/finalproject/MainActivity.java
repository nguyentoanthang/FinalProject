package com.example.mac.finalproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
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
import com.mikepenz.crossfader.util.UIUtils;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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
    //
    // @Bind(R.id.menu) ImageButton menuBtn;
    private static final int REQUEST_LOGIN = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private final String TAG = "TAG";
    private ProjectFragment projectFragment;
    private ProfileFragment profileFragment;
    private WorkFragment workFragment;
    private CommentFragment commentFragment;
    private ProjectDetail projectDetail;
    private WorkDetail workDetail;
    private MemberFragment memberFragment;
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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
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
                projectDrawerItem.withSetSelected(false);
                if (profileFragment.isHide()) {
                    addBtn.setVisibility(View.GONE);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, profileFragment);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    ft.commit();
                }

                return true;
            }
        }).withIdentifier(0).withSelectedIcon(R.drawable.click_icon);
        projectDrawerItem = new PrimaryDrawerItem().withName("Project").withIcon(R.drawable.project).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                profileDrawerItem.withSetSelected(false);
                addBtn.setVisibility(View.VISIBLE);
                if (projectFragment.isHide()) {
                    if (!profileFragment.isLoading()) {
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame, projectFragment);
                        ft.commit();
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    } else {
                        profileFragment.setLoading(false);
                        showAlertDialogForReload();
                        listProject.clear();
                        listParseProject.clear();
                    }
                }
                return true;
            }
        }).withIdentifier(1).withSelectedIcon(R.drawable.click_icon1);
        SecondaryDrawerItem logoutDrawer = new SecondaryDrawerItem().withName("Logout").withIcon(R.drawable.logout).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                ParseUser.logOut();
                ParseInstallation.getCurrentInstallation().put("User", "");
                ParseInstallation.getCurrentInstallation().saveInBackground();
                listProject.clear();
                listParseProject.clear();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                return false;
            }
        });

        buildHeader(false, savedInstanceState);

        Toast.makeText(MainActivity.this, String.valueOf(getDensity()), Toast.LENGTH_LONG).show();
        Toast.makeText(MainActivity.this, String.valueOf(getInches()), Toast.LENGTH_LONG).show();

        // get size of creen to update ui
        if (getInches() > 5.5) {
            navigation = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
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

            miniDrawer.getDrawerAdapter().removeDrawerItem(1);
            miniDrawer.getDrawerAdapter().removeDrawerItem(1);
            miniDrawer.getDrawerAdapter().addDrawerItem(profileDrawerItem);
            miniDrawer.getDrawerAdapter().addDrawerItem(projectDrawerItem);


            //miniDrawer.updateItem(3);

        } else {
            navigation = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withDisplayBelowStatusBar(true)
                    .withDrawerGravity(Gravity.START)
                    .withSavedInstance(savedInstanceState)
                    .withSelectedItem(1)
                    .withGenerateMiniDrawer(true)
                    .withAccountHeader(header)
                    .addDrawerItems(profileDrawerItem, projectDrawerItem, new DividerDrawerItem(), logoutDrawer)
                    .buildView();

            int firstWidth = (int) UIUtils.convertDpToPixel(250, this);
            int secondWidth = (int) UIUtils.convertDpToPixel(0, this);


            miniDrawer = navigation.getMiniDrawer();
            crossfader = new Crossfader()
                    .withContent(findViewById(R.id.frame))
                    .withFirst(navigation.getSlider(), firstWidth)
                    .withSecond(miniDrawer.build(this), secondWidth)
                    .withSavedInstance(savedInstanceState)
                    .build();

            miniDrawer.withCrossFader(new CrossfadeWrapper(crossfader));

            crossfader.getCrossFadeSlidingPaneLayout().setShadowResourceLeft(R.drawable.material_drawer_shadow_left);

        }
        //navigation.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
        //registerReceiver(new NetworkStateChange(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        projectFragment = new ProjectFragment();
        profileFragment = new ProfileFragment();
        workFragment = new WorkFragment();
        commentFragment = new CommentFragment();
        projectDetail = new ProjectDetail();
        workDetail = new WorkDetail();
        memberFragment = new MemberFragment();

        if (currentUser != null) {

            CheckInternet checkInternet = new CheckInternet(MainActivity.this);
            if (checkInternet.isOnline()) {
                new GetAllOfData().execute();
            } else {
                onErrorInternet();
                PullingDataWithoutInternet();
                header.updateProfile(user);
            }


        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN);
        }
    }

    private void showAlertDialogForReload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Reload");
        builder.setMessage("You just accept that project, click ok to reload");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                new GetAllOfData().execute();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
                .withHeaderBackground(R.drawable.background)
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
        final AlertDialog.Builder alertDialog;
        final EditText projectName;
        alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(dialogView);
        alertDialog.setTitle("Enter project name");
        projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        projectName.setHint("Project name");


        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseObject newProject = new ParseObject("Project");
                newProject.put("Name", projectName.getText().toString());

                newProject.put("User", currentUser);
                newProject.put("Work", 0);
                newProject.put("DoneWork", 0);
                newProject.put("Member", 1);
                newProject.put("Notification", 0);
                newProject.put("Finish", new Date());
                listParseProject.add(newProject);

                Project currentP = new Project();
                currentP.setName(projectName.getText().toString());
                currentP.setHost(currentUserAvatar);
                currentP.setNumOfDone(0);
                currentP.setNumOfWork(0);
                currentP.setNumOfMember(0);
                currentP.setPermission(true);

                CheckInternet check = new CheckInternet(MainActivity.this);

                if (check.isOnline()) {
                    newProject.saveInBackground();
                } else {
                    newProject.pinInBackground("Project");
                    newProject.saveEventually();
                }

                if (projectFragment.isHide() == false) {
                    projectFragment.updateData(currentP);
                    projectFragment.refreshData();
                } else {
                    projectFragment.updateData(currentP);
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame, projectFragment);
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
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
        final AlertDialog.Builder alertDialog;
        final EditText projectName;

        alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(dialogView);
        alertDialog.setTitle("Enter Task name");
        projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        projectName.setHint("Task name");


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
                newWork.addUnique("ListMember", currentUser.getEmail());
                newWork.addUnique("LNo", currentUser.getEmail());
                newWork.put("Comment", false);
                newWork.put("DeadLine", new Date());

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

                Project p = listProject.get(index);
                int numofwork = p.getNumOfWork() + 1;
                p.setNumOfWork(numofwork);
                listProject.set(index, p);

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

                    if (profileFragment.isHide() == false || projectFragment.isHide() == false) {
                        crossfader.crossFade();
                        profileFragment.popupWindow.dismiss();
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
                    } else {
                        onBackPressed();
                    }
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
            currentUserAvatar = bitmap;
            user.withIcon(bitmap);
            header.updateProfile(user);

            for (int i = 0; i < listProject.size(); i++) {
                if (listProject.get(i).isPermission()) {
                    listProject.get(i).setHost(currentUserAvatar);
                }
            }

            profileFragment.updateAvatar(currentUserAvatar);
            projectFragment.setList(listProject);
            projectFragment.refreshData();
        }
    }

    private void PullingDataWithoutInternet() {
        user.withName(currentUser.getString("Name"));
        user.withEmail(currentUser.getEmail());

        currentUserAvatar = BitmapFactory.decodeResource(getResources(), R.drawable.profile_image);
        user.withIcon(currentUserAvatar);
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
                    currentP.setPermission(true);

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
                        currentP.setPermission(false);
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
        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
    }

    public void showChoiceDialog(final int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Project");
        builder.setItems(R.array.projectChoice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {

                        if (listProject.get(index).isPermission()) {
                            CheckInternet check = new CheckInternet(MainActivity.this);
                            if (check.isOnline()) {
                                showYesNoDelete(index);
                            } else {

                            }
                        } else {
                            showAlertDialog("Permission", "You have no permission to do that");
                        }

                    }
                    break;
                    case 1: {
                        if (listProject.get(index).isPermission()) {
                            showInputDialog(index);
                        } else {
                            showAlertDialog("Permission", "You have no permission to do that");
                        }

                        break;
                    }
                    case 2: {
                        //boolean permission = ParseUser.getCurrentUser().getObjectId().equals(listParseProject.get(index).getParseUser("User").getObjectId());
                        projectDetail.setData(getDetailOfProject(listParseProject.get(index)), listParseProject.get(index));
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
                        ft.replace(R.id.frame, projectDetail);
                        ft.addToBackStack(null);
                        ft.commit();
                        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
                        addBtn.setVisibility(View.GONE);
                        break;
                    }
                }
                //Toast.makeText(MainActivity.this, String.valueOf(which), Toast.LENGTH_SHORT).show();
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

                    List<String> l = object.getList("listNotify");

                    if (l != null && l.contains(ParseUser.getCurrentUser().getEmail())) {
                        work.setComment(true);
                    } else {
                        work.setComment(false);
                    }

                    work.setDescription(object.getString("Description"));
                    if (object.getList("ListMember") != null) {
                        work.setNumberOfMember(object.getList("ListMember").size());
                        ParseUser u = project.getParseUser("User");
                        if (ParseUser.getCurrentUser().getObjectId().equals(u.getObjectId())) {
                            work.setForCurrentUser(true);
                            work.setPermission(true);
                        } else if(object.getList("ListMember").contains(currentUser.getEmail())) {
                            work.setForCurrentUser(true);
                            work.setPermission(false);
                        } else {
                            work.setForCurrentUser(false);
                            work.setPermission(false);
                        }
                    } else {
                        work.setNumberOfMember(0);

                        ParseUser u = project.getParseUser("User");
                        if (ParseUser.getCurrentUser().getObjectId().equals(u.getObjectId())) {
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
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
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
        if (profileFragment.isHide() == false) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            addBtn.setVisibility(View.GONE);
        }

        if (projectFragment.isHide() == false) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            addBtn.setVisibility(View.VISIBLE);
        }
        if (workFragment.isHide() == false) {
            workFragment.getComment();
            workFragment.refreshData();
        }
    }


    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
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
        if (ParseUser.getCurrentUser().getBoolean("Notification") == true) {
            builder.setItems(R.array.workChoice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: {
                            if (w.isPermission()) {
                                showInputEmailDialog(w.getId());
                            } else {
                                showAlertDialog("Permission", "You have no permission to do that");
                            }
                        }
                        break;
                        case 1: {
                            if (w.isPermission()) {

                                new DeleteAllDataForWork(w).execute();
                            } else {
                                showAlertDialog("Permission", "You have noo permission to do that");
                            }
                            break;
                        }
                        case 2: {
                            new LoadingComment(w).execute();
                            break;
                        }
                        case 3: {
                            new GetDetailOFWork(w).execute();
                            break;
                        }
                        case 4: {
                            if (w.isForCurrentUser()) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
                                query.whereEqualTo("objectId", w.getId());
                                ParseObject object = null;
                                try {
                                    object = query.find().get(0);

                                    if (object.getParseObject("Project").getParseUser("User").getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                        if (object.getBoolean("isDone") == true) {
                                            showAlertDialog(w.getName(), "It is done!");
                                        } else {
                                            List<String> l = object.getList("ListMember");
                                            l.remove(ParseUser.getCurrentUser().getEmail());

                                            Collection<String> list_member = l;
                                            ParseQuery pushQuery = ParseInstallation.getQuery();
                                            pushQuery.whereContainedIn("UserEmail", list_member);
                                            JSONObject data = new JSONObject();
                                            try {
                                                data.put("title", "One task is done");
                                                data.put("alert", "The admin" +
                                                        " mark the task " + w.getName() + " as done.");
                                                data.put("sender", ParseUser.getCurrentUser().getObjectId());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            ParseObject notify = new ParseObject("Notification");
                                            notify.put("Title", "Done");
                                            notify.put("forWork", w.getId());
                                            notify.addAllUnique("User", list_member);

                                            try {
                                                notify.put("Message", data.getString("alert"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            notify.saveInBackground();
                                            ParsePush push = new ParsePush();
                                            push.setData(data);
                                            push.setQuery(pushQuery);
                                            push.sendInBackground();
                                            object.put("isDone", true);
                                            object.saveInBackground();
                                        }
                                    } else {
                                        if (object.getBoolean("isDone") == true) {
                                            showAlertDialog(w.getName(), "It is done!");
                                        } else {

                                            List<String> l = object.getList("ListMember");
                                            l.remove(ParseUser.getCurrentUser().getEmail());

                                            Collection<String> list_member = l;
                                            ParseQuery pushQuery = ParseInstallation.getQuery();
                                            pushQuery.whereContainedIn("UserEmail", list_member);
                                            JSONObject data = new JSONObject();
                                            try {
                                                data.put("title", "One task is done");
                                                data.put("alert", ParseUser.getCurrentUser().getEmail() +
                                                        " mark the task " + w.getName() + " as done.");
                                                data.put("sender", ParseUser.getCurrentUser().getObjectId());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            ParseObject notify = new ParseObject("Notification");
                                            notify.put("Title", "Done");
                                            notify.put("forWork", w.getId());
                                            notify.addAllUnique("User", list_member);

                                            try {
                                                notify.put("Message", data.getString("alert"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            notify.saveInBackground();
                                            ParsePush push = new ParsePush();
                                            push.setData(data);
                                            push.setQuery(pushQuery);
                                            push.sendInBackground();

                                        }
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showAlertDialog("Permission", "You have no permission to do that");
                            }
                            break;
                        }
                        case 5: {
                            if (w.isForCurrentUser()) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
                                query.whereEqualTo("objectId", w.getId());
                                ParseObject object = null;

                                try {
                                    object = query.find().get(0);

                                    List<String> list = new ArrayList<>();
                                    list.add(ParseUser.getCurrentUser().getEmail());
                                    Collection<String> l = list;
                                    object.removeAll("LNo", l);
                                    object.saveInBackground();
                                    ParseUser.getCurrentUser().put("Notification", false);
                                    ParseUser.getCurrentUser().saveInBackground();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showAlertDialog("Alert", "You are not member in this task");
                            }
                            break;
                        }
                        case 6: {

                            memberFragment.setData(getAllMemberForWork(w.getId()), w);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
                            ft.replace(R.id.frame, memberFragment);
                            ft.addToBackStack(null);
                            ft.commit();
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
                            break;
                        }
                    }
                }
            });
        } else {
            builder.setItems(R.array.workChoice1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: {
                            if (w.isPermission()) {
                                showInputEmailDialog(w.getId());
                            } else {
                                showAlertDialog("Permission", "You have no permission to do that");
                            }
                        }
                        break;
                        case 1: {
                            if (w.isPermission()) {

                                new DeleteAllDataForWork(w).execute();
                            } else {
                                showAlertDialog("Permission", "You have noo permission to do that");
                            }
                            break;
                        }
                        case 2: {
                            new LoadingComment(w).execute();
                            break;
                        }
                        case 3: {
                            new GetDetailOFWork(w).execute();
                            break;
                        }
                        case 4: {
                            if (w.isForCurrentUser()) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
                                query.whereEqualTo("objectId", w.getId());
                                ParseObject object = null;
                                try {
                                    object = query.find().get(0);

                                    if (object.getParseObject("Project").getParseUser("User").getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                                        if (object.getBoolean("isDone") == true) {
                                            showAlertDialog(w.getName(), "It is done!");
                                        } else {
                                            List<String> l = object.getList("ListMember");
                                            l.remove(ParseUser.getCurrentUser().getEmail());

                                            Collection<String> list_member = l;
                                            ParseQuery pushQuery = ParseInstallation.getQuery();
                                            pushQuery.whereContainedIn("UserEmail", list_member);
                                            JSONObject data = new JSONObject();
                                            try {
                                                data.put("title", "One task is done");
                                                data.put("alert", "The admin" +
                                                        " mark the task " + w.getName() + " as done.");
                                                data.put("sender", ParseUser.getCurrentUser().getObjectId());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            ParseObject notify = new ParseObject("Notification");
                                            notify.put("Title", "Done");
                                            notify.put("forWork", w.getId());
                                            notify.addAllUnique("User", list_member);

                                            try {
                                                notify.put("Message", data.getString("alert"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            notify.saveInBackground();
                                            ParsePush push = new ParsePush();
                                            push.setData(data);
                                            push.setQuery(pushQuery);
                                            push.sendInBackground();
                                            object.put("isDone", true);
                                            object.saveInBackground();
                                        }
                                    } else {
                                        if (object.getBoolean("isDone") == true) {
                                            showAlertDialog(w.getName(), "It is done!");
                                        } else {

                                            List<String> l = object.getList("ListMember");
                                            l.remove(ParseUser.getCurrentUser().getEmail());

                                            Collection<String> list_member = l;
                                            ParseQuery pushQuery = ParseInstallation.getQuery();
                                            pushQuery.whereContainedIn("UserEmail", list_member);
                                            JSONObject data = new JSONObject();
                                            try {
                                                data.put("title", "One task is done");
                                                data.put("alert", ParseUser.getCurrentUser().getEmail() +
                                                        " mark the task " + w.getName() + " as done.");
                                                data.put("sender", ParseUser.getCurrentUser().getObjectId());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            ParseObject notify = new ParseObject("Notification");
                                            notify.put("Title", "Done");
                                            notify.put("forWork", w.getId());
                                            notify.addAllUnique("User", list_member);

                                            try {
                                                notify.put("Message", data.getString("alert"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            notify.saveInBackground();
                                            ParsePush push = new ParsePush();
                                            push.setData(data);
                                            push.setQuery(pushQuery);
                                            push.sendInBackground();

                                        }
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showAlertDialog("Permission", "You have no permission to do that");
                            }
                            break;
                        }
                        case 5: {
                            if (w.isForCurrentUser()) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
                                query.whereEqualTo("objectId", w.getId());
                                ParseObject object = null;

                                try {
                                    object = query.find().get(0);
                                    object.addUnique("LNo", ParseUser.getCurrentUser().getEmail());
                                    object.saveInBackground();
                                    ParseUser.getCurrentUser().put("Notification", true);
                                    ParseUser.getCurrentUser().saveInBackground();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showAlertDialog("Alert", "You are not member in this task");
                            }
                            break;
                        }
                        case 6: {

                            memberFragment.setData(getAllMemberForWork(w.getId()), w);
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
                            ft.replace(R.id.frame, memberFragment);
                            ft.addToBackStack(null);
                            ft.commit();
                            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
                            break;
                        }
                    }
                }
            });
        }


        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void Callback(View v, int index) {
        if (v.getId() == R.id.choice) {
            showChoiceDialog(index);
        } else if(v.getId() == R.id.arrow) {
            addBtn.setVisibility(View.GONE);
            new GetWork().execute(index);
        } else {

            if (listParseProject.get(index).getParseUser("User").getEmail().equals(currentUser.getEmail())) {
                //showAlertDialog("Nothing to do", "He is you?");
            } else {
                showChoiceDialogForContact(index);
            }
        }

    }

    @Override
    public void WorkCallBack(View v, Work w) {
        if (v.getId() == R.id.choice) {
            showChoiceDialogForWork(w);
        }

    }

    private ArrayList<Member> getAllMemberForWork(String workId) {
        ParseQuery<ParseObject> m = ParseQuery.getQuery("Work");
        m.whereEqualTo("objectId", workId);
        ParseObject ob;
        ArrayList<Member> list = new ArrayList<>();

        try {
            ob = m.find().get(0);
            List<String> listMember = ob.getList("ListMember");
            Collection<String> c = listMember;
            ParseQuery<ParseUser> u = ParseUser.getQuery();
            u.whereContainedIn("email", c);

            List<ParseUser> parseUsers = u.find();

            int n = parseUsers.size();
            for (int i = 0; i < n; i++) {
                Member newMember = new Member();
                newMember.setEmail(parseUsers.get(i).getEmail());
                newMember.setName(parseUsers.get(i).getString("Name"));
                newMember.setPhone(parseUsers.get(i).getString("PhoneNumber"));
                newMember.setAvatar(getAvatarOfUser(parseUsers.get(i).getObjectId()));

                list.add(newMember);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private class LoadingComment extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        ArrayList<Comment> list = new ArrayList<>();
        Work word;

        public LoadingComment(Work word) {
            this.word = word;
        }

        @Override
        protected Void doInBackground(Void... params) {
            list = getAllCommentForWork(word.getId());
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading comment...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            commentFragment.setData(list, word);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
            ft.replace(R.id.frame, commentFragment);
            ft.addToBackStack(null);
            ft.commit();
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            progressDialog.dismiss();
        }
    }

    private ArrayList<Comment> getAllCommentForWork(String workId) {

        ParseQuery<ParseObject> workQuery = ParseQuery.getQuery("Work");
        workQuery.whereEqualTo("objectId", workId);
        ParseObject object;
        ParseQuery<ParseObject> cmt = ParseQuery.getQuery("Comment");
        cmt.whereEqualTo("forWork", workId);
        List<ParseObject> objects;
        ArrayList<Comment> list = new ArrayList<>();
        try {
            object = workQuery.find().get(0);

            List<String> l = object.getList("listNotify");

            if (l != null && l.contains(ParseUser.getCurrentUser().getEmail())) {
                List<String> email = new ArrayList<>();
                email.add(ParseUser.getCurrentUser().getEmail());
                Collection<String> c = email;
                object.removeAll("listNotify", c);
            } else {

            }
            object.saveInBackground();
            objects = cmt.find();

            ParseUser user;
            int n = objects.size();
            for (int i = 0; i < n; i++) {
                Comment newComment = new Comment();
                ParseObject parseObjectComment = objects.get(i);
                newComment.setCmt(parseObjectComment.getString("Comment"));
                long second = ((new Date().getTime()) - parseObjectComment.getCreatedAt().getTime())/1000;
                newComment.setTime(second);
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

    private void showYesNoDelete(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new DeleteAllDataOfProject(index).execute();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
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
            listParseProject.remove(index);
            projectFragment.removeItemAtIndex(index);
            projectFragment.refreshData();
            progressDialog.dismiss();
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

            List<ParseObject> ws;

            try {
                ws = work.find();
                int n = ws.size();
                for (int i = 0; i < n; i++) {
                    deleteAllCommentForWork(ws.get(i));
                }
                ParseObject.deleteAll(ws);
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.whereEqualTo("Project", listParseProject.get(index).getObjectId());
                List<ParseUser> listU;
                listU = query.find();
                int m = listU.size();
                List<String> listEmail = new ArrayList<>();
                for (int i = 0; i < m; i++) {
                    listEmail.add(listU.get(i).getEmail());
                }

                Collection<String> list_member = listEmail;
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereContainedIn("UserEmail", list_member);

                JSONObject data = new JSONObject();
                try {
                    data.put("title", "Delete");
                    data.put("alert", "I delete a project");
                    data.put("id", listParseProject.get(index).getObjectId());
                    data.put("sender", ParseUser.getCurrentUser().getObjectId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ParsePush push = new ParsePush();
                push.setData(data);
                push.setQuery(pushQuery);
                push.sendInBackground();

                listParseProject.get(index).delete();


            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void showInputEmailDialog(final String workId) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View dialogView = layoutInflater.inflate(R.layout.input_dialog, null);
        final AlertDialog.Builder alertDialog;
        final EditText projectName;

        alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(dialogView);
        alertDialog.setTitle("Enter Email");
        projectName = (EditText) dialogView.findViewById(R.id.input_projectname);
        projectName.setHint("Email");

        alertDialog.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if (ParseUser.getCurrentUser().getUsername().equals(projectName.getText().toString())) {
                    showAlertDialog("Nothing to do", "it you?");
                } else {
                    ParseQuery<ParseUser> user = ParseUser.getQuery();
                    user.whereEqualTo("username", projectName.getText().toString());
                    try {
                        List<ParseUser> l = user.find();
                        if (l.size() != 0) {
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
                            query.whereEqualTo("objectId", workId);
                            ParseObject object;

                            object = query.find().get(0);
                            if (!object.getList("ListMember").contains(projectName.getText().toString())) {
                                ParseQuery pushQuery = ParseInstallation.getQuery();
                                pushQuery.whereEqualTo("UserEmail", projectName.getText().toString());
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("title", "Invite");
                                    data.put("sender", ParseUser.getCurrentUser().getObjectId());
                                    data.put("alert", currentUser.getEmail() + " want to invite you to their project");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ParseObject notify = new ParseObject("Notification");
                                notify.addUnique("User", projectName.getText().toString());
                                notify.put("forWork", workId);
                                notify.put("Title", "Invite");

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
                            } else {
                                showAlertDialog("Already member", "That user is already member for this task");
                            }
                        } else {
                            showAlertDialog("No user", "No user match that email");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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

    private class GetAllofDataWithoutInternet extends AsyncTask<Void, Void, Void> {
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
            PullingDataWithoutInternet();
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            header.updateProfile(user);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
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
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            header.updateProfile(user);
            if (currentUser.getInt("Badge") == 0) {
                profileDrawerItem.withBadge("");
                navigation.updateItem(profileDrawerItem);
            } else {
                profileDrawerItem.withBadge(String.valueOf(currentUser.getInt("Badge")));
                navigation.updateItem(profileDrawerItem);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame, projectFragment);
            ft.commit();
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private ArrayList<String> getDetailOfProject(ParseObject object) {


        ArrayList<String> list = new ArrayList<>();
        try {
            object.fetch();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            list.add(object.getString("Name"));
            list.add(object.getString("Description"));
            list.add(String.valueOf(object.getInt("Member")));
            list.add(String.valueOf(object.getInt("Work")));
            list.add(String.valueOf(object.getInt("DoneWork")));
            list.add(sdf.format(object.getCreatedAt()));
            list.add(sdf.format(object.getDate("Finish")));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return list;
    }

    private class GetDetailOFWork extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        Work w;
        ArrayList<String> list = new ArrayList<>();
        ParseObject work;

        public GetDetailOFWork(Work w) {
            this.w = w;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
            query.whereEqualTo("objectId", w.getId());
            try {
                work = query.find().get(0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
                list.add(work.getString("Name"));
                list.add(work.getString("Description"));
                list.add(String.valueOf(work.getInt("Member")));
                if (work.getBoolean("isDone") == true) {
                    list.add("Yes");
                } else {
                    list.add("No");
                }
                list.add(sdf.format(work.getCreatedAt()));
                list.add(sdf.format(work.getDate("DeadLine")));


            } catch (ParseException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            workDetail.setData(list, w.isForCurrentUser(), work);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.pop_in, R.anim.pop_out);
            ft.replace(R.id.frame, workDetail);
            ft.addToBackStack(null);
            ft.commit();
            addBtn.setVisibility(View.GONE);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            progressDialog.dismiss();
        }
    }

    private ArrayList<String> getDetailOfWork(String id) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Work");
        query.whereEqualTo("objectId", id);
        ArrayList<String> list = new ArrayList<>();
        ParseObject object = null;
        try {
            object = query.find().get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");
            list.add(object.getString("Name"));
            list.add(object.getString("Description"));
            list.add(String.valueOf(object.getInt("Member")));
            if (object.getBoolean("isDone") == true) {
                list.add("Yes");
            } else {
                list.add("No");
            }
            list.add(sdf.format(object.getCreatedAt()));
            list.add(sdf.format(object.getDate("DeadLine")));


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void getProjectWithoutInternet() {
        ParseQuery<ParseObject> project = ParseQuery.getQuery("Project");
        project.fromPin("Project");
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
    }

    private void deleteAllCommentForWork(ParseObject work) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comment");
        query.whereEqualTo("forWork", work.getObjectId());

        List<ParseObject> cmts;

        try {
            cmts = query.find();
            ParseObject.deleteAll(cmts);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showChoiceDialogForContact(final int index) {
        final String phone = listParseProject.get(index).getString("PhoneNumber");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Contact");
        builder.setIcon(R.drawable.contact);
        builder.setItems(R.array.Contact1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        if (isSimSupport(MainActivity.this)) {
                            try {
                                if (!phone.equals("")) {
                                    Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                                    my_callIntent.setData(Uri.parse("tel:" + phone));
                                    startActivity(my_callIntent);
                                } else {
                                    showAlertDialog("No phone number", "He hasn't fill his phone number yet");
                                }

                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error in your phone call" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "This device doesn't support sim, you can send email", Toast.LENGTH_LONG).show();
                        }

                    }
                    break;
                    case 1: {
                        if (isSimSupport(MainActivity.this)) {
                            try {
                                if (!phone.equals("")) {
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.putExtra("address", phone);
                                    startActivity(smsIntent);
                                } else {
                                    showAlertDialog("No phone number", "He hasn't fill his phone number yet");
                                }

                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Error in your sms " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "This device doesn't support sim, you can send email", Toast.LENGTH_LONG).show();
                        }
                        break;
                    }
                    case 2: {
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", listParseProject.get(index).getParseUser("User").getEmail(), null));
                        startActivity(emailIntent);
                        break;
                    }
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
        return (tm.getSimState() == TelephonyManager.SIM_STATE_READY);
    }

    private class DeleteAllDataForWork extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;
        private Work work;

        public DeleteAllDataForWork(Work w) {
            this.work = w;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            workFragment.removeItem(work);
            workFragment.refreshData();
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ParseQuery<ParseObject> objectParseQuery = ParseQuery.getQuery("Work");
            objectParseQuery.whereEqualTo("objectId", work.getId());
            ParseObject object;
            try {
                object = objectParseQuery.find().get(0);
                deleteAllCommentForWork(object);

                ParseObject project = object.getParseObject("Project");
                project.increment("Work", -1);
                project.increment("Member", -(object.getList("ListMember").size() - 1));
                project.saveInBackground();

                List<String> listMember = object.getList("ListMember");
                listMember.remove(ParseUser.getCurrentUser().getEmail());

                Collection<String> list_member = listMember;
                ParseQuery pushQuery = ParseInstallation.getQuery();
                pushQuery.whereContainedIn("UserEmail", list_member);

                JSONObject data = new JSONObject();
                try {
                    data.put("title", "Delete");
                    data.put("alert", "I delete a task");
                    data.put("id", project.getObjectId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ParsePush push = new ParsePush();
                push.setData(data);
                push.setQuery(pushQuery);
                push.sendInBackground();

                object.deleteInBackground();

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Deleting...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

    }

}

