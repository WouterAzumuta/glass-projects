package azumuta.wouterrappe.workinstructions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkInstruction implements Serializable {
    private String title;
    private ArrayList<Integer> images;

    public WorkInstruction(String title) {
        this.title = title;
        this.images = new ArrayList<Integer>();
    }

    public WorkInstruction(String title, Integer... images) {
        this.title = title;
        this.images = new ArrayList<Integer>();
        this.images.addAll(Arrays.asList(images));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addImage(Integer imageID) {
        images.add(imageID);
    }

    public ArrayList<Integer> getImages() {
        return images;
    }
}
