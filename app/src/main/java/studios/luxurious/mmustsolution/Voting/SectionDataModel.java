package studios.luxurious.mmustsolution.Voting;

import java.util.ArrayList;


public class SectionDataModel {


    private String headerTitle;
    private ArrayList<Item> allItemsInSection;


    public SectionDataModel() {

    }
    public SectionDataModel(String headerTitle, ArrayList<Item> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<Item> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<Item> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

}
