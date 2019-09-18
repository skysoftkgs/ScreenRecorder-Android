package sim.ami.com.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private Context context;

    String[] tabNames = {"Videos", "Settings"};
    int[] activeTabIcons = {R.drawable.ic_tab_gallery_active, R.drawable.ic_tab_setting_active};
    int[] inActiveTabIcons = {R.drawable.ic_tab_gallery_inactive, R.drawable.ic_tab_setting_inactive};

    TabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
//        mFragmentTitleList.add(title);
//        mFragmentIconList.add(tabIcon);
    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tabTextView = (TextView) view.findViewById(R.id.textView_name);
        tabTextView.setText(tabNames[position]);
        tabTextView.setTextColor(context.getResources().getColor(R.color.inActiveTextColor));
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Book.otf");
        tabTextView.setTypeface(custom_font);

        ImageView tabImageView = (ImageView) view.findViewById(R.id.imageView_icon);
        tabImageView.setImageResource(inActiveTabIcons[position]);
        return view;
    }
    public View getSelectedTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_tab, null);
        TextView tabTextView = (TextView) view.findViewById(R.id.textView_name);
        tabTextView.setText(tabNames[position]);
        tabTextView.setTextColor(context.getResources().getColor(R.color.selectedColor));
        Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/GothamRounded-Book.otf");
        tabTextView.setTypeface(custom_font);

        ImageView tabImageView = (ImageView) view.findViewById(R.id.imageView_icon);
        tabImageView.setImageResource(activeTabIcons[position]);
        return view;
    }
}
