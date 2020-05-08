package studios.luxurious.mmustsolution.Leisure.LeaderBoard;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LeaderBoardPagerAdapter extends FragmentPagerAdapter {

    private int numberOfTabs;

    public LeaderBoardPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.numberOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LeaderBoardCatchtheBall();
            case 1:
                return new LeaderBoardFlappyBird();
            case 2:
                return new LeaderBoardBaloonPop();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
