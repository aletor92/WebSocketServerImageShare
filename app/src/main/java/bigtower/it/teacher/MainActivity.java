package bigtower.it.teacher;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bigtower.it.teacher.bigtower.it.teacher.fragment.CarouselFragment;
import bigtower.it.teacher.bigtower.it.teacher.fragment.ConnectionFragment;
import bigtower.it.teacher.utils.WebSocketUtil;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectionFragment.OnFragmentInteractionListener,CarouselFragment.CarouselInterfaceListener {

    FilePickerDialog dialog;
    List<String> imagePaths =  new ArrayList<String>();
    Fragment fragment;
    Fragment[] fragmentsArray = new Fragment[2];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebSocketUtil.getInstance().initWss();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.rl, ConnectionFragment.newInstance()).disallowAddToBackStack().commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public static boolean isFragmentInBackstack(final FragmentManager fragmentManager, final String fragmentTagName) {
        for (int entry = 0; entry < fragmentManager.getBackStackEntryCount(); entry++) {
            if (fragmentTagName.equals(fragmentManager.getBackStackEntryAt(entry).getName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.connection) {
            if(fragmentsArray[0] == null){
                fragment = ConnectionFragment.newInstance();
                fragmentsArray[0] = fragment;
            }else{
                fragment = fragmentsArray[0];
            }
        } else if (id == R.id.carousel) {
            if(fragmentsArray[1] == null){
                fragment = CarouselFragment.newInstance();
                fragmentsArray[1] = fragment;
            }else{
                fragment = fragmentsArray[1];
            }
        }


        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(isFragmentInBackstack(getFragmentManager(), item.getTitle().toString())){
            getFragmentManager().popBackStack(item.getTitle().toString(),0);
        }else {
            ft.replace(R.id.rl, fragment).addToBackStack(item.getTitle().toString()).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getListFiles() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"png", "jpg"};
        dialog = new FilePickerDialog(this,properties);
        dialog.setTitle("Select a Directory");
        final List<String> filesArray = new ArrayList<String>();
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] dir) {
                int counter = 0;
                List<File> images = Arrays.asList(new File(dir[0]).listFiles());
                for (File file: images) {
                    if((getExtension(file).contains("jpg") || getExtension(file).contains("png")) && !
                            file.isDirectory()) {
                        if(counter<300) {
                            imagePaths.add(file.getAbsolutePath());
                            counter++;
                        }
                    }
                }
                ((CarouselFragment)fragment).initCarousel(imagePaths);
            }

        });
        dialog.show();
    }

    public static String getExtension(File f) {
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            s = s.substring(i+1).toLowerCase();
        }
        return s;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(dialog!=null)
                    {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }else{
                        Toast.makeText(this,"DIAlOG NULL", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(this,"Permission is Required for getting list of files",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void getListFilesFromActivity() {
        getListFiles();

    }

}
