package rr.reflexreactor;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ScreenSlideFragment2 extends Fragment {

    Button cat1, cat2, cat3, cat4,cat5,cat6;
    public static final String ARG_PAGE = "page";
    private int pageNum;

    public ScreenSlideFragment2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.screen_slide_fragment2, container, false);
        cat1 = (Button) rootView.findViewById(R.id.catagory1);
        cat2 = (Button) rootView.findViewById(R.id.catagory2);
        cat3 = (Button) rootView.findViewById(R.id.catagory3);
        cat4 = (Button) rootView.findViewById(R.id.catagory4);
        cat5 = (Button) rootView.findViewById(R.id.catagory5);
        cat6 = (Button) rootView.findViewById(R.id.catagory6);
        if (pageNum == 0) {
            cat1.setText("ECONOMICS");
            cat2.setText("GENERAL");
            cat3.setText("MISCELLANEOUS");
            cat4.setText("CRICKET");
            cat5.setText("GEOGRAPHY");
            cat6.setText("MYTHOLOGY");
        } else {
            cat1.setText("HISTORY");
            cat2.setText("INDIAN_CULTURE");
            cat3.setText("MOVIES");
            cat4.setText("SPORTS");
            cat5.setText("POLITICS");
            cat6.setText("SCIENCE");

        }
        return rootView;
    }

    public static ScreenSlideFragment2 create(int pageNumber) {
        ScreenSlideFragment2 fragment = new ScreenSlideFragment2();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNum = getArguments().getInt(ARG_PAGE);
    }

    public int getPageNum() {
        return pageNum;
    }
}
