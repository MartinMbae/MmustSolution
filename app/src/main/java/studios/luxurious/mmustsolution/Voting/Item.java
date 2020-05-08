package studios.luxurious.mmustsolution.Voting;

public class Item {
    String color, text;
    int drawable;


    public Item(String text,int drawable, String color) {
        this.color = color;
        this.text = text;
        this.drawable = drawable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
