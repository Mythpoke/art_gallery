package artGallery;

public class Exhibit {
    private int id;
    private String name;
    private String artist;
    private String status;

    public Exhibit(int id, String name, String artist, String status) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getStatus() {
        return status;
    }
}
