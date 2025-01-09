package artGallery;

public class Exhibition {
    private int id;
    private String title;
    private String exhibitName;
    private String artist;

    public Exhibition(int id, String title, String exhibitName, String artist) {
        this.id = id;
        this.title = title;
        this.exhibitName = exhibitName;
        this.artist = artist;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getExhibitName() {
        return exhibitName;
    }

    public String getArtist() {
        return artist;
    }
}
