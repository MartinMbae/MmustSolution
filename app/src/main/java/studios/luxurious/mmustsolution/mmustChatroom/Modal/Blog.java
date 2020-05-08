package studios.luxurious.mmustsolution.mmustChatroom.Modal;


public class Blog {

    private String User;
    private String Image;
    private long Time;
    private String Title;
    private String Desc;
    private String Details;
    private String Id;


    public Blog(){}


    public Blog(String user, String image, long time, String title, String desc, String details, String ID) {
        User = user;
        Image = image;
        Time = time;
        Title = title;
        Desc = desc;
        Details = details;
        Id = ID;
    }

    public String getID() {
        return Id;
    }

    public void setID(String ID) {
        Id = ID;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

}
