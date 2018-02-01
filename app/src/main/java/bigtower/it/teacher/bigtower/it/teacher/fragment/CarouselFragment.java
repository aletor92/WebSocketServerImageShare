package bigtower.it.teacher.bigtower.it.teacher.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ImageListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bigtower.it.teacher.MainActivity;
import bigtower.it.teacher.R;
import bigtower.it.teacher.utils.WebSocketUtil;


public class CarouselFragment extends Fragment {

    CarouselView carouselView;
    FilePickerDialog dialog;
    View v;
    private CarouselInterfaceListener mListener;

    private static CarouselFragment instance = null;

    public CarouselFragment() {
    }

    public static CarouselFragment newInstance() {
        instance = new CarouselFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mListener != null)
            mListener.getListFilesFromActivity();
        Log.d("STATE", "create");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(v == null)
            v = inflater.inflate(R.layout.fragment_carousel, container, false);
        Log.d("STATE", "createView");
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CarouselInterfaceListener) {
            mListener = (CarouselInterfaceListener) context;


        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    public void initCarousel(final List<String> files) {
        // GET IMAGE FROM INTERNAL STORAGE
        carouselView = (CarouselView) v.findViewById(R.id.carouselView);
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap myBitmap = BitmapFactory.decodeFile(files.get(position), options);
                imageView.setImageBitmap(myBitmap);
                imageView.setAdjustViewBounds(true);

            }
        });
        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(getActivity(), "Clicked item: "+ position, Toast.LENGTH_SHORT).show();

            }
        });
        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Bitmap bm = BitmapFactory.decodeFile(files.get(position));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encodedString = Base64.encodeToString(b, Base64.NO_WRAP);
                int base64Length = encodedString.length();
                int counter = 0;
                WebSocketUtil.getInstance().broadcastMessage("start");
                while(counter < base64Length){
                    WebSocketUtil.getInstance().broadcastMessage(
                            encodedString.substring(counter,
                                    (counter + 10000) > base64Length ?
                                            base64Length :
                                            (counter + 10000)
                            )
                    );
                    counter += 10000;
                }
                WebSocketUtil.getInstance().broadcastMessage("end");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });
        carouselView.setPageCount(files.size());
        carouselView.setSlideInterval(0);

    }


    public interface CarouselInterfaceListener {

        void getListFilesFromActivity();
    }

}
