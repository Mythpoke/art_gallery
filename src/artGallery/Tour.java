package artGallery;

public class Tour {
    private int id;
    private String guideName;
    private String tourDate;
    private int groupSize;

    public Tour(int id, String guideName, String tourDate, int groupSize) {
        this.id = id;
        this.guideName = guideName;
        this.tourDate = tourDate;
        this.groupSize = groupSize;
    }

    public int getId() {
        return id;
    }

    public String getGuideName() {
        return guideName;
    }

    public String getTourDate() {
        return tourDate;
    }

    public int getGroupSize() {
        return groupSize;
    }
}
